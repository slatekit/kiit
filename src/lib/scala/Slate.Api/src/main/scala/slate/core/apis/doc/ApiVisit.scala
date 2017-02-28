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

import slate.common.{ListMap, Strings, ReflectedArg}
import slate.core.apis.{ApiBase, Api, ApiAction, ApiArg}

trait ApiVisit {

  def settings:ApiDocSettings


  def isOutputSupported : Boolean


  def result : AnyRef


  def onVisitSeparator(): Unit


  def onVisitAreasBegin(): Unit


  def onVisitAreaBegin(area:String): Unit


  def onVisitAreaEnd(area:String): Unit


  def onVisitAreasEnd(): Unit


  def onVisitApisBegin(area:String): Unit


  def onVisitApiBegin(api:Api): Unit


  def onVisitApiEnd(api:Api): Unit


  def onVisitApiActionSyntax(): Unit


  def onVisitApisEnd(area:String): Unit


  def onVisitApiActionBegin(action:ApiAction, name:String): Unit


  def onVisitApiActionEnd(action:ApiAction, name:String): Unit


  def onVisitApiActionExample(api: Api, actionName: String, action: ApiAction,
                              args:List[ReflectedArg]): Unit


  def onVisitApiArgBegin(arg:ApiArg): Unit


  def onVisitApiArgEnd(arg:ApiArg): Unit
}
