name := "mockitoscala-examples-scala"

version := "0.1"

scalaVersion := "2.13.3"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Ywarn-dead-code")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"     % "3.2.2"  % Test,
  "org.mockito"   %% "mockito-scala" % "1.14.8" % Test
)

enablePlugins(JavaAppPackaging)
