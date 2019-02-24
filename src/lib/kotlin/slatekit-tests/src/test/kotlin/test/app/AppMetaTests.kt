/**
*<slate_header>
  *author: Kishore Reddy
  *url: https://github.com/kishorereddy/scala-slate
  *copyright: 2015 Kishore Reddy
  *license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  *desc: a scala micro-framework
  *usage: Please refer to license on github for more info.
*</slate_header>
  */

package slate.test

import org.junit.Assert
import org.junit.Test
import slatekit.common.*
import slatekit.common.info.About
import slatekit.app.App
import slatekit.app.AppRunner


class AppMetaTests  {


  @Test fun can_request_help() {
    checkHelp(arrayOf("help", "-help", "--help", "/help", "?"), HELP.code, "help")
  }


  @Test fun can_request_about() {
    checkHelp(arrayOf("about", "-about", "--about", "/about", "info"), ABOUT.code, "help")
  }


  @Test fun can_request_version() {
    checkHelp(arrayOf("version", "-version", "--version", "/version", "ver"), VERSION.code,  "help")
  }


  fun checkHelp(words:Array<String>, code:Int, msg:String) {
    for(word in words){
      val result = AppRunner.run(
              rawArgs = arrayOf(word),
              about = About.none,
              builder = { ctx -> App(ctx) }
      )
      Assert.assertEquals(code, result.code)
    }
  }
}
