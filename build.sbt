name := "mockitoscala-examples-scala"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Ywarn-dead-code")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"     % "3.0.5"  % Test,
  "org.mockito"   %% "mockito-scala" % "1.+"    % Test
)

enablePlugins(JavaAppPackaging)
