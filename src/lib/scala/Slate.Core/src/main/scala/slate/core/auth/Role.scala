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

package slate.core.auth


trait RoleInfo {
  val name:String
  val value:String
}


/**
 * Represents any authenticated user
 */
case object RoleAny extends RoleInfo { val name = "any"; val value = "*" }

/**
 * Represents a guest
 */
case object RoleGuest extends RoleInfo { val name = "guest"; val value = "?" }


/**
 * Represents a reference to a parent role
 */
case object RoleParent extends RoleInfo { val name = "parent"; val value = "@parent" }


/**
 * No roles
 */
case object RoleNone extends RoleInfo { val name = "none"; val value = "@none" }


/**
 * No roles
 */
case class Role(name:String, value:String) extends RoleInfo

