package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.envs.{Env, Envs, EnvItem}

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

  private def ensureMatch(envs:Envs, name:String, envType:String, desc:String): Unit ={
    val env = envs(name)
    assert( env.isDefined )
    assert( env.get.name == name )
    assert( env.get.env == envType)
    assert( env.get.desc == desc )
  }


  test("can construct") {
    val env = new  EnvItem("qa1", "qa", "nyc")
    assert( env.name   == "qa1" )
    assert( env.env    == "qa"  )
    assert( env.region == "nyc" )
  }


  test("default envs can be created") {
    val envs = Env.defaults()
    ensureMatch( envs, "loc", Env.DEV , "Dev environment (local)" )
    ensureMatch( envs, "dev", Env.DEV , "Dev environment (shared)" )
    ensureMatch( envs, "qa1", Env.QA  , "QA environment  (current release)" )
    ensureMatch( envs, "qa2", Env.QA  , "QA environment  (last release)" )
    ensureMatch( envs, "stg", Env.UAT , "STG environment (demo)" )
    ensureMatch( envs, "pro", Env.PROD, "LIVE environment" )
  }


  test("default envs have a default current environment") {
    val envs = Env.defaults()
    assert( envs.current.isDefined )
    assert( envs.name == "loc")
    assert( envs.env == Env.DEV )
    assert( envs.isDev )
  }


  test("default envs: can select environment") {
    val envAll = Env.defaults()
    val envs = envAll.select("qa1")
    assert( envs.current.isDefined )
    assert( envs.name == "qa1")
    assert( envs.env == Env.QA )
    assert( envs.isQa )
  }


  test("default envs: can validate an env against defaults") {
    val envAll = Env.defaults()
    assert( envAll.isValid("qa1") )
    assert( !envAll.isValid("abc") )
  }


  test("can build key") {
    assert( EnvItem("qa1", "qa").key == "qa1:qa" )
  }


  test("can check loc") {
    assert( EnvItem("loc", Env.DEV).isDev )
  }


  test("can check dev") {
    assert( EnvItem("qa1", Env.QA).isQa )
  }


  test("can check qa") {
    assert( EnvItem("qa1", Env.QA).isQa )
  }


  test("can check stg") {
    assert( EnvItem("stg", Env.UAT).isUat )
  }


  test("can check pro") {
    assert( EnvItem("pro", Env.PROD).isProd )
  }

}
