package utility

import com.novus.salat.Context

/**
 * This Object defines a Salat-Context
 * Salat is used for Marshalling/Unmarshalling of Case-Classes to MongoDBObjects
 * Only one context may exist per app
 * */
object SalatContext {
  val ctx = new Context{
    val name ="Custom_Salat_Context"
  }
}
