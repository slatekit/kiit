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

import slate.common.{Strings, ListMap}
import slate.core.apis.support.ApiCallReflect
import slate.core.apis.{ApiArg, Api, ApiBase}


class ApiVisitor {

  def visitAreas( areas:List[String], visitor:ApiVisit ) : Unit =
  {
    visitor.onVisitAreasBegin()
    for(area <- areas )
    {
      visitor.onVisitAreaBegin(area)
      visitor.onVisitAreaEnd(area)
    }
    visitor.onVisitAreasEnd()
  }


  def visitApis(area:String, apis:ListMap[String,ApiBase], visitor:ApiVisit) : Unit =
  {
    val all = apis.all()
    if(all.isEmpty) return
    val keys = apis.keys()
    visitor.settings.maxLengthApi = Strings.maxLength(keys)

    visitor.onVisitApisBegin(area)
    for(api <- all)
    {
      val actions = api.actions()
      if(actions.size() > 0)
      {
        val apiAnno = actions.getAt(0).api
        visitApi(apiAnno, visitor, actions,  listActions = false, listArgs = false)
      }
    }
    visitor.onVisitApisEnd(area)
  }


  def visitApi(apiBase: ApiBase, apiName:String, visitor:ApiVisit ) : Unit =
  {
    val actions = apiBase.actions()
    if(actions.size() > 0)
    {
      val apiAnno = actions.getAt(0).api
      visitApi(apiAnno, visitor, actions, listActions = true, listArgs = false )
    }
    visitor.onVisitApiActionSyntax()
  }


  def visitApi(api:Api, visitor:ApiVisit, actions:ListMap[String,ApiCallReflect],
               listActions:Boolean = true, listArgs:Boolean = false) : Unit =
  {
    visitor.onVisitApiBegin(api)
    if (actions.size > 0)
    {
      if ( listActions ) {

        visitor.onVisitSeparator()
        val actionNames = actions.keys()
        visitor.settings.maxLengthAction = Strings.maxLength(actionNames)

        for (action <- actions.all()) {
          visitApiAction(action, visitor, listArgs)
        }
      }
    }
    visitor.onVisitApiEnd(api)
  }


  def visitApiAction(apiBase: ApiBase, apiName:String, actionName:String, visitor:ApiVisit) : Unit =
  {
    val actions = apiBase.actions()
    if(actions.size() > 0)
    {
      val api = actions.getAt(0).api
      visitor.onVisitApiBegin(api)
      visitor.onVisitSeparator()
      val call = actions(actionName)
      visitApiAction(call, visitor, detailMode = true)

      if ( true ) {
        visitor.onVisitApiActionExample(api, call.name, call.action, call.paramList)
      }
    }
  }


  def visitApiAction(action:ApiCallReflect, visitor:ApiVisit, detailMode:Boolean = true):Unit =
  {
    // action
    visitor.onVisitApiActionBegin(action.action, action.name)

    if( detailMode )
    {
      visitArgs(action, visitor)
    }
    // args here.
    visitor.onVisitApiActionEnd(action.action, action.name)
  }


  def visitArgs(info:ApiCallReflect, visitor:ApiVisit):Unit =
  {
    if(!info.hasArgs) return
    val names = info.paramList.map((item) => item.name)
    val maxLength = Strings.maxLength(names)
    visitor.settings.maxLengthArg = maxLength
    for(argInfo <- info.paramList)
    {
      visitor.onVisitApiArgBegin(new ApiArg(name = argInfo.name, ""))
    }
  }
}
