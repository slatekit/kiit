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
