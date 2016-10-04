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

package slate.core.apis.support

import slate.common.ReflectedArg
import slate.core.apis.{Api, ApiAction}

import scala.reflect.runtime.universe.Symbol
import scala.reflect.runtime.universe.MethodMirror

/**
 * Represents all the meta data and reflection info needed to call a method dynamically on an class/ApiBase
 * NOTE: Java reflection is used to get the annotations and scala reflection is used to get the parameter lists
 *
 * @param name      :  The name of the api action / method name to call
 * @param api       :  The Api annotation put on the method indicating its an api with roles/permissions
 * @param action    :  The ApiAction annotation put on the method indicating its an api action with roles/permissions
 * @param mirror    :  The Scala scala.lang.runtime.universe.MethodMirror to call a method dynamically
 * @param hasArgs   :  Whether the api action has any arguments / parameters ( convenience flag )
 * @param paramList :  A list of the parameters on the method ( name, typename, typeSymbol, position )
 */
class ApiCallReflect(
                      val name      : String                   ,
                      val api       : Api                      ,
                      val action    : ApiAction                ,
                      val mirror    : MethodMirror             ,
                      val hasArgs   : Boolean                  ,
                      val paramList : List[ReflectedArg]
                    )
{
  def isSingleDefaultedArg():Boolean = {
    if(!hasArgs || paramList.size != 1)
      return false
    val arg = paramList(0)
    arg.isParamDefaulted
  }


  def isSingleArg():Boolean = {
    hasArgs && paramList.size == 1
  }
}
