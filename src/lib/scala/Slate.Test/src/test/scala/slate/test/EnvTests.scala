package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.envs.{Envs, EnvItem}

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

  test("can construct") {
    val env = new  EnvItem("qa1", "qa", "nyc")
    assert( env.name   == "qa1" )
    assert( env.env    == "qa"  )
    assert( env.region == "nyc" )
  }


  test("can build key") {
    assert( EnvItem("qa1", "qa").key == "qa1:qa" )
  }


  test("can check loc") {
    assert( EnvItem("loc", Envs.DEV).isDev )
  }


  test("can check dev") {
    assert( EnvItem("qa1", Envs.QA).isQa )
  }


  test("can check qa") {
    assert( EnvItem("qa1", Envs.QA).isQa )
  }


  test("can check stg") {
    assert( EnvItem("stg", Envs.UAT).isUat )
  }


  test("can check pro") {
    assert( EnvItem("pro", Envs.PROD).isProd )
  }

}
