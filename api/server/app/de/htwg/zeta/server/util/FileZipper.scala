package de.htwg.zeta.server.util

import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import scala.annotation.tailrec
import scala.util.Try

import akka.stream.scaladsl.StreamConverters
import akka.stream.scaladsl.Source
import akka.util.ByteString
import de.htwg.zeta.common.models.entity.File


object FileZipper {

  // http://localhost:9000/rest/v2/models/aedf1d78-21db-48b4-8865-fea445df1b62/downloadSourceCode
  def stream(files: List[File]): Source[ByteString, Unit] = {
    StreamConverters.asOutputStream()
      .mapMaterializedValue(os => {
        val zip = new ZipOutputStream(os)
        try {
          for {file <- files} {
            zip.putNextEntry(new ZipEntry(file.name))
            zip.write("test".map(_.toByte).toArray)
            zip.closeEntry()
          }
        } catch {
          case e: Throwable =>
            println(s"error $e")
        } finally {
          zip.close()
        }
      })
  }

}
