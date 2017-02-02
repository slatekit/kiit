/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.apis

import slate.common.{RequestBase, Inputs}
import slate.common.args.Args

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
 * @param tag       : Optional tag for tracking purposes
 */
class Request (
                     path       :String            ,
                     parts      :List[String]      ,
                     area       :String            = "",
                     name       :String            = "",
                     action     :String            = "",
                     verb       :String            = "",
                     args       :Option[Inputs]    = None,
                     opts       :Option[Inputs]    = None,
                     tag        :String            = ""
                   ) extends RequestBase ( path, parts, area, name, action, verb, args, opts, tag)
{
}


object Request
{
  def apply( path:String, args:Args, opts:Option[Inputs], verb:String) : Request =
  {
    new Request(path, args.actionVerbs, args.getVerb(0), args.getVerb(1), args.getVerb(2), verb,
      Some(args), opts)
  }


  def apply( path:String, args:Args, argsInputs:Option[Inputs], opts:Option[Inputs], verb:String) : Request =
  {

    new Request(path, args.actionVerbs, args.getVerb(0), args.getVerb(1), args.getVerb(2), verb,
      argsInputs, opts)
  }
}
