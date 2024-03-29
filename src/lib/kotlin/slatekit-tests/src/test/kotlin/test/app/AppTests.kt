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

package test.app

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kiit.app.App
import kiit.app.AppRunner
import kiit.context.Context
import kiit.common.args.ArgsSchema
import kiit.common.crypto.Encryptor
import kiit.common.info.About
import kiit.providers.logback.LogbackLogs
import kiit.results.Success
import kiit.results.Try
import kiit.results.getOrElse
import test.TestApp


class AppTests {

//    suspend fun s1(a:Int, b:Int): Int {
//        Thread.sleep(300)
//        return a + b
//    }
//
//    @Test
//    fun testSuspend(){
//        runBlocking {
//            val result = s1(1, 2)
//            Assert.assertEquals(3, result)
//        }
//    }

    fun testScenarios(call: (Array<String>?) -> Try<Any>) {
        call(null)

        call(arrayOf())

        call(arrayOf("-a=1", "-b=2"))
    }


    /**
     * Case: No schema
     */
    class AppArgsSchemaNull(ctx: Context) : App<Context>(ctx) {
        override suspend fun exec(): Any? = "ok - schema null"
    }


    /**
     * Case: Empty schema
     * @param schema
     */
    class AppArgsSchemaEmpty(ctx: Context) : App<Context>(ctx) {
        override suspend fun exec(): Any? = "ok - schema empty"
    }


    suspend fun runApp(args: Array<String>?, schema: ArgsSchema?, enc: Encryptor?, appCreator: (Context) -> App<Context>): Try<Any?> {
        return AppRunner.run(
                TestApp::class.java,
                rawArgs = args ?: arrayOf(),
                about = About.simple("test id", "test name", "test desc", "test company"),
                schema = schema,
                enc = enc,
                logs = LogbackLogs(),
                builder = { ctx -> appCreator(ctx) }
        )
    }


    @Test
    fun can_run_process_with_null_args_schema_without_raw_args() {
        runBlocking {
            val result = runApp(null, null, null, { ctx -> AppArgsSchemaNull(ctx) })
            Assert.assertTrue(result.success)
            result.onSuccess { value ->
                Assert.assertEquals("ok - schema null", value)
            }
        }
    }


    @Test
    fun can_run_process_with_empty_args_schema() {
        runBlocking {
            val result = runApp(null, ArgsSchema(listOf()), null, { ctx -> AppArgsSchemaEmpty(ctx) })
            Assert.assertTrue(result.success)
            result.onSuccess { value ->
                Assert.assertEquals("ok - schema empty", value)
            }
        }
    }


