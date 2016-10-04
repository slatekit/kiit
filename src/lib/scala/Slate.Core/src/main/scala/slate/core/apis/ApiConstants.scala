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
  * Created by kreddy on 2/24/2016.
  */
object ApiConstants {
  val ProtocolCLI = "cli"
  val ProtocolWeb = "web"

  val RoleAny = "*"
  val RoleGuest = "?"
  val RoleParent = "@parent"
  val RoleNone = "@none"

  val AuthModeAppKey = "app-key"
  val AuthModeAppRole = "app-roles"
  val AuthModeKeyRole = "key-roles"

  val StatusError = 0
  val StatusExit = 2
  val StatusOk = 1

  val ErrorArguments = "Invalid inputs"
  val ErrorApiNotFound = "Api not found"
  val ErrorApiDoesNotHaveMethod = "Api does not have the command supplied"
}
