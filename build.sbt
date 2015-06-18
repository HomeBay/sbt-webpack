sbtPlugin := true

organization := "com.homebay.sbt"

name := "sbt-webpack"

version := "0.0.3-SNAPSHOT"

scalaVersion := "2.10.4"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

// using package.json dependencies for now because webpack has a TON of transitive dependencies that are not represented
// libraryDependencies ++= Seq(
//   "org.webjars" % "webpack" % "1.5.3"
// )

resolvers ++= Seq(
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.url("sbt snapshot plugins", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns),
  Resolver.sonatypeRepo("snapshots"),
  "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/",
  Resolver.mavenLocal
)

addSbtPlugin("com.typesafe.sbt" %% "sbt-js-engine" % "1.0.2")

scriptedSettings

scriptedLaunchOpts <+= version apply { v => s"-Dproject.version=$v" }


publishTo := Some("HomeBay Artifactory Repo" at "http://zulli.artifactoryonline.com/zulli/plugins-snapshots-local")
