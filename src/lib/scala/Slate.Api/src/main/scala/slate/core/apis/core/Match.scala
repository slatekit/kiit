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

/**
 * Represents a way to match on a route
 * @param area
 * @param api
 * @param action
 */
case class Match(area:Option[String], api:Option[String], action:Option[String]) {

  /**
   * represents a match on everything
   */
  val isGlobal:Boolean = area.isEmpty && api.isEmpty && action.isEmpty


  /**
   * represents a match on a specific area only
   */
  val isArea  :Boolean = area.isDefined && api.isEmpty


  /**
   * represents a match on a specific api only
   */
  val isApi   :Boolean = area.isDefined && api.isDefined && action.isEmpty


  /**
   * represents a match on a specific action only
   */
  val isAction:Boolean = area.isDefined && api.isDefined && action.isDefined


  /**
   * the full path representing the match
   * @return
   */
  def fullName():String = {
    //val path = List(area, api, action).fold[String]("")( (a, b) => a + "/" + b)
    //path
    ""
  }


  /**
   * whether this api match matches the area/api/action supplied.
   * @param targetArea
   * @param targetApi
   * @param targetAction
   * @return
   */
  def isMatch(targetArea:String, targetApi:String, targetAction:String): Boolean = {
    if ( isGlobal )
      true
    else if ( isArea   && !this.area.contains(targetArea))
      false
    else if ( isApi    && !this.api.contains(targetApi))
      false
    else if ( isAction && !this.action.contains(targetAction))
      false
    else
      true
  }
}