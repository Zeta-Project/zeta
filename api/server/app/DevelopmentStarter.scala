import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URLClassLoader
import java.net.URL

import scala.annotation.tailrec
import scala.collection.mutable
import scala.sys.process.Process

import grizzled.slf4j.Logging

/**
 *
 * DevelopmentStarter will start a new ThreadGroup with a single Thread.
 * This Thread will spawn a Process that will run [[de.htwg.zeta.generatorControl.Main]].
 * <p>
 * Play reload will not stop running Threads. The Thread is encapsulated in a ThreadGroup because a ThreadGroups is easier to find.
 * When the Thread gets interrupted, the process will get destroyed and a new identical process will be spawned.
 * <p>
 * Starting generatorControl via SBT will take quite some time. It is also unnecessary as it will be compiled before that during the play reload.
 * For this reason. java will bes started directly. Because te complete classPath is too long, it is written into a JAR file that is then passed to the process.
 *
 */
class DevelopmentStarter(mode: CustomApplicationLoader.DeploymentMode, contextLoader: ClassLoader) extends Logging {

  info("DevelopmentStarter started")

  private def getParents(cl: ClassLoader): List[ClassLoader] = {
    cl.getParent match {
      case null => List(cl)
      case parent: ClassLoader => cl :: getParents(parent)
    }
  }

  handleDevThread(mode)

  private def handleDevThread(env: CustomApplicationLoader.DeploymentMode): Unit = {
    env match {
      case CustomApplicationLoader.ProdDeployment => // do nothing
      case CustomApplicationLoader.DevDeployment =>
        info("start/restart devThread")
        handleDevThread()
    }
  }

  private def createClassPath(): List[String] = {
    val classLoaders: List[ClassLoader] = getParents(contextLoader)

    val urlSet: mutable.Set[URL] = mutable.Set()
    val urls: mutable.MutableList[String] = mutable.MutableList()

    def addUrls(list: List[URL]): Unit = {
      list.foreach(url => {
        if (urlSet.add(url)) urls += url.getFile else error("duplicate classloader")
      })
    }

    classLoaders.collect {
      case cl: URLClassLoader =>
        addUrls(cl.getURLs.toList)
    }

    val asList = urls.toList

    val genCont = asList.find(_.contains("api/generatorControl")).get // throw exeption if not found

    val filtered = asList.filterNot(filename => {
      filename.contains(DevelopmentStarter.findShared) ||
        filename.contains(DevelopmentStarter.findServer) ||
        filename.contains(DevelopmentStarter.findGenCont)
    })

    genCont :: filtered
  }

  private def buildManifestJar(): File = {

    val classpath = createClassPath()

    val manifestLines = List(
      "Manifest-Version: 1.0",
      s"Class-Path:  ${classpath.mkString("\n  ")}",
      "" // file must end with empty line
    )

    val genControlJar = new File(this.getClass.getResource("").getPath + "startGeneratorControl.jar")
    genControlJar.createNewFile()

    val manifest = new java.util.jar.Manifest(new ByteArrayInputStream(manifestLines.mkString("\n").getBytes("UTF-8")))

    val fOut: FileOutputStream = new FileOutputStream(genControlJar)
    val jarOut = new java.util.jar.JarOutputStream(fOut, manifest)
    jarOut.flush()
    fOut.flush()
    jarOut.close()
    fOut.close()

    genControlJar
  }


  private def createNewDevThread(): Unit = {

    info("creating new Dev Thread")
    val mainClass = "de.htwg.zeta.generatorControl.Main"

    val port = CustomApplicationLoader.devPort
    val seed = s"localhost:$port"

    val manifestJar = buildManifestJar()

    val commandList = List(
      "-classpath",
      manifestJar.getAbsolutePath,
      s"$mainClass",
      s"--master-port $port --master-num 1 --workers 3 --worker-seeds $seed --dev-port 2552 --dev-seeds $seed"
    )

    def buildCommandString(quote: String) = {
      commandList.map(s => {
        if (s.startsWith("-")) {
          s
        } else {
          s"""$quote$s$quote"""
        }
      }).mkString(" ")
    }


    val cmd: String = {
      val home = System.getProperty("java.home")

      if (System.getProperty("os.name").startsWith("Windows")) {
        s""""$home\\bin\\java.exe" """ +
          buildCommandString("\"")
      } else {
        s"""$home/bin/java """ +
          buildCommandString("")
      }
    }

    startThread(cmd)
  }

  private def startThread(commands: String): Unit = {
    val thread = new Thread(new ThreadGroup(clusterGroup), new Runnable {
      @tailrec
      override def run(): Unit = {
        val p = Process(commands).run()
        val restart: Boolean =
          try {
            p.exitValue()
            false
          } catch {
            case _: InterruptedException => true
            case _: Throwable => false
          } finally {
            p.destroy()
          }
        if (restart) {
          info("reloading generatorControl")
          run()
        }
      }
    })

    thread.start()
  }

  private def handleDevThread(): Unit = {

    val groupOpt = getExistingGroup()
    groupOpt match {
      case Some(group) => val threadArr: Array[Thread] = Array.ofDim(group.activeCount() + 10)
        group.enumerate(threadArr, /* recurse = */ true)
        val threadList = threadArr.toList.filterNot(_ == null)
        info("calling interrupt")
        threadList.foreach(_.interrupt())
      case None =>
        createNewDevThread()
    }
  }

  private val clusterGroup = "clusterThreadGroup"

  private def getExistingGroup(): Option[ThreadGroup] = {
    val current = Thread.currentThread().getThreadGroup
    val arr: Array[ThreadGroup] = Array.ofDim(100)
    current.enumerate(arr, /* recurse = */ false)

    val list = arr.toStream.filterNot(_ == null)

    list.find(_.getName == clusterGroup)
  }
}

object DevelopmentStarter {

  private[DevelopmentStarter] val findServer = "api/server"
  private[DevelopmentStarter] val findShared = "api/shared"
  private[DevelopmentStarter] val findGenCont = "api/generatorControl"

  def apply(mode: CustomApplicationLoader.DeploymentMode, classLoader: ClassLoader): DevelopmentStarter = new DevelopmentStarter(mode, classLoader)
}
