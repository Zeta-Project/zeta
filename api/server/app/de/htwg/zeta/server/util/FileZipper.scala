package de.htwg.zeta.server.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import scala.concurrent.Future

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.StreamConverters
import akka.util.ByteString
import de.htwg.zeta.common.models.entity.File


object FileZipper {

  def zip(files: List[File]): Source[ByteString, Future[IOResult]] = {
    val os = new ByteArrayOutputStream()
    val zip = new ZipOutputStream(os)
    try {
      for {file <- files} {
        zip.putNextEntry(new ZipEntry(s"${file.name}"))
        zip.write(new String(file.content).getBytes)
        zip.closeEntry()
      }
    } finally {
      zip.close()
    }
    val is = new ByteArrayInputStream(os.toByteArray)
    StreamConverters.fromInputStream(() => is)
  }

}
