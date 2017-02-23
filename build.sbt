import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "pl.waw.oledzki",
      scalaVersion := "2.11.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    mainClass in Compile := Some("toledo17.Toledo17Visitors"),
    name := "Toledo17",
    libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.1",
    libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.35.0",
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.2.0",
    libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.46"
  )
