package models

object AppConfig {
  val MONGO_URL: String = "141.37.31.44"
  val MONGO_PORT: Int = 27017
  val MONGO_DB_USER: String = "root"
  val MONGO_DB_PWD: String = "root"
  val MONGO_DB_NAME: String = "modigen_v3"
  val MONGO_USER_DB_NAME: String = "Users"
  val MONGO_TOKENS_DB_NAME: String = "Tokens"

  val MODEL_SPECIFIC_JS_PATH: String = System.getProperty("user.dir") + "/public/javascripts/editor/modelSpecific"
}
