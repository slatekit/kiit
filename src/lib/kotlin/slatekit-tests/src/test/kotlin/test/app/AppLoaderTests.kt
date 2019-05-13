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
import slatekit.common.Context
import slatekit.common.info.About
import slatekit.app.App
import slatekit.app.AppRunner
import slatekit.results.Status
import slatekit.results.StatusCodes
import slatekit.results.Success
import slatekit.results.Try


class AppLoaderTests  {


    @Test
    fun can_select_and_use_env_local() {
        run(
                arrayOf("-env='loc'"),
                ConfigValueTest("loc", "env loc", 1, 20.1),
                StatusCodes.SUCCESS
        )
    }

    @Test
    fun can_select_and_use_env_dev() {
        run(
                arrayOf("-env='dev'"),
                ConfigValueTest("dev", "env dev", 2, 20.2),
                StatusCodes.SUCCESS
        )
    }

    @Test
    fun can_select_and_use_env_qa1() {
        run(
                arrayOf("-env='qa1'"),
                ConfigValueTest("qa1", "env qa1", 3, 20.3),
                StatusCodes.SUCCESS
        )
    }

    @Test
    fun can_select_and_use_env_qa2() {
        run(arrayOf("-env='qa2'"),
                ConfigValueTest("qa2", "env qa2", 4, 20.4),
                StatusCodes.SUCCESS
        )
    }

    data class ConfigValueTest(val v1: String, val v2: String, val v3: Int, val v4: Double)



    private fun run(args:Array<String>, value:ConfigValueTest, status: Status) {
        runBlocking {
            val result = AppRunner.run(
                    rawArgs = args,
                    about = About.none,
                    builder = { ctx -> AppConfigTest(ctx) }
            )

            Assert.assertEquals(true, result.success)
            Assert.assertEquals(status.code, result.code)
            Assert.assertEquals(status.msg, result.msg)
            val actual = result as Success<ConfigValueTest>
            Assert.assertTrue(value == actual.value)
        }
    }


    class AppConfigTest(ctx:Context) : App<Context>(ctx) {

        override suspend fun execute(): Try<Any> {
            val data = ConfigValueTest(
                    ctx.env.name,
                    ctx.cfg.getString("test_stri"),
                    ctx.cfg.getInt("test_int"),
                    ctx.cfg.getDouble("test_doub")
            )
            return Success(data)
        }
    }
}
