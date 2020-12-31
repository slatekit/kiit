/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slate.test

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.common.*
import slatekit.common.info.About
import slatekit.app.App
import slatekit.app.AppRunner
import slatekit.results.Codes
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
