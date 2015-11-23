name := "akka_lab"

version := "1.0"

scalaVersion := "2.11.7"

fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.0",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % "test",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.0",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
)
