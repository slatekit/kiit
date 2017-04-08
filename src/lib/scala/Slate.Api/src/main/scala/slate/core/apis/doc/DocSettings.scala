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
package slate.core.apis.doc

class DocSettings(
                          var maxLengthApi     :Int     = 0    ,
                          var maxLengthAction  :Int     = 0    ,
                          var maxLengthArg     :Int     = 0    ,
                          var enableDetailMode :Boolean = false
                        )
{
}
