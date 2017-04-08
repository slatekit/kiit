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

/**
 * This class is intended for console / desktop / batch apps where
 * only 1 person can be logged in at a time.
 */
class AuthConsole(isAuthenticated:Boolean, user:User, roles:String)
  extends Auth(isAuthenticated, user, roles) {
}
