package controller

trait DomainSpecificLanguage {
  def langForModel(id: String): Unit
}
