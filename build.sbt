name := "scleme-sbt"

version := "0.1-SNAPSHOT"

organization := "com.github.kanterov"

scalaVersion := "2.9.2"

sbtPlugin := true

resolvers ++= Seq(
    "snapshots" at "http://scala-tools.org/repo-snapshots", 
    "releases"  at "http://scala-tools.org/repo-releases",
    "sonatype" at "https://oss.sonatype.org/content/groups/public")

if (java.net.InetAddress.getLocalHost.getHostName == "Glebs-MacBook-Air.local") {
    resolvers += "local" at "file:///Users/kanterov/git/kanterov.github.com/repo/snapshots"
} else {
    resolvers += "github" at "http://kanterov.github.com/repo/snapshots"
}

libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % "6.0.3",
    "com.github.kanterov" %% "scleme" % "0.1-SNAPSHOT")

