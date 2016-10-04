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

package slate.core.apis.doc

import slate.common.ReflectedArg
import slate.core.apis.{Api, ApiAction, ApiArg}

trait ApiVisit {

  def settings:ApiDocSettings


  def isOutputSupported : Boolean


  def result : Object


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
