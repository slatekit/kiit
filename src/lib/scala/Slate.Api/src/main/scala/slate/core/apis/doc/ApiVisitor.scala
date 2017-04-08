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

import slate.common.{Strings, ListMap}
import slate.core.apis.core.Action
import slate.core.apis.{ApiArg, Api, ApiBase}


class ApiVisitor {

  def visitAreas( areas:List[String], visitor:ApiVisit ) : Unit =
  {
    visitor.onAreasBegin()
    areas.foreach( area => {
      visitor.onAreaBegin(area)
      visitor.onAreaEnd(area)
    })
    visitor.onAreasEnd()
  }


  def visitApis(area:String, apis:ListMap[String,ApiBase], visitor:ApiVisit) : Unit =
  {
    val all = apis.all()
    if(all.nonEmpty) {
      val keys = apis.keys()
      visitor.settings.maxLengthApi = Strings.maxLength(keys)

      visitor.onApisBegin(area)
      val sorted = keys.sortBy(s => s)
      sorted.foreach(key => {
        val api = apis(key)
        val actions = api.actions()
        if (actions.size() > 0) {
          actions.getAtOpt(0).fold(Unit)(apiAnno => {
            visitApi(apiAnno.api, visitor, actions, listActions = false, listArgs = false)
            Unit
          })
        }
      })
      visitor.onApisEnd(area)
    }
  }


  def visitApi(apiBase: ApiBase, apiName:String, visitor:ApiVisit ) : Unit =
  {
    val actions = apiBase.actions()
    if(actions.size() > 0)
    {
      actions.getAtOpt(0).fold(Unit)( apiAnno => {
        visitApi(apiAnno.api, visitor, actions, listActions = true, listArgs = false)
        Unit
      })
    }
    visitor.onApiActionSyntax()
  }


  def visitApi(api:Api, visitor:ApiVisit, actions:ListMap[String,Action],
               listActions:Boolean = true, listArgs:Boolean = false) : Unit =
  {
    visitor.onApiBegin(api)
    if (actions.size > 0)
    {
      if ( listActions ) {

        visitor.onVisitSeparator()
        val actionNames = actions.keys().sortBy(s => s)
        visitor.settings.maxLengthAction = Strings.maxLength(actionNames)

        actionNames.foreach( actionName => {
          val action = actions(actionName)
          visitApiAction(action, visitor, listArgs)
        })
      }
    }
    visitor.onApiEnd(api)
  }


  def visitApiAction(apiBase: ApiBase, apiName:String, actionName:String, visitor:ApiVisit) : Unit =
  {
    val actions = apiBase.actions()
    if(actions.size() > 0)
    {
      actions.getAtOpt(0).fold(Unit)( apiAnno => {

        val api = apiAnno.api
        visitor.onApiBegin(api)
        visitor.onVisitSeparator()
        val call = actions(actionName)
        visitApiAction(call, visitor, detailMode = true)

        if ( true ) {
          visitor.onApiActionExample(api, call.name, call.action, call.paramList)
        }
        Unit
      })
    }
  }


  def visitApiAction(action:Action, visitor:ApiVisit, detailMode:Boolean = true):Unit =
  {
    // action
    visitor.onApiActionBegin(action.action, action.name)

    if( detailMode )
    {
      visitArgs(action, visitor)
    }
    // args here.
    visitor.onApiActionEnd(action.action, action.name)
  }


  def visitArgs(info:Action, visitor:ApiVisit):Unit =
  {
    if(info.hasArgs) {
      val names = info.paramList.map((item) => item.name)
      val maxLength = Strings.maxLength(names)
      visitor.settings.maxLengthArg = maxLength
      info.paramList.foreach(argInfo => {
        visitor.onArgBegin(new ApiArg(name = argInfo.name, ""))
      })
    }
  }
}
