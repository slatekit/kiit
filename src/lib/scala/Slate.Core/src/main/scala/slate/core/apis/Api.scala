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

/**
  * Annotation to designate a Scala class as an API
  * @param area   : the top level area/category of the api "sys", "app", "ops"
  * @param name   : the name of the api "users"
  * @param desc   : description of the api
  * @param roles  : the roles that are permissioned to access this api ( "@admin", "@ops" )
  *                 in the event, the auth mode is "api-key", this is the name of the api-key
  * @param auth   : the authorization mode ( "app-key" | "app-roles", "key-roles" )
  * @param verb   : the verb ( "get", "post", "cli", "*" )
  * @param protocol : the platforms this is accessible to ( "web" | "cli" | "*" )
  */
case class Api (area     : String = "",
                name     : String = "",
                desc     : String = "",
                roles    : String = "",
                auth     : String = "app",
                verb     : String = "get",
                protocol : String = "*" )
  extends scala.annotation.StaticAnnotation
{

}