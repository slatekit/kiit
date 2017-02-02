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

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite}

import slate.common.DateTime
import slate.common.envs.EnvItem
import slate.common.serialization.{SerializerProps, SerializerJson, SerializerCsv}
import slate.test.common.User


class SerializerTests  extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  def sampleUser():User = {
    val user = new User()
    user.email     = "kishore@abc.com"
    user.firstName = "kishore"
    user.lastName  = "red"
    //user.id        = 2
    user.isMale    = true
    user.age       = 37
    user.createdAt = DateTime.now
    user
  }


  test("can serialize model to props") {
    val user = sampleUser()
    val serializer = new SerializerProps()
    val content = serializer.serialize(user)
    println(content)
  }


  test("can serialize model to CSV") {
    val user = sampleUser()
    val serializer = new SerializerCsv()
    val content = serializer.serialize(user)
    println(content)
    println()
  }


  test("can serialize model to json") {
    val user = sampleUser()
    val serializer = new SerializerJson()
    val content = serializer.serialize(user)
    println(content)
  }


  test("can serialize case class to props") {
    val env = new EnvItem("qa1", "QA", "qa1:QA")
    val serializer = new SerializerProps()
    val content = serializer.serialize(env)
    println(content)
  }


  test("can serialize case class to CSV") {
    val env = new EnvItem("qa1", "QA", "qa1:QA")
    val serializer = new SerializerCsv()
    val content = serializer.serialize(env)
    println(content)
    println()
  }


  test("can serialize case class to json") {
    val env = new EnvItem("qa1", "QA", "qa1:QA")
    val serializer = new SerializerJson()
    val content = serializer.serialize(env)
    println(content)
  }
}