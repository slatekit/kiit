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

package slate.core.apis.doc

import slate.common.reflect.ReflectedArg
import slate.core.apis.{Api, ApiAction, ApiArg}

trait ApiVisit {

  def settings:DocSettings


  def onVisitSeparator(): Unit


  def onAreasBegin(): Unit


  def onAreaBegin(area:String): Unit


  def onAreaEnd(area:String): Unit


  def onAreasEnd(): Unit


  def onApisBegin(area:String): Unit


  def onApiBegin(api:Api): Unit


  def onApiEnd(api:Api): Unit


  def onApiActionSyntax(): Unit


  def onApisEnd(area:String): Unit


  def onApiActionBegin(action:ApiAction, name:String): Unit


  def onApiActionEnd(action:ApiAction, name:String): Unit


  def onApiActionExample(api: Api, actionName: String, action: ApiAction, args:List[ReflectedArg]): Unit


  def onArgBegin(arg:ApiArg): Unit


  def onArgEnd(arg:ApiArg): Unit
}
