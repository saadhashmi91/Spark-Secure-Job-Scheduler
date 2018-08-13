package com.veon.rafm.simbox.scheduler.batch

import java.util

import scala.collection.JavaConverters._

class CreateBatchRequest {


  var args: List[String] = List()
  var className: Option[String] = None
  var jars: List[String] = List()
  var pyFiles: List[String] = List()
  var files: List[String] = List()
  var driverMemory: Option[String] = None
  var driverCores: Option[Int] = None
  var executorMemory: Option[String] = None
  var executorCores: Option[Int] = None
  var numExecutors: Option[Int] = None
  var archives: List[String] = List()
  var queue: Option[String] = None
  var name: Option[String] = None
  var conf: Map[String, String] = Map()

  def setRequestParams(args:util.ArrayList[String], className:String, jars:util.ArrayList[String], files:util.ArrayList[String], driverMemory:String, driverCores:Int,
                       executorMemory:String, executorCores:Int, numExecutors:Int): Unit =
  {

    this.args=args.asScala.toList;
    this.className=Option(className);
    this.jars=jars.asScala.toList;
    this.files=files.asScala.toList;
    this.driverMemory=Option(driverMemory);
    this.driverCores=Option(driverCores);
    this.executorMemory=Option(executorMemory);
    this.executorCores=Option(executorCores);
    this.numExecutors=Option(numExecutors);

  }

}
