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

package slate.core.apis

import slate.common.{Inputs, Strings}
import slate.common.args.Args
import scala.collection.mutable.Map

/**
 * Represents an abstraction of a Web Api Request and also a CLI ( Command Line ) request
 * @param path      : route(endpoint) e.g. /{area}/{name}/{action} e.g. /app/reg/activateUser
 * @param parts     : list of the parts of the action e.g. [ "app", "reg", "activateUser" ]
 * @param area      : action represented by route e.g. app in "app.reg.activateUser"
 * @param name      : name represented by route   e.g. reg in "app.reg.activateUser"
 * @param action    : action represented by route e.g. activateUser in "app.reg.activateUser"
 * @param verb      : get / post ( similar to http verb )
 * @param opts      : options representing settings/configurations ( similar to http-headers )
 * @param args      : arguments to the command
 */
case class ApiCmd (
                     path       :String            ,
                     parts      :List[String]      ,
                     area       :String            = "",
                     name       :String            = "",
                     action     :String            = "",
                     verb       :String            = "",
                     args       :Option[Inputs]    = None,
                     opts       :Option[Inputs]    = None
                   )
{
  def fullName :String =
  {
    if(Strings.isNullOrEmpty(name))
      return area

    if(Strings.isNullOrEmpty(action))
      return area + "." + name

    area + "." + name + "." + action
  }
}


object ApiCmd
{
  def apply( path:String, args:Args, opts:Option[Inputs], verb:String) : ApiCmd =
  {
    new ApiCmd(path, args.actionVerbs, args.getVerb(0), args.getVerb(1), args.getVerb(2), verb,
      Some(args), opts)
  }


  def apply( path:String, args:Args, argsInputs:Option[Inputs], opts:Option[Inputs], verb:String) : ApiCmd =
  {

    new ApiCmd(path, args.actionVerbs, args.getVerb(0), args.getVerb(1), args.getVerb(2), verb,
      argsInputs, opts)
  }
}
