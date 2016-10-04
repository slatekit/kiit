/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.info

/**
 * Represents a host such as a cloud server. e.g. ec2
 * @param name       : name of the language
 * @param home       : home directory of the language
 * @param origin     : origin of the language, e.g. for scala -reference to jre
 * @param versionNum : version of the language
 * @param version    : addition info about architechture for lang ( e.g. 64 bit )
 * @param ext1       : additional information about the language
 */
case class Lang(
                  name        : String = "",
                  home        : String = "",
                  versionNum  : String = "",
                  version     : String = "",
                  origin      : String = "",
                  ext1        : String = ""
               )
{

  def log( callback:(String,String) => Unit) : Unit = {
    callback("name"   , name )
    callback("home"     , home )
    callback("versionNum" , versionNum )
    callback("version"   , version )
    callback("origin", origin )
    callback("ext1"   , ext1 )
  }
}


object Lang
{
  val none = new Lang(
    name     = "none",
    home     = "-",
    versionNum = "",
    version  = "-",
    origin   = "local",
    ext1        = "-"
  )


  def asScala(): Lang =
  {
    new Lang(
      name       = "scala",
      home       = scala.util.Properties.javaHome.replaceAllLiterally("\\", "/"),
      versionNum = scala.util.Properties.versionNumberString,
      version    = scala.util.Properties.javaVersion,
      origin     = "local",
      ext1       = ""
    )
  }
}
