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
        parentValue
      }
      else
        primaryValue
    }
    // Parent!
    else if(!Strings.isNullOrEmpty(parentValue)){
      parentValue
    }
    else
      ""
  }
}
