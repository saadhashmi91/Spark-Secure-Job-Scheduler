package com.veon.rafm.simbox.scheduler.batch

import java.lang.ProcessBuilder.Redirect
import java.net.{URI, URISyntaxException}

import com.veon.rafm.simbox.scheduler.utils.{Conf, SparkProcessBuilder}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

//,val livyConf: Conf
class BatchSession(request: CreateBatchRequest)
   {

  private val process = {
    val conf = prepareConf(request.conf, request.jars, request.files, request.archives,
      request.pyFiles)
    //require(request.file != null, "File is required.")

    val builder = new SparkProcessBuilder()

    request.className.foreach(builder.className)
    request.driverMemory.foreach(builder.driverMemory)
    request.driverCores.foreach(builder.driverCores)
    request.executorMemory.foreach(builder.executorMemory)
    request.executorCores.foreach(builder.executorCores)
    request.numExecutors.foreach(builder.numExecutors)
    request.queue.foreach(builder.queue)
    request.name.foreach(builder.name)
    builder.redirectOutput(Redirect.PIPE)
    builder.redirectErrorStream(true)

  //  val file = resolveURIs(Seq(request.file))(0)
    builder.start(Option(request.jars(0)),request.args)
  }

     /**
       * Validates and prepares a user-provided configuration for submission.
       *
       * - Verifies that no blacklisted configurations are provided.
       * - Merges file lists in the configuration with the explicit lists provided in the request
       * - Resolve file URIs to make sure they reference the default FS
       * - Verify that file URIs don't reference non-whitelisted local resources
       */
     protected def prepareConf(conf: Map[String, String],
                               jars: Seq[String],
                               files: Seq[String],
                               archives: Seq[String],
                               pyFiles: Seq[String]): Map[String, String] = {
       if (conf == null) {
         return Map()
       }

     /*  val errors = conf.keySet.filter(configBlackList.contains)
       if (errors.nonEmpty) {
         throw new IllegalArgumentException(
           "Blacklisted configuration values in session config: " + errors.mkString(", "))
       }
*/
      /* val confLists: Map[String, Seq[String]] = livyConf.sparkFileLists
         .map { key => (key -> Nil) }.toMap
        */
       val userLists =  Map(
         Conf.SPARK_JARS -> jars,
         Conf.SPARK_FILES -> files,
         Conf.SPARK_ARCHIVES -> archives,
         Conf.SPARK_PY_FILES -> pyFiles)

       val merged = userLists.flatMap { case (key, list) =>
         /*val confList = conf.get(key)
           .map { list =>
             resolveURIs(list.split("[, ]+").toSeq)
           }
           .getOrElse(Nil)
           */
         val userList = resolveURIs(list)
         if ( userList.nonEmpty) {
           Some(key -> (userList).mkString(","))
         } else {
           None
         }
       }

       conf ++ merged
     }




  protected implicit def executor: ExecutionContextExecutor = ExecutionContext.global



   def logLines(): IndexedSeq[String] = process.inputLines

   def stopSession(): Unit = destroyProcess()

  private def destroyProcess() = {
    if (process.isAlive) {
      process.destroy()
      reapProcess(process.waitFor())
    }
  }

  private def reapProcess(exitCode: Int) = synchronized {
  /*  if (_state.isActive) {
      if (exitCode == 0) {
        _state = SessionState.Success()
      } else {
        _state = SessionState.Error()
      }
    }*/
  }



     /**
       * Prepends the value of the "fs.defaultFS" configuration to any URIs that do not have a
       * scheme. URIs are required to at least be absolute paths.
       *
       * @throws IllegalArgumentException If an invalid URI is found in the given list.
       */
     protected def resolveURIs(uris: Seq[String]): Seq[String] = {
     //  val defaultFS = livyConf.hadoopConf.get("fs.defaultFS").stripSuffix("/")
       uris.filter(_.nonEmpty).map { _uri =>
         val uri = try {
           new URI(_uri)
         } catch {
           case e: URISyntaxException => throw new IllegalArgumentException(e)
         }
         resolveURI(uri).toString()
       }
     }

     protected def resolveURI(uri: URI): URI = {
     //  val defaultFS = livyConf.hadoopConf.get("fs.defaultFS").stripSuffix("/")
     /*  val resolved =
         if (uri.getScheme() == null) {
           require(uri.getPath().startsWith("/"), s"Path '${uri.getPath()}' is not absolute.")
           new URI(uri.getPath())
         } else {
           uri
         }
*/
  //     if (resolved.getScheme() == "file") {
         // Make sure the location is whitelisted before allowing local files to be added.
        // require(livyConf.localFsWhitelist.find(resolved.getPath().startsWith).isDefined,
          // s"Local path ${uri.getPath()} cannot be added to user sessions.")
    //   }
       uri

       //resolved
     }


     /** Simple daemon thread to make sure we change state when the process exits. */
  private[this] val thread = new Thread("Batch Process Reaper") {
    override def run(): Unit = {
      reapProcess(process.waitFor())
    }
  }
  thread.setDaemon(true)
  thread.start()
}