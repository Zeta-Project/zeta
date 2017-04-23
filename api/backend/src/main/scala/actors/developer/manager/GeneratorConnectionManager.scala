package actors.developer.manager

import actors.developer.Event
import actors.developer.WorkCompleted
import actors.worker.MasterWorkerProtocol.Work
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import models.frontend.Connected
import models.frontend.Connection
import models.frontend.Disconnected
import models.frontend.FromGenerator
import models.frontend.GeneratorClient
import models.frontend.GeneratorCompleted
import models.frontend.ToGenerator
import models.worker.RunGeneratorFromGeneratorJob

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
