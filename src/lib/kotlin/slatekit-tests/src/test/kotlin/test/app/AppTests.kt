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


class AppTests {

    /*
    @Test
    fun can_run_process_with_null_args_schema_without_raw_args() {
        runApp { args ->
            val res = AppRunner.run(AppArgsSchemaNull(args))
            assertResult(res, "ok", 200, "schema null")
            res
        }
    }


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
        assertResultBasic(res, StatusCodes.BAD_REQUEST.code, "invalid arguments supplied: Missing : env")
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


    fun runApp(call: (Array<String>?) -> Try<Any>) {
        call(null)

        call(arrayOf())

        call(arrayOf("-a=1", "-b=2"))
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
