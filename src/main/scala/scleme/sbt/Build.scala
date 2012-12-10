import sbt._
import Keys._
import scleme.Scleme
import scalaz.{ Failure, Success }

object SclemeSbtPlugin extends Plugin {

  val codeTemplate =
    """/* THIS IS GENERATED CODE */
{{packageDef}}object {{objectName}} {
  {{innerCode}}
}
"""

  override lazy val settings = Seq[Setting[_]](
    (sourceGenerators in Compile) <+= runGenerators)

  private def runGenerators =
    (streams,
      sourceManaged in Compile,
      sourceDirectory in Compile) map { (out, targetDir, srcDir) =>

        import scleme._

        val sclemeDir = srcDir / "scleme"
        val files = (sclemeDir ** ("*" + ".scleme")).get
        val log = out.log

        log.info("[scleme] Code generation started")

        val coreFile = targetDir / Scleme.corePath.stripSuffix("/")
        IO.write(coreFile, Scleme.core)

        val scalaSources = files flatMap { file =>
          val relative = file relativeTo sclemeDir
          val path = relative.get.getPath

          if (!path.endsWith(".scleme")) List()
          else {
            log.info("[scleme] '%s' detected.".format(path))

            val scalaPath = path.replace(".scleme", ".scleme.g.scala")
            val scalaSource = targetDir / scalaPath
            val parent = Option(relative.get.getParent) getOrElse ""
            val packageName = parent.replace("/", ".")
            val objectName = relative.get.base

            log.debug("[scleme] Generating '%s' into '%s'.".format(path, scalaSource))
            log.debug("[scleme] package='%s', object='%s'".format(packageName, objectName))

            val sclemeCode = IO.read(file)
            val scalaCode = generateScalaCode(packageName, objectName, sclemeCode)

            log.debug("[scleme] Scleme Code: \n%s".format(sclemeCode))
            log.debug("[scleme] Scala Code: \n%s".format(scalaCode))

            IO.write(scalaSource, scalaCode)

            List(scalaSource)
          }
        }

        scalaSources ++ List(coreFile)
      }

  def generateScalaCode(packageName: String, objectName: String, sclemeCode: String): String = {
    val validation = Scleme(
      packageName = packageName,
      objectName = objectName,
      input = sclemeCode)

    validation match {
      case Success(scalaCode) => scalaCode
      case Failure(msg) => throw new RuntimeException(msg)
    }
  }

}
