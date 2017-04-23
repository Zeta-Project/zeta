package models.frontend

import akka.actor.ActorRef
import models.document.Settings

/**
 * Represent any message which can be send to the internal of the system
 */
sealed trait Message

sealed trait Client extends Message {
  /**
   * The identifier for the logged in user
   */
  val id: String
  /**
   * The reference to send messages back to the client
   */
  val out: ActorRef
}

/**
 * Represents a developer of a meta model
 *
 * @param out The ActorRef to send messages back to the client
 * @param id The identifier for the logged in user
 */
case class ToolDeveloper(out: ActorRef, id: String) extends Client

/**
 * Represents a user of a model
 *
 * @param out The ActorRef to send messages back to the client
 * @param id The identifier for the logged in user
 * @param model The id of the model
 */
case class ModelUser(out: ActorRef, id: String, model: String) extends Client

/**
 * Represents a connected generator instance
 * A generator can start a generator. Therefore the generator need to connect to the backend to get the
 * result from the generator he had started)
 *
 * @param out The ActorRef to send messages back to the generator
 * @param workId The Work id (where the generator is running)
 */
case class GeneratorClient(out: ActorRef, workId: String) extends Client {
  val id = workId
}

/*
 * Represent a client connection to transport the actor ref of the actor
 * to internals of the system.
 * A Client can be a User or a Developer.
 */
sealed trait Connection extends Message
case class Connected(client: Client) extends Connection
case class Disconnected(client: Client) extends Connection

/**
 * Represent a request from a client to the system
 */
trait Request extends Message

/**
 * Represent a response from the system to a client
 */
trait Response

/**
 * Initialize the developer mediator actor
 *
 * @param developer The developer document
 */
case class Init(developer: Settings) extends Message

/**
 * Wrap the message send from a client to the system in this message.
 * This wrapper contains the id of the internal actor which is a sharded actor
 *
 * @param id The internal actor id
 * @param message The message to send
 */
case class MessageEnvelope(id: String, message: Message)
