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
package slate.test

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSpec}
import slate.core.cache.{CacheSettings, Cache}
import scala.concurrent.{ExecutionContext}
import scala.util.{Failure, Success}

class CacheTests extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  //import ExecutionContext.Implicits.global
  val ctx = scala.concurrent.ExecutionContext.global

  before {
    println("before")
  }


  describe("core") {

    it("can put item") {
      val cache = new Cache(new CacheSettings(10), ctx)
      cache.put("countries", Some("countries supported for mobile app"), None, () => {
        Some(List("us", "ca"))
      })

      Thread.sleep(300)
      val result = cache.get[List[String]]("countries")
      assert(result.get.length == 2)
      assert(result.get(0) == "us")
      assert(result.get(1) == "ca")
    }


    it("can get or load item") {
      val cache = new Cache(new CacheSettings(10), ctx)
      cache.put("countries", Some("countries supported for mobile app"), None, () => {
        Some(List("us", "ca"))
      })

      Thread.sleep(300)
      val result = cache.get[List[String]]("countries")
      assert(result.get.length == 2)
      assert(result.get(0) == "us")
      assert(result.get(1) == "ca")
    }


    it("can refresh item") {
      val cache = new Cache(new CacheSettings(10), ctx)
      cache.put("countries", Some("countries supported for mobile app"), None, () => {
        Some(List("us", "ca"))
      })

      Thread.sleep(400)
      val result1 = cache.getCacheItem("countries")

      // Refresh
      cache.refresh("countries")
      Thread.sleep(300)
      val result2 = cache.getCacheItem("countries")

      assert(result1.isDefined)
      assert(result2.isDefined)
      assert(result1.get.updated.get < result2.get.updated.get)
    }


    it("can refresh and get result") {
      val cache = new Cache(new CacheSettings(10), ctx)
      cache.put("countries", Some("countries supported for mobile app"), None, () => {
        Some(List("us", "ca"))
      })

      Thread.sleep(400)
      val result1 = cache.getCacheItem("countries")

      // Refresh
      val result = cache.getFresh[List[String]]("countries")
      result.onComplete {
        case Success(s) => {
          val result2 = cache.getCacheItem("countries")
          println(s)
          assert(result1.isDefined)
          assert(result2.isDefined)
          assert(result1.get.updated.get < result2.get.updated.get)
        }
        case Failure(e) => {

        }
      }(ctx)
      Thread.sleep(300)
    }
  }
}
