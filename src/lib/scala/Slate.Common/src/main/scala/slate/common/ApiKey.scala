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

package slate.common

/**
 * Represents a key for accessing an API securely.
 * @param name : "admin"
 * @param key  : "123456789123456789"
 * @param roles : "admin"
 */
case class ApiKey(
                   name:String,
                   key :String,
                   roles:String = "",
                   rolesLookup:Map[String,String] = null
                 )
{
}
