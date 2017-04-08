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

package slate.core.apis.core

import slate.common.ListMap
import slate.core.apis.ApiContainer
import slate.core.apis.doc.{ApiVisitor, DocConsole, Doc}

class Help(val ctn:ApiContainer, lookup:ListMap[String, Apis], docs:Doc) {


  /**
   * handles help request for all the areas supported
   *
   * @return
   */
  def help():Unit = {
  }


  /**
   * handles help request for a specific area
   *
   * @param area
   * @return
   */
  def helpForArea(area:String):Unit =
  {
    // Guard
    require(lookup.contains(area), s"${area} not found")

    val doc = new DocConsole()
    val visitor = new ApiVisitor()
    visitor.visitApis(area, lookup(area), doc)
  }


  /**
   * handles help request for a specific api
   *
   * @param area
   * @param apiName
   * @return
   */
  def helpForApi(area:String, apiName:String):Unit =
  {
    // Guard
    require(lookup.contains(area), s"${area} not found")
    require( lookup(area).contains(apiName), s"${apiName} not found")

    val apis = lookup(area)
    val api = apis(apiName)
    val doc = new DocConsole()
    val visitor = new ApiVisitor()
    visitor.visitApi(api,apiName, doc)
  }


  /**
   * handles help request for a specific api action
   *
   * @param area
   * @param apiName
   * @param actionName
   * @return
   */
  def helpForAction(area:String, apiName:String, actionName:String):Unit =
  {
    // Guard
    require(lookup.contains(area), s"${area} not found")
    require( lookup(area).contains(apiName), s"${apiName} not found")
    require( lookup(area)(apiName).contains(actionName), s"${actionName} not found")

    val apis = lookup(area)
    val api = apis(apiName)
    val doc = new DocConsole()
    val visitor = new ApiVisitor()
    visitor.visitApiAction(api, apiName, actionName, doc)
  }
}
