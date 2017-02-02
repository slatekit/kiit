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

package slate.core.apis.support

/**
 * Used to supply information to the audit method in event of auditing an api call.
 * @param action : "app.users.invite"
 * @param user   : The user performing the action
 * @param data   : The data supplied to the api action
 * @param args   : The arguments to the api action
 * @param call   : needed ??
 */
class ApiCallData(val action:String, val user:String, val data:String, val args:Array[String], val call:() => Any ) {

}
