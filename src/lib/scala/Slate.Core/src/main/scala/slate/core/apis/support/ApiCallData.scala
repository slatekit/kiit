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
