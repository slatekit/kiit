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

import org.junit.Test
import slatekit.common.*
import slatekit.common.args.Arg
import slatekit.common.args.ArgsSchema
import slatekit.core.app.App
import slatekit.core.app.AppRunner
import slatekit.core.common.AppContext
import slatekit.results.StatusCodes
import slatekit.results.Success
import slatekit.results.Try
import slatekit.results.getOrElse


class AppTests  {


  @Test fun can_request_help() {
    checkHelp(arrayOf("help", "-help", "--help", "/help", "?"), HELP.code, "help")
  }


  @Test fun can_request_about() {
    checkHelp(arrayOf("about", "-about", "--about", "/about", "info"), HELP.code, "help")
  }


  @Test fun can_request_version() {
    checkHelp(arrayOf("version", "-version", "--version", "/version", "ver"), HELP.code,  "help")
  }


  @Test fun can_request_exit() {
    checkHelp(arrayOf("exit", "-exit", "--exit", "/exit", "exit"), EXIT.code, "exit")
  }


  @Test fun can_run_process_with_null_args_schema_without_raw_args() {
    runApp { args ->
      val res = AppRunner.run(AppArgsSchemaNull(args))
      assertResult(res, "ok", 200, "schema null")
      res
    }
  }


  @Test fun can_run_process_with_empty_args_schema() {
    runApp { args ->
      val res = AppRunner.run( AppArgsSchemaEmpty(args))
      assertResult(res, "ok", 200, "schema empty")
      res
    }
  }


  @Test fun can_run_process_with_empty_args_defined() {
    runApp { args ->
      val res = AppRunner.run(AppArgsSchemaBasicNoneRequired(args))
      assertResult(res, "ok", 200, "schema basic")
      res
    }
  }


  @Test fun can_run_process_with_args_defined_and_required_and_missing() {
    val res = AppRunner.run(AppArgsSchemaBasic1Required( arrayOf<String>()))
    assertResultBasic(res, StatusCodes.BAD_REQUEST.code, "invalid arguments supplied: Missing : env")
  }


  @Test fun can_run_process_with_args_defined_and_required_and_supplied() {
    val res = AppRunner.run(AppArgsSchemaBasic1Required(arrayOf("-env='loc'")))
    assertResult(res, "ok", 200, "schema args 1")
  }


  @Test fun can_run_process_with_env_correct() {
    val res = AppRunner.run(AppArgsSchemaBasic1Required( arrayOf("-env='loc'")))
    assertResult(res, "ok", 200, "schema args 1")
  }


  @Test fun can_run_process_with_env_incorrect() {
    val res = AppRunner.run(AppArgsSchemaBasic1Required(arrayOf("-env='abc'")))
    assertResultBasic(res, 400, "Unknown environment name : abc supplied")
  }
  

    @Test fun can_select_and_use_env_local() {
      val res = AppRunner.run(AppConfigTest(arrayOf("-env='loc'")))
      val res2 = res as Try<ConfigValueTest>
      assertConfigResult(res2, ConfigValueTest("loc", "env loc", 1, 20.1), 200, "success")
    }

    @Test fun can_select_and_use_env_dev() {
      val res = AppRunner.run(AppConfigTest(arrayOf("-env='dev'")))
      val res2 = res as Try<ConfigValueTest>
      assertConfigResult(res2, ConfigValueTest("dev", "env dev", 2, 20.2), 200, "success")
    }

    @Test fun can_select_and_use_env_qa1() {
      val res = AppRunner.run(AppConfigTest(arrayOf("-env='qa1'")))
      val res2 = res as Try<ConfigValueTest>
      assertConfigResult(res2, ConfigValueTest("qa1", "env qa1", 3, 20.3), 200, "success")
    }

    @Test fun can_select_and_use_env_qa2() {
      val res = AppRunner.run(AppConfigTest(arrayOf("-env='qa2'")))
      val res2 = res as Try<ConfigValueTest>
      assertConfigResult(res2, ConfigValueTest("qa2", "env qa2", 4, 20.4), 200, "success")
    }
  

