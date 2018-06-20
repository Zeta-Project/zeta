package de.htwg.zeta.server.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import de.htwg.zeta.common.models.entity.File


object FileZipper {

  def zip(files: List[File]): ByteArrayInputStream = {
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
    new ByteArrayInputStream(os.toByteArray)
  }

}
