package slate.test

import slate.common.reflect.{ReflectedClass, ReflectedClassT}
import slate.tests.common.MyAppContext
import org.scalatest.{FunSpec, BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.{InputArgs, DateTime, Field, Reflector}
import slate.core.apis.{Api, ApiAction}
import slate.test.common.{UserNormal1, User2, User, UserApi}
import scala.reflect.runtime.universe.{typeOf}

/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */


class ReflectorTests extends  FunSpec with BeforeAndAfter with BeforeAndAfterAll {


  describe("Normal Class") {


    it("can create normal class") {
      val inst = Reflector.createInstance(typeOf[UserNormal1])
      assert(inst.isInstanceOf[UserNormal1])
    }


    it("can get a field value declared as var") {
      val user = new User()
      user.email = "johndoe@home.com"
      val email = Reflector.getFieldValue(user, "email")
      assert(email == user.email)
    }


    it("can set a field value declared as var") {
      val user = new User()
      Reflector.setFieldValue(user, "email", "johndoe@work.com")
      assert(user.email == "johndoe@work.com")
    }


    it("can get method") {
      val ent = MyAppContext.sample.ent
      ent.register[User](false, typeOf[User], serviceCtx = Some(MyAppContext.sample))
      val api = new UserApi(MyAppContext.sample)
      val sym = Reflector.getMethod(api, "info")
      assert(sym.name.toTermName.toString == "info")
    }


    it("can get method parameters") {

      val api = new UserApi(MyAppContext.sample)
      val sym = Reflector.getMethod(api, "create")
      val result = Reflector.getMethodParameters(sym)
      assert(result.size == 5)
    }
  }


  describe("Case Class") {

    it("can create") {
      val inst = Reflector.createInstance(typeOf[User2],
        Some(Seq("batman@gotham.com", "bruce", "wayne", true, 30, 0, "abc", DateTime.now, 0, DateTime.now, 0)))
      assert(inst.isInstanceOf[User2])
    }


    it("can create dynamically") {
      val timeStamp = DateTime.now
      val inputs = new InputArgs(Map[String,Any](
          "email"     -> "batman@gotham.com",
          "firstName" -> "bruce",
          "lastName"  -> "wayne",
          "isMale"    -> true,
          "age"       -> 30,
          "id"        -> 0,
          "uniqueId"  -> "abc",
          "createdAt" -> timeStamp,
          "createdBy" -> 0,
          "updatedAt" -> timeStamp,
          "updatedBy" -> 0
        )
      )
      val rcls = new ReflectedClass(typeOf[User2])
      val inst = rcls.create(inputs).asInstanceOf[User2]
      assert(inst.email     == "batman@gotham.com")
      assert(inst.firstName == "bruce")
      assert(inst.lastName  == "wayne")
      assert(inst.isMale    == true)
      assert(inst.age       == 30)
      assert(inst.id        == 0)
      assert(inst.uniqueId  == "abc")
      assert(inst.createdAt == timeStamp)
      assert(inst.createdBy == 0)
      assert(inst.updatedAt == timeStamp)
      assert(inst.updatedBy == 0)
    }


    it("can get a field value") {
      val user:User2 = new User2(email = "johndoe@home.com" )
      val email = Reflector.getFieldValue(user, "email")
      assert(email == user.email)
    }


    it("can get fields") {
      val user = new User2(email = "johndoe@abc.com")
      val fields = Reflector.getFields[User2]()

      assert(fields.size == 11)
      assert(fields(0).asTerm.name.toString == "email")

      val email:String = Reflector.getFieldValue(user, fields(0).asTerm.name.toString).toString
      assert( email == "johndoe@abc.com")
    }
  }



  describe("Annotations"){

    it("can get a class level annotation") {
      // NOTE: The annotation must be created with all parameters ( not named parameters )
      val anno = Reflector.getClassAnnotation(typeOf[UserApi], typeOf[Api])
        .asInstanceOf[Option[Any]].get.asInstanceOf[Api]
      assert(anno.area == "app")
      assert(anno.name == "users")
      assert(anno.roles == "admin")
      assert(anno.protocol == "*")
      assert(anno.auth == "app-roles")
    }


    it("can get a method level annotation") {
      // NOTE: The annotation must be created with all parameters
      val anno = Reflector.getMemberAnnotation(typeOf[UserApi], typeOf[ApiAction], "activate")
        .asInstanceOf[Option[Any]].get.asInstanceOf[ApiAction]
      assert(anno.name == "activate")
      assert(anno.roles == "@parent")
    }


    it("can get a method level annotation with verb") {
      // NOTE: The annotation must be created with all parameters
      val anno = Reflector.getMemberAnnotation(typeOf[UserApi], typeOf[ApiAction], "info")
        .asInstanceOf[Option[Any]].get.asInstanceOf[ApiAction]
      assert(anno.name == "")
      assert(anno.roles == "")
      assert(anno.verb == "get")
    }


    it("can get a field level annotation") {
      // NOTE: The annotation must be created with all parameters
      val anno = Reflector.getFieldAnnotation(typeOf[User], typeOf[Field], "email")
        .asInstanceOf[Option[Any]].get.asInstanceOf[Field]
      assert(anno.name == "")
      assert(anno.length == 30)
      assert(anno.required)
    }
  }


  describe("Execution"){

    it("can call a method with parameters") {

      val ent = MyAppContext.sample.ent
      ent.register[User](false, typeOf[User], serviceCtx = Some(MyAppContext.sample))

      val api = new UserApi(MyAppContext.sample)
      val result = Reflector.callMethod(api, "create", Array[Any]("superman@metro.com", "super", "man", true, 35))
      assert(api.user.email == "superman@metro.com")
      assert(api.user.firstName == "super")
      assert(api.user.lastName == "man")
      assert(api.user.isMale)
      assert(api.user.age == 35)
    }

  }


  describe("Reflected") {

    it("can get full name"){
      val re = new ReflectedClassT[User]()
      assert( re.name == "User" )
      assert( re.fullname == "slate.test.common.User" )
    }


    it("can get method"){
      val re = new ReflectedClassT[User]()
      assert( re.name == "User" )
      assert( re.fullname == "slate.test.common.User" )
    }


    it("can get a field value") {
      val re = new ReflectedClassT[User]()
      val user = new User()
      user.email = "johndoe@home.com"
      val email = re.getValue(user, "email")
      assert(email == user.email)
    }


    it("can set a field value") {
      val re = new ReflectedClassT[User]()
      val user = new User()
      re.setValue(user, "email", "johndoe@work.com")
      assert(user.email == "johndoe@work.com")
    }
  }


 /*

  test("can determine if basic type") {
    val argType = symArgs(0)
    println(argType.isBasicType())
  }

  test("can create instance from parameter") {
    // CASE 13: Create instance from parameter
    val argInstance = Reflector.createInstance(symArgs(0).asType())
    println(argInstance)
  }
  */
}


