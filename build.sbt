name := "akka-http-scala-spontad"
organization := "com.clovisr"
version := "1.0"
scalaVersion := "2.11.8"

scalacOptions := Seq("-encoding", "utf8")

libraryDependencies ++= {
  val akkaV       = "2.4.12"
  val akkaHttpV   = "10.0.1"
  val scalaTestV  = "3.0.1"
  val mysqlV      = "5.1.16"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
    "mysql" % "mysql-connector-java" % mysqlV
  )
}
