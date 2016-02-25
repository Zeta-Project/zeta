package generator.generators.spray

/**
 * Created by julian on 10.02.16.
 */
object BuildZipGenerator {
  def generate =
    """
    <?xml version="1.0" encoding="UTF-8"?>
      <project name="SprayOnline" default="zip" basedir=".">

        <property name="project-name" value="${ant.project.name}" />
        <property name="folder-to-zip" value="diagram" />
        <property name="ecoreSrcDir" value="../../domain/model"/>
        <dirname property="dirnameProject" file="../buildZip.xml"/>
        <property name="projectRoot" value="${dirnameProject}"/>

        <target name="clean">
          <delete file="${project-name}.zip" />
        </target>

        <target name="zip" depends="cpEcore">
          <zip destfile="${project-name}.zip" basedir="${folder-to-zip}" excludes="dont*.*" />
        </target>

        <target name="cpEcore">
          <copy todir="diagram">
            <fileset dir="${projectRoot}.domain/model">
              <include name="*.ecore"/>
            </fileset>
          </copy>
        </target>

      </project>


    """
}
