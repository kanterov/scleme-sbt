import sbt._
import Keys._

object SclemeSbtBuild extends Build {
    lazy val sclemeSbt = Project(
        id = "scleme-sbt",
        base = file("."),

        settings = Defaults.defaultSettings ++ Seq(
            organization := "com.github.kanterov",
            version := "0.1-SNAPSHOT",
            sbtPlugin := true,
            
            publishTo <<= (version) { (v: String) =>
                val repoSuffix = if (v.contains("-SNAPSHOT")) "snapshots" else "releases"
                val resolver = Resolver.file("gh-pages",
                    new File("/Users/kanterov/git/kanterov.github.com/repo", repoSuffix))
                Some(resolver)
            }
        )
    )

    libraryDependencies ++= Seq(
        "org.scalaz" %% "scalaz-core" % "6.0.3")

    resolvers += "Local Snapshots" at "file:///Users/kanterov/git/kanterov.github.com/repo/snapshots"
}
