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
import slate.common.envs.{Env, EnvItem}
import slate.common.results.ResultCode
import slate.common.{SuccessResult, Result}
import slate.common.args.{Arg, ArgsSchema}
import slate.common.logging.{LogEntry, LoggerBase, LogLevel}
import slate.core.app.{AppFuncs, AppRunner, AppProcess}


class AppTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }



  describe("App Inputs") {

    it("can run process with null args schema without raw args") {
      val app = new AppArgsSchemaNull()
      runApp((args) => {
        val res = AppRunner.run(app, args)
        assertResult(res, "ok", 200, "schema null")
        res
      })
    }


    it("can run process with empty args schema") {
      val app = new AppArgsSchemaEmpty()
      runApp((args) => {
        val res = AppRunner.run(app, args)
        assertResult(res, "ok", 200, "schema empty")
        res
      })
    }


    it("can run process with empty args defined") {
      val app = new AppArgsSchemaBasicNoneRequired()
      runApp((args) => {
        val res = AppRunner.run(app, args)
        assertResult(res, "ok", 200, "schema basic")
        res
      })
    }
  }


  describe("App Help") {

    it("can request help") {
      checkHelp(Array[String]("help", "-help", "--help", "/help", "?"))
    }


    it("can request about") {
      checkHelp(Array[String]("about", "-about", "--about", "/about", "info"))
    }


    it("can request version") {
      checkHelp(Array[String]("version", "-version", "--version", "/version", "ver"))
    }


    def checkHelp(words:Array[String]):Unit = {
      for(word <- words){
        val app = new AppArgsSchemaBasic1Required()
        val res = AppRunner.run(app, Some(Array[String](word)))
        assertResultBasic(res, ResultCode.HELP, "success")
      }
    }
  }


  describe("App Args") {

    it("can run process with args defined and required and missing") {
      val app = new AppArgsSchemaBasic1Required()
      val res = AppRunner.run(app, Some(Array[String]()))
      assertResultBasic(res, ResultCode.BAD_REQUEST, "invalid arguments supplied")
    }


    it("can run process with args defined and required and supplied") {
      val app = new AppArgsSchemaBasic1Required()
      val res = AppRunner.run(app, Some(Array[String]("-env='loc'")))
      assertResult(res, "ok", 200, "schema args 1")
    }
  }


  describe("App Envs") {

    it("can run process with env correct") {
      val app = new AppArgsSchemaBasic1Required()
      val res = AppRunner.run(app, Some(Array[String]("-env='loc'")))
      assertResult(res, "ok", 200, "schema args 1")
    }


    it("can run process with env incorrect") {
      val app = new AppArgsSchemaBasic1Required()
      val res = AppRunner.run(app, Some(Array[String]("-env='abc'")))
      assertResultBasic(res, 400, "Unexpected error running application: Unknown environment name : abc supplied")
    }


    it("can run process with env custom") {
      val app = new AppCustomEnvs()
      val res = AppRunner.run(app, Some(Array[String]("-env='beta'")))
      assertResult(res, "ok", 200, "schema custom envs")
    }
  }


  describe("App Config") {

    it("can select and use env local") {
      val app = new AppConfigTest()
      val res = AppRunner.run(app, Some(Array[String]("-env='loc'")))
      val res2 = res.asInstanceOf[Result[(String,String,Int,Double)]]
      assertConfigResult(res2, ("loc", "env loc", 1, 20.1), 200, null)
    }

    it("can select and use env dev") {
      val app = new AppConfigTest()
      val res = AppRunner.run(app, Some(Array[String]("-env='dev'")))
      val res2 = res.asInstanceOf[Result[(String,String,Int,Double)]]
      assertConfigResult(res2, ("dev", "env dev", 2, 20.2), 200, null)
    }

    it("can select and use env qa1") {
      val app = new AppConfigTest()
      val res = AppRunner.run(app, Some(Array[String]("-env='qa1'")))
      val res2 = res.asInstanceOf[Result[(String,String,Int,Double)]]
      assertConfigResult(res2, ("qa1", "env qa1", 3, 20.3), 200, null)
    }

    it("can select and use env qa2") {
      val app = new AppConfigTest()
      val res = AppRunner.run(app, Some(Array[String]("-env='qa2'")))
      val res2 = res.asInstanceOf[Result[(String,String,Int,Double)]]
      assertConfigResult(res2, ("qa2", "env qa2", 4, 20.4), 200, null)
    }
  }


  describe("App Error") {

    it("can handle unexpected error") {
      val app = new AppErrorTest()
      val res = AppRunner.run(app, Some(Array[String]("-env='loc'")))
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




  class AppArgsSchemaNull extends AppProcess {
    override lazy val argsSchema = null

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200, Some("schema null"))
  }


  class AppArgsSchemaEmpty extends AppProcess {
    override lazy val argsSchema = new ArgsSchema(List[Arg]())

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200, Some("schema empty"))
  }


  class AppArgsSchemaBasicNoneRequired extends AppProcess {
    override lazy val argsSchema = new ArgsSchema()
      .text("env"        , "the environment to run in", false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
      .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")
      .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200, Some("schema basic"))
  }


  class AppArgsSchemaBasic1Required extends AppProcess {
    override lazy val argsSchema = new ArgsSchema()
      .text("env"        , "the environment to run in", true, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
      .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")
      .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200, Some("schema args 1"))
  }


  class AppArgsSchemaBasicAllRequired extends AppProcess {
    override lazy val argsSchema = new ArgsSchema()
      .text("env"        , "the environment to run in", true, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
      .text("region"     , "the region linked to app" , true, "us"   , "us"   , "us|europe|india|*")
      .text("log.level"  , "the log level for logging", true, "info" , "info" , "debug|info|warn|error")

    override def onExecute():Result[Any] = new SuccessResult[String]("ok", 200)
  }


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


  class AppConfigTest extends AppProcess {
    override lazy val argsSchema = new ArgsSchema()
      .text("env"        , "the environment to run in", false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )

    override def onExecute():Result[Any] = {
      val data = (
        env.name,
        conf.getString("test.string"),
        conf.getInt("test.integer"),
        conf.getDouble("test.double")
      )
      new SuccessResult[(String, String, Int, Double)](data, 200)
    }
  }


  class AppErrorTest extends AppProcess {
    override lazy val argsSchema = new ArgsSchema()
      .text("env"        , "the environment to run in", false, "dev"  , "dev"  , "dev1|qa1|stg1|pro" )
      .text("region"     , "the region linked to app" , false, "us"   , "us"   , "us|europe|india|*")
      .text("log.level"  , "the log level for logging", false, "info" , "info" , "debug|info|warn|error")

    override def onExecute():Result[Any] = {
      if(conf != null ) {
        throw new Exception("error test")
      }
      new SuccessResult[String]("ok", 200)
    }
  }
}
