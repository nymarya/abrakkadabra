name := "abrakkadabra"
 
version := "1.0" 
      
lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.11.12"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

//libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.0"


//libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.6"
val akkaVersion = "2.5.21"
//
//// Akka dependencies used by Play
libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-actor"  % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"  % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,

  "org.scalanlp" %% "breeze" % "0.12",
)

//libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.6.0"
//libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.6.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.4"
//// https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
//libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.4.4"
// https://mvnrepository.com/artifact/io.netty/netty-all
//libraryDependencies += "io.netty" %  "netty-all" % "4.1.17.Final"


//unmanagedResourceDirectories in Test +=  {baseDirectory ( _ /"target/web/public/test" )}




      