package de.htwg.zeta.codeGenerator


import java.util.UUID

import scala.annotation.tailrec
import scala.collection.immutable.Seq
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import de.htwg.zeta.codeGenerator.generation.KlimaCodeGenerator
import de.htwg.zeta.codeGenerator.model.Anchor
import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.GeneratedFolder
import de.htwg.zeta.codeGenerator.model.Link
import de.htwg.zeta.codeGenerator.model.MapLink
import de.htwg.zeta.codeGenerator.model.ReferenceLink
import de.htwg.zeta.codeGenerator.model.Value
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.HasAttributeValues
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import grizzled.slf4j.Logging

// scalastyle:off
object GdslInstanceToZetaModel extends Logging {

  val noUniquePath = "fail"

  private case class GDSLState(
      nodes: Map[String, NodeInstance],
      edges: Map[String, EdgeInstance],
      entityIdCache: mutable.Map[String, Entity],
      entityNameCache: mutable.Map[String, Entity],
      nodePath: mutable.Map[String, String]
  )

  def generate(concept: Concept, gdslInstance: GraphicalDslInstance): Either[String, List[File]] = {

    for {
      state <- buildState(gdslInstance)
      first <- buildOptAnchor(gdslInstance, state).toRight("failed to extract Anchor")
      team <- updateReferences(first.team, state)
      period <- updateReferences(first.period, state)
    } yield {
      val anchor = first.copy(team = team, period = period)
      val generated = KlimaCodeGenerator.generate(anchor, "de", "htwg")
      transformGeneratedFolder(gdslInstance.id, generated, "")
    }

  }

  private def buildState(gdslInstance: GraphicalDslInstance): Either[String, GDSLState] = {

    def extractName(ha: HasAttributeValues): Option[String] = ha.attributeValues.get("name").flatMap(extractOneStringElem)
    def handleDuplicates[R](list: Seq[String], name: String)(right: => R): Either[String, R] = {
      val duplicates = list.diff(list.distinct).distinct
      if (duplicates.isEmpty) Right(right) else Left(s"duplicate $name: [${duplicates.mkString(", ")}]")
    }

    for {
      namedPairs <- mapAllOrNone(gdslInstance.nodes)(n => extractName(n).map(n.name -> _)).toRight("node without Name")
      // TODO check valid name
      allNames = namedPairs.map(_._2)
      idToName <- handleDuplicates(allNames, "nodes")(namedPairs.toMap)
      // only look at edges with names and concatenate them with the sourceNode to find distinct names per sourceNode
      allEdges = gdslInstance.edges.flatMap(e => extractName(e).map(en => s"${idToName(e.sourceNodeName)}.$en"))
      _ <- handleDuplicates(allEdges, "edges")(())
    } yield {
      GDSLState(
        gdslInstance.nodes.map(n => n.name -> n).toMap,
        gdslInstance.edges.map(e => e.name -> e).toMap,
        mutable.Map(),
        mutable.Map(),
        mutable.Map()
      )
    }
  }

  private def buildOptAnchor(gdslInstance: GraphicalDslInstance, state: GDSLState): Option[Anchor] = {
    for {
      teamAnch <- gdslInstance.nodes.find("TeamAnchor" == _.className)
      periodAnch <- gdslInstance.nodes.find("PeriodAnchor" == _.className)
      teamOutId <- expectOneElem(teamAnch.outputEdgeNames)
      periodOutId <- expectOneElem(periodAnch.outputEdgeNames)
      teamOut <- state.edges.get(teamOutId)
      periodOut <- state.edges.get(periodOutId)
      teamNode <- state.nodes.get(teamOut.targetNodeName)
      periodNode: NodeInstance <- state.nodes.get(periodOut.targetNodeName)
      teamEntity <- extractEntity(teamNode, state, "team")
      periodEntity <- extractEntity(periodNode, state, "period")
    } yield {
      model.Anchor("klima", teamEntity, periodEntity)
    }
  }

  private def updateReferences(entity: Entity, state: GDSLState): Either[String, Entity] = {
    def updateRefs(list: List[ReferenceLink]): Either[String, List[ReferenceLink]] = {
      updateEitherList(list) { ref =>
        // get path for ref => actual update here
        state.nodePath.get(ref.entityPath)
          .toRight(s"no Path for ${entity.name}.${ref.name}")
          .filterOrElse(_ != noUniquePath, s"no unique path for ${entity.name}.${ref.name}")
      }((ref, newPath) => ref.copy(entityPath = newPath))
    }

    state.entityNameCache.get(entity.name) match {
      case Some(updated) => Right(updated)
      case None =>
        for {
          links <- updateEitherList(entity.links)(l => updateReferences(l.entity, state))((l, e) => l.copy(entity = e))
          maps <- updateEitherList(entity.maps)(m => updateReferences(m.entity, state))((m, e) => m.copy(entity = e))
          refs <- updateRefs(entity.refs)
        } yield {
          val updatedEntity = entity.copy(
            links = links,
            maps = maps,
            refs = refs
          )
          state.entityNameCache(entity.name) = updatedEntity
          updatedEntity
        }
    }
  }