    @Test fun can_handle_unexpected_error() {
      val res = AppRunner.run(AppErrorTest(arrayOf("-env='loc'")))
      assertResultBasic(res, 500, "Unexpected error : error test")
    }


  fun assertConfigResult(res:Try<ConfigValueTest>,
                         expected:ConfigValueTest, code:Int, msg:String):Unit {
    assert( res.code == code)
    assert( res.msg == msg)
    assert( res.getOrElse{ null } == expected )
  }


  data class ConfigValueTest(val v1:String, val v2:String, val v3:Int, val v4:Double)


  class AppConfigTest(
                       args:Array<String>?
                     )  : App(AppContext.empty) {

    override fun execute():Try<Any> {
      val data = ConfigValueTest(
        ctx.env.name,
        ctx.cfg.getString("test_stri"),
        ctx.cfg.getInt("test_int"),
        ctx.cfg.getDouble("test_doub")
      )
      return Success(data)
    }
  }


  class AppErrorTest(
                      args:Array<String>?
                    )  : App(AppContext.empty) {

    override fun execute():Try<Any> {
      if(ctx.cfg != null ) {
        throw Exception("error test")
      }
      return Success("ok")
    }
  }



  fun runApp( call: (Array<String>?) -> Try<Any>)   {
    call(null)

    call(arrayOf())

    call(arrayOf("-a=1", "-b=2"))
  }



  fun assertResult(res:Try<Any>, value:String, code:Int, msg:String) {
    assert( res.getOrElse { null } == value)
    assert( res.code == code)
    assert( res.msg == msg)
  }


  fun checkHelp(words:Array<String>, code:Int, msg:String) {
    for(word in words){
      val res = AppRunner.run(AppArgsSchemaBasic1Required(arrayOf(word)))
      assertResultBasic(res, code, msg)
    }
  }



  fun assertResultBasic(res:Try<Any>, code:Int, msg:String) {
    assert( res.code == code)
    assert( res.msg == msg)
  }


  /**
   * Case: No schema
   */
  class AppArgsSchemaNull(args:Array<String>?)  : App(AppContext.empty) {

    override fun execute():Try<Any> = Success("ok", msg ="schema null")
  }


  /**
   * Case: Empty schema
   * @param schema
   */
  class AppArgsSchemaEmpty(
                            args  : Array<String>?,
                            schema: ArgsSchema? = ArgsSchema(listOf<Arg>())
                          )  : App(AppContext.empty) {

    override fun execute():Try<Any> = Success("ok", msg ="schema empty")
  }


  /**
   * Case args - none required
   * @param schema
   */
  class AppArgsSchemaBasicNoneRequired
  (
          args  : Array<String>?,
          schema: ArgsSchema? = ArgsSchema()
                  .text("env"        , "the environment to run in", false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
                  .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")
                  .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")
  )
    : App(AppContext.empty) {

    override fun execute():Try<Any> = Success("ok", msg ="schema basic")
  }


  /**
   * Case args - 1 required
   * @param schema
   */
  class AppArgsSchemaBasic1Required
  (
          args  : Array<String>?,
          schema: ArgsSchema? = ArgsSchema()
                  .text("env"        , "the environment to run in", true, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
                  .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")
                  .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")
  )
    : App(AppContext.empty) {

    override fun execute():Try<Any> = Success("ok", msg ="schema args 1")
  }


  class AppArgsSchemaBasicAllRequired
  (
          args  : Array<String>?,
          schema: ArgsSchema? = ArgsSchema()
                  .text("env"        , "the environment to run in", true, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
                  .text("region"     , "the region linked to app" , true, "us"   , "us"   , "us|europe|india|*")
                  .text("log.level"  , "the log level for logging", true, "info" , "info" , "debug|info|warn|error")
  )
    : App(AppContext.empty) {

    override fun execute():Try<Any> = Success("ok")
  }
}
