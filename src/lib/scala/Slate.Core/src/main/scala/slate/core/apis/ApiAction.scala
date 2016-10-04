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

package slate.core.apis

import slate.common.Strings

/**
  * Annotation used in conjunction with the Api annotation, to designate your scala method
  * as an api action that is exposed for use.
  * @param name     : the name of the action, leave empty to use the method name this is applied to
  * @param desc     : the description of the action
  * @param roles    : the roles allowed ( use @parent to refer to parent Api anntoation roles )
  * @param verb     : the verb ( "get", "post", "cli", "*" ) allowed.
  * @param protocol : the protocol ( "web, "cli", "*" ) required to access this action
  */
case class ApiAction(
                      name     : String = "" ,
                      desc     : String = "" ,
                      roles    : String = "" ,
                      verb     : String = "" ,
                      protocol : String = "*"
                    )
  extends scala.annotation.StaticAnnotation
{

  /**
    * gets the actual verb
    * @param api
    * @return
    */
  def actualVerb(api:Api): String = getReferencedValue(verb, api.verb)


  def actualProtocol(api:Api): String = getReferencedValue(protocol, api.protocol)


  def getReferencedValue(primaryValue:String, parentValue:String) : String = {

    // Role!
    if(!Strings.isNullOrEmpty(primaryValue) ){
      if(Strings.isMatch(primaryValue, ApiConstants.RoleParent)){
        return parentValue
      }
      return primaryValue
    }
    // Parent!
    if(!Strings.isNullOrEmpty(parentValue)){
      return parentValue
    }
    ""
  }
}
