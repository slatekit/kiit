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

import org.scalatest.{FunSpec, BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.envs.Env
import slate.common.results.ResultCode
import slate.common.{SuccessResult, Result}
import slate.common.args.{Arg, ArgsSchema}
import slate.core.app.{AppFuncs, AppRunner, AppProcess}

class AppTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }



  describe("App Inputs") {

    it("can run process with null args schema without raw args") {
      runApp((args) => {
        val res = AppRunner.run(new AppArgsSchemaNull(args))
        assertResult(res, "ok", 200, "schema null")
        res
      })
    }


    it("can run process with empty args schema") {
      runApp((args) => {
        val res = AppRunner.run( new AppArgsSchemaEmpty(args))
        assertResult(res, "ok", 200, "schema empty")
        res
      })
    }


    it("can run process with empty args defined") {
      runApp((args) => {
        val res = AppRunner.run(new AppArgsSchemaBasicNoneRequired(args))
        assertResult(res, "ok", 200, "schema basic")
        res
      })
    }
  }


  describe("App Help") {

    it("can request help") {
      checkHelp(Array[String]("help", "-help", "--help", "/help", "?"), ResultCode.HELP, "help")
    }


    it("can request about") {
      checkHelp(Array[String]("about", "-about", "--about", "/about", "info"), ResultCode.HELP, "help")
    }


    it("can request version") {
      checkHelp(Array[String]("version", "-version", "--version", "/version", "ver"),ResultCode.HELP,  "help")
    }


    it("can request exit") {
      checkHelp(Array[String]("exit", "-exit", "--exit", "/exit", "exit"),ResultCode.EXIT, "exit")
    }


    def checkHelp(words:Array[String], code:Int, msg:String):Unit = {
      for(word <- words){
        val res = AppRunner.run(new AppArgsSchemaBasic1Required(Some(Array[String](word))))
        assertResultBasic(res, code, msg)
      }
    }
  }


  describe("App Args") {

    it("can run process with args defined and required and missing") {
      val res = AppRunner.run(new AppArgsSchemaBasic1Required( Some(Array[String]())))
      assertResultBasic(res, ResultCode.BAD_REQUEST, "invalid arguments supplied")
    }


    it("can run process with args defined and required and supplied") {
      val res = AppRunner.run(new AppArgsSchemaBasic1Required(Some(Array[String]("-env='loc'"))))
      assertResult(res, "ok", 200, "schema args 1")
    }
  }


  describe("App Envs") {

    it("can run process with env correct") {
      val res = AppRunner.run(new AppArgsSchemaBasic1Required( Some(Array[String]("-env='loc'"))))
      assertResult(res, "ok", 200, "schema args 1")
    }


    it("can run process with env incorrect") {
      val res = AppRunner.run(new AppArgsSchemaBasic1Required(Some(Array[String]("-env='abc'"))))
      assertResultBasic(res, 400, "Unknown environment name : abc supplied")
    }


    /*
    it("can run process with env custom") {
      val app = new AppCustomEnvs()
      val res = AppRunner.run(app, Some(Array[String]("-env='beta'")))
      assertResult(res, "ok", 200, "schema custom envs")
    }
    */
  }


  describe("App Config") {

    it("can select and use env local") {
      val res = AppRunner.run(new AppConfigTest(Some(Array[String]("-env='loc'"))))
      val res2 = res.asInstanceOf[Result[(String,String,Int,Double)]]
      assertConfigResult(res2, ("loc", "env loc", 1, 20.1), 200, null)
    }

    it("can select and use env dev") {
      val res = AppRunner.run(new AppConfigTest(Some(Array[String]("-env='dev'"))))
      val res2 = res.asInstanceOf[Result[(String,String,Int,Double)]]
      assertConfigResult(res2, ("dev", "env dev", 2, 20.2), 200, null)
    }

    it("can select and use env qa1") {
      val res = AppRunner.run(new AppConfigTest(Some(Array[String]("-env='qa1'"))))
      val res2 = res.asInstanceOf[Result[(String,String,Int,Double)]]
      assertConfigResult(res2, ("qa1", "env qa1", 3, 20.3), 200, null)
    }

    it("can select and use env qa2") {
      val res = AppRunner.run(new AppConfigTest(Some(Array[String]("-env='qa2'"))))
      val res2 = res.asInstanceOf[Result[(String,String,Int,Double)]]
      assertConfigResult(res2, ("qa2", "env qa2", 4, 20.4), 200, null)
    }
  }


  describe("App Error") {

    it("can handle unexpected error") {
      val res = AppRunner.run(new AppErrorTest(Some(Array[String]("-env='loc'"))))
      assertResultBasic(res, 500, "Unexpected error : error test")
    }
  }


  def runApp( call: (Option[Array[String]] => Result[Any])) :Unit = {
    call(null)

    call(Some(Array[String]()))

    call(Some(Array[String]("-a=1", "-b=2")))
  }


  def assertConfigResult(res:Result[(String,String,Int,Double)],
                         expected:(String,String,Int,Double), code:Int, msg:String):Unit = {
    assert( res.code == code)
    assert( res.msg == Option(msg))
    assert( res.get._1 == expected._1)
    assert( res.get._2 == expected._2)
    assert( res.get._3 == expected._3)
    assert( res.get._4 == expected._4)
  }


  def assertResult(res:Result[Any], value:String, code:Int, msg:String):Unit = {
    assert( res.get == value)
    assert( res.code == code)
    assert( res.msg == Some(msg))
  }


  def assertResultBasic(res:Result[Any], code:Int, msg:String):Unit = {
    assert( res.code == code)
    assert( res.msg == Some(msg))
  }


  /**
    * Case: No schema
    */
  class AppArgsSchemaNull(args:Option[Array[String]])  extends AppProcess(None, args) {

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200, Some("schema null"))
  }


  /**
    * Case: Empty schema
    * @param schema
    */
  class AppArgsSchemaEmpty
  (
      args:Option[Array[String]],
      schema: Option[ArgsSchema] = Some(new ArgsSchema(List[Arg]()))
  )  extends AppProcess(None, args, schema) {

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200, Some("schema empty"))
  }


  /**
    * Case args - none required
    * @param schema
    */
  class AppArgsSchemaBasicNoneRequired
  (
      args:Option[Array[String]],
      schema: Option[ArgsSchema]     = Some
      (
        new ArgsSchema()
          .text("env"        , "the environment to run in", false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
          .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")
          .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")
      )
  ) extends AppProcess(None, args, schema) {

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200, Some("schema basic"))
  }


  /**
    * Case args - 1 required
    * @param schema
    */
  class AppArgsSchemaBasic1Required
  (
    args:Option[Array[String]],
    schema: Option[ArgsSchema]     = Some
    (
      new ArgsSchema()
      .text("env"        , "the environment to run in", true, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
      .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")
      .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")
    )
  ) extends AppProcess(None, args, schema) {

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200, Some("schema args 1"))
  }


  class AppArgsSchemaBasicAllRequired
  (
    args:Option[Array[String]],
    schema: Option[ArgsSchema]     = Some
    (
      new ArgsSchema()
      .text("env"        , "the environment to run in", true, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
      .text("region"     , "the region linked to app" , true, "us"   , "us"   , "us|europe|india|*")
      .text("log.level"  , "the log level for logging", true, "info" , "info" , "debug|info|warn|error")
    )
  ) extends AppProcess(None, args, schema) {

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200)
  }

