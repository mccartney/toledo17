import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "pl.waw.oledzki",
      scalaVersion := "2.11.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Toledo17",
    libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.1",
    libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.35.0",
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.2.0",
    libraryDependencies += "io.atlassian.aws-scala" %% "aws-scala-sqs"  % "7.0.6"
  )
