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