  private def transformGeneratedFolder(id: UUID, folder: GeneratedFolder, prefix: String): List[File] = {
    val buff = ListBuffer[File]()
    def rec(current: GeneratedFolder, pre: String): Unit = {
      current.files.foreach { f =>
        val fileName = s"$pre/${f.name}.${f.fileType}"
        val formattedCont = ScalaCodeBeautifier.format(fileName, f.content)
        buff += File(id, fileName, formattedCont)
      }
      current.children.foreach { f =>
        rec(f, s"$pre/${f.name}")
      }
    }
    rec(folder, if (prefix.isEmpty) folder.name else s"$prefix/${folder.name}")
    buff.result()
  }

  private def updateEitherList[L, R](oldList: List[L])(eitherFromL: L => Either[String, R])(update: (L, R) => L): Either[String, List[L]] = {
    val buff = ListBuffer[L]()
    def rec(list: List[L]): Either[String, List[L]] = list match {
      case Nil => Right(buff.result())
      case l :: tail =>
        eitherFromL(l) match {
          case Left(s) => Left(s)
          case Right(newE) =>
            buff += update(l, newE)
            rec(tail)
        }
    }
    rec(oldList)
  }

  private def expectOneElem[E](s: Seq[E]): Option[E] = s.headOption.filter(_ => s.tail.isEmpty) // safe access to tail

  private def extractOneStringElem(s: List[AttributeValue]): Option[String] = {
    expectOneElem(s).collect { case StringValue(n) => n.trim }
  }

  private def mapAllOrNone[E, R](seq: Seq[E])(map: E => Option[R]): Option[List[R]] = {
    val buff = ListBuffer[R]()
    @tailrec def rek(s: List[E]): Option[List[R]] = s match {
      case Nil => Some(buff.result())
      case head :: tail => map(head) match {
        case None => None
        case Some(r) =>
          buff += r
          rek(tail)
      }
    }
    rek(seq.toList)
  }

  private def extractEntity(node: NodeInstance, state: GDSLState, path: String): Option[Entity] = {
    def create = for {
      name <- node.attributeValues.get("name").flatMap(extractOneStringElem)
      edges <- mapAllOrNone(node.outputEdgeNames)(en => state.edges.get(en))
      // names are from shape dsl
      linkRef = edges.filter("Link" == _.referenceName)
      mapRef = edges.filter("Map" == _.referenceName) // this is not yet defined in shape
      refRef = edges.filter("Reference" == _.referenceName)
      links <- mapAllOrNone(linkRef)(lr => extractLink(lr, state, path))
      mapLinks <- mapAllOrNone(mapRef)(lr => extractMapLink(lr, state))
      refLinks <- mapAllOrNone(refRef)(lr => extractReferenceLink(lr, state))
      fixOpt <- node.attributeValues.get("fix")
      inOpt <- node.attributeValues.get("in")
      outOpt <- node.attributeValues.get("out")
      fixValues <- mapAllOrNone(fixOpt)(extractSingleValue)
      inValues <- mapAllOrNone(inOpt)(extractSingleValue)
      outValues <- mapAllOrNone(outOpt)(extractSingleValue)
    } yield {
      val e = Entity(name.trim, fixValues, inValues, outValues, links, mapLinks, refLinks)
      state.entityIdCache(node.name) = e
      e
    }

    state.entityIdCache.get(node.name).orElse(create)
  }

  private def extractLink(edge: EdgeInstance, state: GDSLState, path: String): Option[Link] = {
    for {
      name <- edge.attributeValues.get("name").flatMap(extractOneStringElem)
      node <- state.nodes.get(edge.targetNodeName)
      entityPath = s"$path.$name"
      entity <- extractEntity(node, state, entityPath)
    } yield {
      state.nodePath.get(node.name) match {
        case Some(_ /* existing */) => state.nodePath(node.name) = noUniquePath
        case None => state.nodePath(node.name) = entityPath
      }
      Link(name, entity)
    }
  }

  private def extractMapLink(edge: EdgeInstance, state: GDSLState): Option[MapLink] = {
    for {
      name <- edge.attributeValues.get("name").flatMap(extractOneStringElem)
      node <- state.nodes.get(edge.targetNodeName)
      entity <- extractEntity(node, state, null /* TODO path */)
    } yield {
      MapLink(name, null /* TODO key type*/ , entity)
    }
  }

  private def extractReferenceLink(edge: EdgeInstance, state: GDSLState): Option[ReferenceLink] = {
    for {
      name <- edge.attributeValues.get("name").flatMap(extractOneStringElem)
      node <- state.nodes.get(edge.targetNodeName)
    } yield {
      ReferenceLink(name, node.name) // build path out of this later. see updateReferences
    }
  }

  private def extractSingleValue(at: AttributeValue): Option[Value] = at match {
    case StringValue(combinedString) =>
      combinedString.split(":", 2).toList match {
        case name :: tpe :: Nil => Some(Value(name, tpe))
        case _ => None
      }

    case _ => None
  }
}
