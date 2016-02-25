package generator.generators.spray

import java.sql.Timestamp

import generator.model.diagram.Diagram

/**
 * Created by julian on 10.02.16.
 */

@deprecated
object ReadmeGenerator {
  val date = new java.util.Date()
  def generate( diagram:Diagram)= {
    s"""
    <project>
      <projectInformation>
        <name>SprayOnline</name>
        <created>${new Timestamp(date.getTime)}</created>

      </projectInformation>
      <diagram>
        <name>${diagram.name}</name>
      </diagram>
    </project>
    """
  }

}
