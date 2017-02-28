package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.envs._

/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

class EnvTests  extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  private def ensureMatch(envs:Envs, name:String, envType:EnvMode, desc:String): Unit ={
    val env = envs(name)
    assert( env.isDefined )
    assert( env.get.name == name )
    assert( env.get.mode.name == envType.name)
    assert( env.get.desc == desc )
  }


  test("can construct") {
    val env = new  Env("qa1", Qa, "nyc")
    assert( env.name      == "qa1" )
    assert( env.mode.name == "qa"  )
    assert( env.region    == "nyc" )
  }


  test("default envs can be created") {
    val envs = Env.defaults()
    ensureMatch( envs, "loc", Dev , "Dev environment (local)" )
    ensureMatch( envs, "dev", Dev , "Dev environment (shared)" )
    ensureMatch( envs, "qa1", Qa  , "QA environment  (current release)" )
    ensureMatch( envs, "qa2", Qa  , "QA environment  (last release)" )
    ensureMatch( envs, "stg", Uat , "STG environment (demo)" )
    ensureMatch( envs, "pro", Prod, "LIVE environment" )
  }


  test("default envs have a default current environment") {
    val envs = Env.defaults()
    assert( envs.current.isDefined )
    assert( envs.name == "loc")
    assert( envs.env == Dev.name )
    assert( envs.isDev )
  }


  test("default envs: can select environment") {
    val envAll = Env.defaults()
    val envs = envAll.select("qa1")
    assert( envs.current.isDefined )
    assert( envs.name == "qa1")
    assert( envs.env == Qa.name )
    assert( envs.isQa )
  }


  test("default envs: can validate an env against defaults") {
    val envAll = Env.defaults()
    assert( envAll.isValid("qa1") )
    assert( !envAll.isValid("abc") )
  }


  test("can build key") {
    assert( Env("qa1", Qa).key == "qa1:qa" )
  }


  test("can check loc") {
    assert( Env("loc", Dev).isDev )
  }


  test("can check dev") {
    assert( Env("qa1", Qa).isQa )
  }


  test("can check qa") {
    assert( Env("qa1", Qa).isQa )
  }


  test("can check stg") {
    assert( Env("stg", Uat).isUat )
  }


  test("can check pro") {
    assert( Env("pro", Prod).isProd )
  }

}
