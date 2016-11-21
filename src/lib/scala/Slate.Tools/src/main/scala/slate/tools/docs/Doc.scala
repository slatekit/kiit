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

package slate.tools.docs

import slate.common.Strings

case class Doc(
                name     : String,
                source   : String,
                version  : String,
                example  : String,
                available: Boolean,
                multi    : Boolean,
                readme   : Boolean,
                group    : String,
                folder   : String,
                jar      : String,
                depends  : String,
                desc     : String
              )
{
  def area:String =
  {
    //"slate.common.args.Args"
    val ns = namespace
    ns.substring(0, ns.lastIndexOf("."))
  }


  def namespace:String =
  {
    //"slate.common.args.Args"
    source.substring(0, source.lastIndexOf("."))
  }


  def sourceFolder():String =
  {
    //"slate.common.args.Args"
    val path = namespace.replace(".", "/")
    val proj = Strings.split(path, '/')(1)
    val folder = Strings.split(path, '/').last
    var fullPath = ""
    proj match {
      case "common"   => fullPath = s"/src/lib/scala/Slate.Common/src/main/scala/slate/common/${folder}"
      case "entities" => fullPath = s"/src/lib/scala/Slate.Entities/src/main/scala/slate/entities/${folder}"
      case "core"     => fullPath = s"/src/lib/scala/Slate.Core/src/main/scala/slate/core/${folder}"
      case "cloud"    => fullPath = s"/src/lib/scala/Slate.Cloud/src/main/scala/slate/cloud/${folder}"
      case "ext"      => fullPath = s"/src/lib/scala/Slate.Ext/src/main/scala/slate/ext/${folder}"
      case _          => fullPath = ""
    }
    fullPath
  }


  def dependsOn():String = {
    var items = ""
    val tokens = Strings.split(depends, ',')
    for(token <- tokens){
      token match {
        case "com"       => items = items + " slate.common.jar"
        case "ent"       => items = items + " slate.entities.jar"
        case "core"      => items = items + " slate.core.jar"
        case "cloud"     => items = items + " slate.cloud.jar"
        case "ext"       => items = items + " slate.ext.jar"
        case "tools"     => items = items + " slate.tools.jar"
        case _           => {}
      }
    }
    items
  }


  def layout:String = {
    if(Strings.isMatch(group, "infra")){
      return "_mods_infra"
    }
    if(Strings.isMatch(group, "feat")){
      return "_mods_fea"
    }
    if(Strings.isMatch(group, "utils")){
      return "_mods_utils"
    }
    ""
  }
}
