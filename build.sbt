import sbt.Keys._

name := "cloud-formation-template-generator"

organization := "com.monsanto.arch"

startYear := Some(2014)

// scala versions and options

scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.11.11", "2.12.4")
releaseCrossBuild := true

// These options will be used for *all* versions.

def crossVersionScalaOptions(scalaVersion: String) = {
   CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 11)) => Seq(
      "-Yclosure-elim",
      "-Yinline"
    )
    case _ => Nil
  }
}
scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-Xlint",
    "-Xverify",
    "-encoding", "UTF-8",
    "-feature",
    "-language:postfixOps"
  ) ++ crossVersionScalaOptions(scalaVersion.value)

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

// dependencies

libraryDependencies ++= Seq (
  // -- testing --
   "org.scalatest"  %% "scalatest"     % "3.0.4"  % Test
  // -- json --
  ,"io.spray"       %%  "spray-json"   % "1.3.4"
  // -- reflection --
  ,"org.scala-lang" %  "scala-reflect" % scalaVersion.value
).map(_.force())

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

// for ghpages

enablePlugins(GhpagesPlugin, SiteScaladocPlugin)

git.remoteRepo := "git@github.com:MonsantoCo/cloudformation-template-generator.git"

licenses += ("BSD", url("http://opensource.org/licenses/BSD-3-Clause"))

publishTo in ThisBuild := Some("maven" at "https://artifactory.nike.com/artifactory/maven")
credentials in ThisBuild += Credentials(realm = "maven", host = "artifactory.nike.com", userName = "maven", passwd = "ludist")
credentials in ThisBuild += Credentials(realm = "Artifactory Realm", host = "artifactory.nike.com", userName = "maven", passwd = "ludist")
val sharedScalacOptions = Seq(
  "-deprecation",
  "-feature",
  "-unchecked"
)
val releaseSettings = Seq(
  resolvers ++= Seq(
    "nike" at "https://artifactory.nike.com/artifactory/all-repos"
  ),
  organization := "com.nike.sim",
  releaseCrossBuild := true,
  scalacOptions ++= sharedScalacOptions ++ Seq("-Xlint", "-Xlint:-adapted-args"),
  scalacOptions in(Compile, console) ++= sharedScalacOptions,
  scalacOptions in(Compile, doc) ++= sharedScalacOptions,
  publishTo := {
    val repo = "https://artifactory.nike.com/artifactory/maven"
    if (isSnapshot.value) {
      Some("snapshots" at s"$repo-snapshots")
    } else {
      Some("releases" at repo)
    }
  },
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
)

