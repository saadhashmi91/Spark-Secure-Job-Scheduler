name := "sparkjobservicetest"

version := "0.1"

scalaVersion := "2.11.12"


compileOrder in Compile := CompileOrder.Mixed

// https://mvnrepository.com/artifact/org.quartz-scheduler/quartz
libraryDependencies += "org.quartz-scheduler" % "quartz" % "2.2.1"

// https://mvnrepository.com/artifact/commons-io/commons-io
libraryDependencies += "commons-io" % "commons-io" % "2.6"


// https://mvnrepository.com/artifact/org.mapdb/mapdb
libraryDependencies += "org.mapdb" % "mapdb" % "3.0.5"

// https://mvnrepository.com/artifact/commons-cli/commons-cli
libraryDependencies += "commons-cli" % "commons-cli" % "1.4"



assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
        