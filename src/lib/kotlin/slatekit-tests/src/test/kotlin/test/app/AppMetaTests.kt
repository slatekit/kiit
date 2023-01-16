/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package slate.test

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kiit.common.*
import kiit.common.info.About
import kiit.app.App
import kiit.app.AppRunner
import kiit.results.Codes
import test.TestApp


class AppMetaTests  {


  @Test fun can_request_help() {
    checkHelp(arrayOf("help", "-help", "--help", "/help", "?"), Codes.HELP.code, "help")
  }


  @Test fun can_request_about() {
    checkHelp(arrayOf("about", "-about", "--about", "/about", "info"), Codes.ABOUT.code, "help")
  }


  @Test fun can_request_version() {
    checkHelp(arrayOf("version", "-version", "--version", "/version", "ver"), Codes.VERSION.code,  "help")
  }


  fun checkHelp(words:Array<String>, code:Int, msg:String) {
    runBlocking {
      for (word in words) {
        val result = AppRunner.run(
                TestApp::class.java,
                rawArgs = arrayOf(word),
                about = About.none,
                builder = { ctx -> App(ctx) }
        )
        Assert.assertEquals(code, result.code)
      }
    }
  }
}
