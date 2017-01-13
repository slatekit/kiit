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
package slate.core.app

case class AppOptions (
                        printSummaryBeforeExec :Boolean = false,
                        printSummaryOnShutdown :Boolean = true ,
                        getConfNameFromEnv     :Boolean = true ,
                        getLogNameFromEnv      :Boolean = true
                      )
{
}
