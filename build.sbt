name := "abrakkadabra"
 
version := "1.0" 
      
lazy val `abrakkadabra` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.6"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.18"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.6.0"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.6.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.4"
// https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.4" % "runtime"
// https://mvnrepository.com/artifact/io.netty/netty-all
//libraryDependencies += "io.netty" %  "netty-all" % "4.1.17.Final"


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )




      