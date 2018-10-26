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
import slatekit.common.Result
import slatekit.common.ResultEx
import slatekit.common.Success
import slatekit.common.args.Arg
import slatekit.common.args.ArgsSchema
import slatekit.common.getOrElse
import slatekit.common.results.ResultCode.BAD_REQUEST
import slatekit.common.results.ResultCode.EXIT
import slatekit.common.results.ResultCode.HELP
import slatekit.common.results.ResultFuncs.success
import slatekit.core.app.AppProcess
import slatekit.core.app.AppRunner


class AppTests  {


  @Test fun can_request_help() {
    checkHelp(arrayOf("help", "-help", "--help", "/help", "?"), HELP, "help")
  }


  @Test fun can_request_about() {
    checkHelp(arrayOf("about", "-about", "--about", "/about", "info"), HELP, "help")
  }


  @Test fun can_request_version() {
    checkHelp(arrayOf("version", "-version", "--version", "/version", "ver"),HELP,  "help")
  }


  @Test fun can_request_exit() {
    checkHelp(arrayOf("exit", "-exit", "--exit", "/exit", "exit"),EXIT, "exit")
  }


  @Test fun can_run_process_with_null_args_schema_without_raw_args() {
    runApp({ args ->
      val res = AppRunner.run(AppArgsSchemaNull(args))
      assertResult(res, "ok", 200, "schema null")
      res
    })
  }


  @Test fun can_run_process_with_empty_args_schema() {
    runApp({ args ->
      val res = AppRunner.run( AppArgsSchemaEmpty(args))
      assertResult(res, "ok", 200, "schema empty")
      res
    })
  }


  @Test fun can_run_process_with_empty_args_defined() {
    runApp({ args ->
      val res = AppRunner.run(AppArgsSchemaBasicNoneRequired(args))
      assertResult(res, "ok", 200, "schema basic")
      res
    })
  }


  @Test fun can_run_process_with_args_defined_and_required_and_missing() {
    val res = AppRunner.run(AppArgsSchemaBasic1Required( arrayOf<String>()))
    assertResultBasic(res, BAD_REQUEST, "invalid arguments supplied: Missing : env")
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
      val res2 = res as ResultEx<ConfigValueTest>
      assertConfigResult(res2, ConfigValueTest("loc", "env loc", 1, 20.1), 200, "success")
    }

    @Test fun can_select_and_use_env_dev() {
      val res = AppRunner.run(AppConfigTest(arrayOf("-env='dev'")))
      val res2 = res as ResultEx<ConfigValueTest>
      assertConfigResult(res2, ConfigValueTest("dev", "env dev", 2, 20.2), 200, "success")
    }

    @Test fun can_select_and_use_env_qa1() {
      val res = AppRunner.run(AppConfigTest(arrayOf("-env='qa1'")))
      val res2 = res as ResultEx<ConfigValueTest>
      assertConfigResult(res2, ConfigValueTest("qa1", "env qa1", 3, 20.3), 200, "success")
    }

    @Test fun can_select_and_use_env_qa2() {
      val res = AppRunner.run(AppConfigTest(arrayOf("-env='qa2'")))
      val res2 = res as ResultEx<ConfigValueTest>
      assertConfigResult(res2, ConfigValueTest("qa2", "env qa2", 4, 20.4), 200, "success")
    }
  

    @Test fun can_handle_unexpected_error() {
      val res = AppRunner.run(AppErrorTest(arrayOf("-env='loc'")))
      assertResultBasic(res, 500, "Unexpected error : error test")
    }


  fun assertConfigResult(res:ResultEx<ConfigValueTest>,
                         expected:ConfigValueTest, code:Int, msg:String):Unit {
    assert( res.code == code)
    assert( res.msg == msg)
    assert( res.getOrElse{ null } == expected )
  }


  data class ConfigValueTest(val v1:String, val v2:String, val v3:Int, val v4:Double)


  class AppConfigTest(
                       args:Array<String>?
                     )  : AppProcess(null, args) {

    override fun onExecute():ResultEx<Any> {
      val data = ConfigValueTest(
        ctx.env.name,
        conf.getString("test_stri"),
        conf.getInt("test_int"),
        conf.getDouble("test_doub")
      )
      return Success(data)
    }
  }


  class AppErrorTest(
                      args:Array<String>?
                    )  : AppProcess(null, args) {

    override fun onExecute():ResultEx<Any> {
      if(conf != null ) {
        throw Exception("error test")
      }
      return Success("ok")
    }
  }



  fun runApp( call: (Array<String>?) -> ResultEx<Any>) :Unit  {
    call(null)

    call(arrayOf<String>())

    call(arrayOf("-a=1", "-b=2"))
  }



  fun assertResult(res:ResultEx<Any>, value:String, code:Int, msg:String):Unit {
    assert( res.getOrElse { null } == value)
    assert( res.code == code)
    assert( res.msg == msg)
  }


  fun checkHelp(words:Array<String>, code:Int, msg:String):Unit {
    for(word in words){
      val res = AppRunner.run(AppArgsSchemaBasic1Required(arrayOf(word)))
      assertResultBasic(res, code, msg)
    }
  }



  fun assertResultBasic(res:ResultEx<Any>, code:Int, msg:String):Unit {
    assert( res.code == code)
    assert( res.msg == msg)
  }


  /**
   * Case: No schema
   */
  class AppArgsSchemaNull(args:Array<String>?)  : AppProcess(null, args) {

    override fun onExecute():ResultEx<Any> = Success("ok", msg ="schema null")
  }


  /**
   * Case: Empty schema
   * @param schema
   */
  class AppArgsSchemaEmpty(
                            args  : Array<String>?,
                            schema: ArgsSchema? = ArgsSchema(listOf<Arg>())
                          )  : AppProcess(null, args, schema) {

    override fun onExecute():ResultEx<Any> = Success("ok", msg ="schema empty")
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
    : AppProcess(null, args, schema) {

    override fun onExecute():ResultEx<Any> = Success("ok", msg ="schema basic")
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
    : AppProcess(null, args, schema) {

    override fun onExecute():ResultEx<Any> = Success("ok", msg ="schema args 1")
  }


  class AppArgsSchemaBasicAllRequired
  (
          args  : Array<String>?,
          schema: ArgsSchema? = ArgsSchema()
                  .text("env"        , "the environment to run in", true, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
                  .text("region"     , "the region linked to app" , true, "us"   , "us"   , "us|europe|india|*")
                  .text("log.level"  , "the log level for logging", true, "info" , "info" , "debug|info|warn|error")
  )
    : AppProcess(null, args, schema) {

    override fun onExecute():ResultEx<Any> = Success("ok")
  }
}
