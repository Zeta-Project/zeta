package actors.developer.manager

import actors.developer.WorkState
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import models.frontend._
import models.worker._

object GeneratorConnectionManager {
  def props() = Props(new GeneratorConnectionManager())
}

class GeneratorConnectionManager() extends Actor with ActorLogging {
  var connections: Map[String, GeneratorClient] = Map()

  def receive = {
    // handle the connections of generators which started another generator
    case connection: Connection => connection match {
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
    // streamed a result from a generator, send it to the generator now
    case toGenerator: ToGenerator => connections.get(toGenerator.receiver) match {
      case Some(receiver) => {
        receiver.out ! FromGenerator(index = toGenerator.index, key = toGenerator.key, message = toGenerator.message)
      }
      case None => log.error("Receiver of the stream is not available.")
    }

    // event from the work queue which need to be handled
    case WorkState.WorkCompleted(work, result) =>
      work.job match {
        case job: RunGeneratorFromGeneratorJob => {
          connections.get(job.parentId) match {
            case Some(receiver) => receiver.out ! GeneratorCompleted(job.key, result)
            case None => log.warning("Work {} completed but parent was not available", work)
          }
        }
        case _ => // only if it's the right job type
      }

    case _: WorkState.Event => // ignore other events
  }
}
