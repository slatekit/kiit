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

package slate.common

/**
 * Provides a simple abstraction over most requests ( both HTTP and CLI )
 * NOTE: This is used as the base class for the ApiCmd in the Slate.Core API module
 * which allows you to build protocol independent APIs.
 *
 * @param path      : route(endpoint) e.g. /{area}/{name}/{action} e.g. /app/reg/activateUser
 * @param parts     : list of the parts of the action e.g. [ "app", "reg", "activateUser" ]
 * @param area      : action represented by route e.g. app in "app.reg.activateUser"
 * @param name      : name represented by route   e.g. reg in "app.reg.activateUser"
 * @param action    : action represented by route e.g. activateUser in "app.reg.activateUser"
 * @param verb      : get / post ( similar to http verb )
 * @param opts      : options representing settings/configurations ( similar to http-headers )
 * @param args      : arguments to the command
 * @param tag       : Optional tag for tracking purposes
 */
class RequestBase(
                val path       :String            ,
                val parts      :List[String]      ,
                val area       :String            = "",
                val name       :String            = "",
                val action     :String            = "",
                val verb       :String            = "",
                val args       :Option[Inputs]    = None,
                val opts       :Option[Inputs]    = None,
                val tag        :String            = ""
               )
{
  def fullName :String =
  {
    if(Strings.isNullOrEmpty(name))
      area

    else if(Strings.isNullOrEmpty(action))
      area + "." + name

    else
      area + "." + name + "." + action
  }
}
