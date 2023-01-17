/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
 *  </kiit_header>
 */

package slate.test

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kiit.context.Context
import kiit.common.info.About
import kiit.app.App
import kiit.app.AppRunner
import kiit.results.Status
import kiit.results.Codes
import kiit.results.Success
import test.TestApp


class AppLoaderTests  {


    @Test
    fun can_select_and_use_env_local() {
        run(
                arrayOf("-env='loc'"),
                ConfigValueTest("loc", "env loc", 1, 20.1),
                Codes.SUCCESS
        )
    }

    @Test
    fun can_select_and_use_env_dev() {
        run(
                arrayOf("-env='dev'"),
                ConfigValueTest("dev", "env dev", 2, 20.2),
                Codes.SUCCESS
        )
    }

    @Test
    fun can_select_and_use_env_qat() {
        run(
                arrayOf("-env='qat'"),
                ConfigValueTest("qat", "env qat", 3, 20.3),
                Codes.SUCCESS
        )
    }

    data class ConfigValueTest(val v1: String, val v2: String, val v3: Int, val v4: Double)



    private fun run(args:Array<String>, value:ConfigValueTest, status: Status) {
        runBlocking {
            val result = AppRunner.run(
                    TestApp::class.java,
                    rawArgs = args,
                    about = About.none,
                    builder = { ctx -> AppConfigTest(ctx) }
            )

            Assert.assertEquals(true, result.success)
            Assert.assertEquals(status.code, result.code)
            Assert.assertEquals(status.desc, result.desc)
            val actual = result as Success<ConfigValueTest>
            Assert.assertTrue(value == actual.value)
        }
    }


    class AppConfigTest(ctx: Context) : App<Context>(ctx) {

        override suspend fun exec():Any? {
            val data = ConfigValueTest(
                    ctx.envs.name,
                    ctx.conf.getString("test_stri"),
                    ctx.conf.getInt("test_int"),
                    ctx.conf.getDouble("test_doub")
            )
            return data
        }
    }
}
