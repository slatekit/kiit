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

import slate.common.ListMap


/**
  * Container for all the registered apis that can be called dynamically.
  * This contains a lookup of api names to the actual apis.
  * e.g.
  * {
  *   "invites" => InvitesApi()
  *   "devices" => DevicesApi()
  * }
  */
class ApiLookup extends ListMap[String,ApiBase] {

}
