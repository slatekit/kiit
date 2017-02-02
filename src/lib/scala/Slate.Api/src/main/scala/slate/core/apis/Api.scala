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