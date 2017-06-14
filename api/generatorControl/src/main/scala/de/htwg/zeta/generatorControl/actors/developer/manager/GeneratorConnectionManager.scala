package de.htwg.zeta.generatorControl.actors.developer.manager

import de.htwg.zeta.generatorControl.actors.developer.Event
import de.htwg.zeta.generatorControl.actors.developer.WorkCompleted
import de.htwg.zeta.generatorControl.actors.worker.MasterWorkerProtocol.Work
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import de.htwg.zeta.common.models.frontend.Connected
import de.htwg.zeta.common.models.frontend.Connection
import de.htwg.zeta.common.models.frontend.Disconnected
import de.htwg.zeta.common.models.frontend.FromGenerator
import de.htwg.zeta.common.models.frontend.GeneratorClient
import de.htwg.zeta.common.models.frontend.GeneratorCompleted
import de.htwg.zeta.common.models.frontend.ToGenerator
import de.htwg.zeta.common.models.worker.RunGeneratorFromGeneratorJob

object GeneratorConnectionManager {
  def props() = Props(new GeneratorConnectionManager())
}

class GeneratorConnectionManager() extends Actor with ActorLogging {
  var connections: Map[String, GeneratorClient] = Map()

  def receive = {
    // handle the connections of generators which started another generator
    case connection: Connection => processConnection(connection)
    // streamed a result from a generator, send it to the generator now
    case toGenerator: ToGenerator => streamResultToGenerator(toGenerator)
    // event from the work queue which need to be handled
    case WorkCompleted(work, result) => processWork(work, result)
    case _: Event => // ignore other events
  }

  private def processConnection(connection: Connection) = {
    connection match {
      case Connected(client) => client match {
        case generatorClient @ GeneratorClient(out, id) => {
          connections += (generatorClient.id -> generatorClient)
        }
        case _ =>
      }
      case Disconnected(client) => client match {
        case generatorClient @ GeneratorClient(out, id) => {
          connections -= generatorClient.id
        }
        case _ =>
      }
    }
  }

  private def streamResultToGenerator(toGenerator: ToGenerator) = {
    connections.get(toGenerator.receiver) match {
      case Some(receiver) => {
        receiver.out ! FromGenerator(index = toGenerator.index, key = toGenerator.key, message = toGenerator.message)
      }
      case None => log.error("Receiver of the stream is not available.")
    }
  }

  private def processWork(work: Work, result: Int) = {
    work.job match {
      case job: RunGeneratorFromGeneratorJob => {
        connections.get(job.parentId) match {
          case Some(receiver) => receiver.out ! GeneratorCompleted(job.key, result)
          case None => log.warning("Work {} completed but parent was not available", work)
        }
      }
      case _ => // only if it's the right job type
    }
  }
}
