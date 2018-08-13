package com.veon.rafm.simbox.scheduler.utils

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


class SparkProcessBuilder() extends Logging {

  private[this] var _executable: String = "/usr/bin/spark-submit";//livyConf.sparkSubmit()
  private[this] var _master: Option[String] = Option("yarn")
  private[this] var _deployMode: Option[String] = None
  private[this] var _className: Option[String] = None
  private[this] var _name: Option[String] = Some("Livy")
  private[this] val _conf = mutable.HashMap[String, String]()
  private[this] var _driverClassPath: ArrayBuffer[String] = ArrayBuffer()
  private[this] var _proxyUser: Option[String] = None
  private[this] var _queue: Option[String] = None
  private[this] var _env: ArrayBuffer[(String, String)] = ArrayBuffer()
  private[this] var _redirectOutput: Option[ProcessBuilder.Redirect] = None
  private[this] var _redirectError: Option[ProcessBuilder.Redirect] = None
  private[this] var _redirectErrorStream: Option[Boolean] = None

  def executable(executable: String): SparkProcessBuilder = {
    _executable = executable
    this
  }

  def master(masterUrl: String): SparkProcessBuilder = {
    _master = Some(masterUrl)
    this
  }

  def deployMode(deployMode: String): SparkProcessBuilder = {
    _deployMode = Some(deployMode)
    this
  }

  def className(className: String): SparkProcessBuilder = {
    _className = Some(className)
    this
  }

  def name(name: String): SparkProcessBuilder = {
    _name = Some(name)
    this
  }

  def conf(key: String): Option[String] = {
    _conf.get(key)
  }

  def conf(key: String, value: String, admin: Boolean = false): SparkProcessBuilder = {
    this._conf(key) = value
    this
  }

  def conf(conf: Traversable[(String, String)]): SparkProcessBuilder = {
    conf.foreach { case (key, value) => this.conf(key, value) }
    this
  }

  def driverJavaOptions(driverJavaOptions: String): SparkProcessBuilder = {
    conf("spark.driver.extraJavaOptions", driverJavaOptions)
  }

  def driverClassPath(classPath: String): SparkProcessBuilder = {
    _driverClassPath += classPath
    this
  }

  def driverClassPaths(classPaths: Traversable[String]): SparkProcessBuilder = {
    _driverClassPath ++= classPaths
    this
  }

  def driverCores(driverCores: Int): SparkProcessBuilder = {
    this.driverCores(driverCores.toString)
  }

  def driverMemory(driverMemory: String): SparkProcessBuilder = {
    conf("spark.driver.memory", driverMemory)
  }

  def driverCores(driverCores: String): SparkProcessBuilder = {
    conf("spark.driver.cores", driverCores)
  }

  def executorCores(executorCores: Int): SparkProcessBuilder = {
    this.executorCores(executorCores.toString)
  }

  def executorCores(executorCores: String): SparkProcessBuilder = {
    conf("spark.executor.cores", executorCores)
  }

  def executorMemory(executorMemory: String): SparkProcessBuilder = {
    conf("spark.executor.memory", executorMemory)
  }

  def numExecutors(numExecutors: Int): SparkProcessBuilder = {
    this.numExecutors(numExecutors.toString)
  }

  def numExecutors(numExecutors: String): SparkProcessBuilder = {
    this.conf("spark.executor.instances", numExecutors)
  }

  def proxyUser(proxyUser: String): SparkProcessBuilder = {
    _proxyUser = Some(proxyUser)
    this
  }

  def queue(queue: String): SparkProcessBuilder = {
    _queue = Some(queue)
    this
  }

  def env(key: String, value: String): SparkProcessBuilder = {
    _env += ((key, value))
    this
  }

  def redirectOutput(redirect: ProcessBuilder.Redirect): SparkProcessBuilder = {
    _redirectOutput = Some(redirect)
    this
  }

  def redirectError(redirect: ProcessBuilder.Redirect): SparkProcessBuilder = {
    _redirectError = Some(redirect)
    this
  }

  def redirectErrorStream(redirect: Boolean): SparkProcessBuilder = {
    _redirectErrorStream = Some(redirect)
    this
  }

  def start(file: Option[String], args: Traversable[String]): LineBufferedProcess = {
    var arguments = ArrayBuffer(_executable)

    def addOpt(option: String, value: Option[String]): Unit = {
      value.foreach { v =>
        arguments += option
        arguments += v
      }
    }

    def addList(option: String, values: Traversable[String]): Unit = {
      if (values.nonEmpty) {
        arguments += option
        arguments += values.mkString(",")
      }
    }

    addOpt("--master", _master)
   // addOpt("--deploy-mode", _deployMode)
    addOpt("--name", _name)
    addOpt("--class", _className)
    _conf.foreach { case (key, value) =>
      arguments += "--conf"
      arguments += f"$key=$value"
    }
    addList("--driver-class-path", _driverClassPath)



   // addOpt("--queue", _queue)

    arguments += file.getOrElse("spark-internal")
    arguments ++= args

    val argsString = arguments
      .map("'" + _.replace("'", "\\'") + "'")
      .mkString(" ")

    info(s"Running $argsString")

    val pb = new ProcessBuilder(arguments.asJava)
    val env = pb.environment()

    for ((key, value) <- _env) {
      env.put(key, value)
    }

    _redirectOutput.foreach(pb.redirectOutput)
    _redirectError.foreach(pb.redirectError)
    _redirectErrorStream.foreach(pb.redirectErrorStream)

    new LineBufferedProcess(pb.start())
  }

}