    /*
    @Test
    fun can_run_process_with_empty_args_schema() {
        runApp { args ->
            val res = AppRunner.run(AppArgsSchemaEmpty(args))
            assertResult(res, "ok", 200, "schema empty")
            res
        }
    }


    @Test
    fun can_run_process_with_empty_args_defined() {
        runApp { args ->
            val res = AppRunner.run(AppArgsSchemaBasicNoneRequired(args))
            assertResult(res, "ok", 200, "schema basic")
            res
        }
    }


    @Test
    fun can_run_process_with_args_defined_and_required_and_missing() {
        val res = AppRunner.run(AppArgsSchemaBasic1Required(arrayOf<String>()))
        assertResultBasic(res, Codes.BAD_REQUEST.code, "invalid arguments supplied: Missing : env")
    }


    @Test
    fun can_run_process_with_args_defined_and_required_and_supplied() {
        val res = AppRunner.run(AppArgsSchemaBasic1Required(arrayOf("-env='loc'")))
        assertResult(res, "ok", 200, "schema args 1")
    }


    @Test
    fun can_run_process_with_env_correct() {
        val res = AppRunner.run(AppArgsSchemaBasic1Required(arrayOf("-env='loc'")))
        assertResult(res, "ok", 200, "schema args 1")
    }


    @Test
    fun can_run_process_with_env_incorrect() {
        val res = AppRunner.run(AppArgsSchemaBasic1Required(arrayOf("-env='abc'")))
        assertResultBasic(res, 400, "Unknown environment name : abc supplied")
    }


    @Test
    fun can_handle_unexpected_error() {
        val res = AppRunner.run(AppErrorTest(arrayOf("-env='loc'")))
        assertResultBasic(res, 500, "Unexpected error : error test")
    }


    fun assertConfigResult(res: Try<ConfigValueTest>,
                           expected: ConfigValueTest, code: Int, msg: String): Unit {
        Assert.assertTrue(res.code == code)
        Assert.assertTrue(res.msg == msg)
        Assert.assertTrue(res.getOrElse { null } == expected)
    }


    data class ConfigValueTest(val v1: String, val v2: String, val v3: Int, val v4: Double)



    class AppErrorTest(
            args: Array<String>?
    ) : App(AppContext.empty) {

        override fun execute(): Try<Any> {
            if (ctx.cfg != null) {
                throw Exception("error test")
            }
            return Success("ok")
        }
    }


    fun assertResult(res: Try<Any>, value: String, code: Int, msg: String) {
        Assert.assertTrue(res.getOrElse { null } == value)
        Assert.assertTrue(res.code == code)
        Assert.assertTrue(res.msg == msg)
    }


    fun checkHelp(words: Array<String>, code: Int, msg: String) {
        for (word in words) {
            val res = AppRunner.run(AppArgsSchemaBasic1Required(arrayOf(word)))
            assertResultBasic(res, code, msg)
        }
    }


    fun assertResultBasic(res: Try<Any>, code: Int, msg: String) {
        Assert.assertTrue(res.code == code)
        Assert.assertTrue(res.msg == msg)
    }


    /**
     * Case: No schema
     */
    class AppArgsSchemaNull(args: Array<String>?) : App(AppContext.empty) {

        override fun execute(): Try<Any> = Success("ok", msg = "schema null")
    }


    /**
     * Case: Empty schema
     * @param schema
     */
    class AppArgsSchemaEmpty(
            args: Array<String>?,
            schema: ArgsSchema? = ArgsSchema(listOf<Arg>())
    ) : App(AppContext.empty) {

        override fun execute(): Try<Any> = Success("ok", msg = "schema empty")
    }


    /**
     * Case args - none required
     * @param schema
     */
    class AppArgsSchemaBasicNoneRequired
    (
            args: Array<String>?,
            schema: ArgsSchema? = ArgsSchema()
                    .text("env", "the environment to run in", false, "dev", "dev", "dev1|qa1|stg1|pro")
                    .text("region", "the region linked to app", false, "us", "us", "us|europe|india|*")
                    .text("log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")
    ) : App(AppContext.empty) {

        override fun execute(): Try<Any> = Success("ok", msg = "schema basic")
    }


    /**
     * Case args - 1 required
     * @param schema
     */
    class AppArgsSchemaBasic1Required
    (
            args: Array<String>?,
            schema: ArgsSchema? = ArgsSchema()
                    .text("env", "the environment to run in", true, "dev", "dev", "dev1|qa1|stg1|pro")
                    .text("region", "the region linked to app", false, "us", "us", "us|europe|india|*")
                    .text("log.level", "the log level for logging", false, "info", "info", "debug|info|warn|error")
    ) : App(AppContext.empty) {

        override fun execute(): Try<Any> = Success("ok", msg = "schema args 1")
    }


    class AppArgsSchemaBasicAllRequired() : App(AppContext.empty) {

        override fun execute(): Try<Any> = Success("ok")

        companion object {
            val args: Array<String> = arrayOf("env=dev", "region=us", "log.level=info")

            val schema: ArgsSchema? = ArgsSchema()
            .text("env", "the environment to run in", true, "dev", "dev", "dev1|qa1|stg1|pro")
            .text("region", "the region linked to app", true, "us", "us", "us|europe|india|*")
            .text("log.level", "the log level for logging", true, "info", "info", "debug|info|warn|error")

        }
    }
    */
}
