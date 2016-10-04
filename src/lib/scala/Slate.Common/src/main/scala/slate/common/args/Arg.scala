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

package slate.common.args

import slate.common.{ConsoleWriter}
import slate.common.StringHelpers.StringExt


/**
 *
 * @param alias         : alias for the argument     ( -e   )
 * @param name          : name of the argument       ( -env )
 * @param desc          : description of argument
 * @param isRequired    : whether arg is required
 * @param isCased       : case sensitive
 * @param isDevOnly     : used for development only
 * @param isInterpreted : if arg can be interpreted ( @date.today )
 * @param group         : used to group args
 * @param tag           : used to tag an arg
 * @param defaultVal    : default value for the arg
 * @param example       : example of an arg value
 * @param exampleMany   : multiple examples of arg values
 */
case class Arg (
                  alias         :String    = "",
                  name          :String    = "",
                  desc          :String    = "",
                  dataType      :String    = "",
                  isRequired    :Boolean   = true,
                  isCased       :Boolean   = true,
                  isDevOnly     :Boolean   = false,
                  isInterpreted :Boolean   = false,
                  group         :String    = "",
                  tag           :String    = "",
                  defaultVal    :String    = "",
                  example       :String    = "",
                  exampleMany   :String    = ""
               )
  extends scala.annotation.StaticAnnotation
{

  /**
   * prints the arg for command line display
   *
   * -env     :  the environment to run in
   *             ! required  [String]  e.g. dev
   * -log     :  the log level for logging
   *             ? optional  [String]  e.g. info
   * -enc     :  whether encryption is on
   *             ? optional  [String]  e.g. false
   * -region  :  the region linked to app
   *             ? optional  [String]  e.g. us
   *
   * @param writer
   * @param tab
   * @param prefix
   * @param separator
   * @param maxWidth
   */
  def toStringCLI(writer:Option[ConsoleWriter] = None,
                  tab:Option[String] = Some("\t"),
                  prefix:Option[String] = Some("-"),
                  separator:Option[String] = Some("="),
                  maxWidth:Option[Int] = None ): Unit =
  {
    val wr = writer.getOrElse(new ConsoleWriter())
    val namePadded = name.pad(maxWidth.getOrElse(name.length))

    // -env     :  the environment to run in
    wr.highlight("-" + namePadded, endLine = false)
    wr.text(" : ", endLine = false)
    wr.text(desc, endLine = true )
    wr.text(" ".repeat(maxWidth.getOrElse(name.length) + 6), false)

    // ! required  [String]  e.g. dev
    val requiredIndicator = if(isRequired) "!" else "?"
    if(isRequired)
    {
      wr.important(requiredIndicator, endLine = false)
      wr.text(s"required ", endLine = false)
    }
    else
    {
      wr.success(requiredIndicator, endLine = false)
      wr.text(s"optional ", endLine = false)
    }
    wr.subTitle(s"[${dataType}] ", endLine = false)
    wr.text(s"e.g. ${example}", endLine = false)
  }
}