/*
  class AppCustomEnvs extends AppProcess {
    override lazy val argsSchema = new ArgsSchema()
      .text("env"        , "the environment to run in", true, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
      .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")
      .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")

    override protected def envs(): List[EnvItem] = {
        List[EnvItem](
          EnvItem("loc"   , Env.DEV , desc = "Dev environment (local)" ),
          EnvItem("dev"   , Env.DEV , desc = "Dev environment (shared)" ),
          EnvItem("qa1"   , Env.QA  , desc = "QA environment  (current release)" ),
          EnvItem("beta"  , Env.QA  , desc = "QA environment  (beta release)" ),
          EnvItem("stg"   , Env.UAT , desc = "STG environment (demo)" ),
          EnvItem("pro"   , Env.PROD, desc = "LIVE environment" )
        )
    }

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200, Some("schema custom envs"))
  }
  */


  class AppConfigTest(
                       args:Option[Array[String]]
                     )  extends AppProcess(None, args) {

    override def onExecute():Result[Any] = {
      val data = (
        ctx.env.name,
        conf.getString("test.string"),
        conf.getInt("test.integer"),
        conf.getDouble("test.double")
      )
      new SuccessResult[(String, String, Int, Double)](data, 200)
    }
  }


  class AppErrorTest(
                      args:Option[Array[String]]
                    ) extends AppProcess(None, args) {

    override def onExecute():Result[Any] = {
      if(conf != null ) {
        throw new Exception("error test")
      }
      new SuccessResult[String]("ok", 200)
    }
  }
}
