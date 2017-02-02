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

import scala.reflect.runtime.universe.{typeOf,Type}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSpec}
import slate.common.Random._
import slate.common.{ListMap, DateTime, Field}
import slate.common.mapper.{MapperSourceReaderMap, Mapper}
import slate.entities.core.EntityMapper

import scala.annotation.meta.field

class MapperTests  extends FunSpec  with BeforeAndAfter with BeforeAndAfterAll {

  before {
    println("before")
  }


  describe ( "Case Classes") {

    it("can register by key") {
      val mod = new AuthorR(email = "kishore@abc.com")
      val model = Mapper.loadSchema(typeOf[AuthorR])

      model.fields.foreach(println)

      val mapper = new EntityMapper(model)
      val sqlCreate = mapper.mapToSql(mod, false, true)
      println(sqlCreate)

      val sqlUpdate = mapper.mapToSql(mod, true, true)
      println(sqlUpdate)
    }


    it("can map to case class") {
      val mod = new AuthorR(email = "kishore@abc.com")
      val model = Mapper.loadSchema(typeOf[AuthorR])

      val mapper = new EntityMapper(model)
      val data = new ListMap[String,Any]()
      data.add("id", 1L)
      data.add("createdAt", new java.sql.Timestamp(2017, 1, 1, 12,0,0,0))
      data.add("createdBy", 100L)
      data.add("updatedAt", new java.sql.Timestamp(2017, 1, 2, 12,0,0,0))
      data.add("updatedBy", 101L)
      data.add("uniqueId", "ABC")
      data.add("email", "kishore@abc.com")
      data.add("isActive", true)
      data.add("age", 35)

      val source = new MapperSourceReaderMap(data)
      val entity = mapper.mapFrom(source).get.asInstanceOf[AuthorR]

      assert( entity.id == 1L )
      assert( entity.uniqueId == "ABC" )
      assert( entity.email == "kishore@abc.com" )
      assert( entity.isActive == true )
      assert( entity.age == 35 )
    }


    it("can map to var class") {
      val mod = new AuthorW()
      mod.email = "kishore@abc.com"
      val model = Mapper.loadSchema(typeOf[AuthorW])

      val mapper = new EntityMapper(model)
      val data = new ListMap[String,Any]()
      data.add("id", 1L)
      data.add("createdAt", new java.sql.Timestamp(2017, 1, 1, 12,0,0,0))
      data.add("createdBy", 100L)
      data.add("updatedAt", new java.sql.Timestamp(2017, 1, 2, 12,0,0,0))
      data.add("updatedBy", 101L)
      data.add("uniqueId", "ABC")
      data.add("email", "kishore@abc.com")
      data.add("isActive", true)
      data.add("age", 35)

      val source = new MapperSourceReaderMap(data)
      val entity = mapper.mapFrom(source).get.asInstanceOf[AuthorW]

      assert( entity.id == 1L )
      assert( entity.uniqueId == "ABC" )
      assert( entity.email == "kishore@abc.com" )
      assert( entity.isActive == true )
      assert( entity.age == 35 )
    }
  }
}


case class AuthorR
(
      id       : Long             = 0,

      @(Field@field)("", true, -1)
      createdAt: DateTime         = DateTime.now(),

      @(Field@field)("", true, -1)
      createdBy: Long             = 0,

      @(Field@field)("", true, -1)
      updatedAt: DateTime         = DateTime.now(),

      @(Field@field)("", true, -1)
      updatedBy: Long             = 0,

      @(Field@field)("", true, -1)
      uniqueId: String            = stringGuid(),

      @(Field@field)("", true, 30)
      email:String = "",

      @(Field@field)("", true, 30)
      isActive:Boolean = false,

      @(Field@field)("", true, 0)
      age:Int = 35
)


class AuthorW {
      var id: Long = 0

      @Field("", true, -1)
      var createdAt: DateTime = DateTime.now()

      @Field("", true, -1)
      var createdBy: Long = 0

      @Field("", true, -1)
      var updatedAt: DateTime = DateTime.now()

      @Field("", true, -1)
      var updatedBy: Long = 0

      @Field("", true, -1)
      var uniqueId: String = stringGuid()

      @Field("", true, 30)
      var email: String = ""

      @Field("", true, 30)
      var isActive: Boolean = false

      @Field("", true, 0)
      var age: Int = 35

}