package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.{DateTime, Field, Reflector}
import slate.core.apis.{Api, ApiAction}
import slate.entities.core.EntityUnique
import slate.test.common.{User2, User, UserApi}
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


class ReflectorTests extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  test("can create class normal") {
    val inst = Reflector.createInstance(typeOf[User])
    assert( inst.isInstanceOf[User])
  }


  test("can create class case") {
    val inst = Reflector.createInstance(typeOf[User2], Some(Seq("batman@gotham.com", "bruce", "wayne", true, 30, 0, "abc", DateTime.now, 0, DateTime.now, 0)))
    assert( inst.isInstanceOf[User2])
  }


  test("can get a field value") {
    val user = new User()
    user.email = "johndoe@home.com"
    val email = Reflector.getFieldValue(user, "email")
    assert(email == user.email )
  }


  test("can set a field value") {
    val user = new User()
    Reflector.setFieldValue(user, "email", "johndoe@work.com")
    assert( user.email == "johndoe@work.com")
  }


  test("can get method") {
     val api = new UserApi()
     val sym = Reflector.getMethod(api, "info")
     assert( sym.name.toTermName.toString == "info")
  }


  test("can call a method with parameters") {

    val api = new UserApi()
    val result = Reflector.callMethod(api, "create", Array[Any]("superman@metro.com", "super", "man", true, 35))
    assert( api.user.email == "superman@metro.com")
    assert( api.user.firstName == "super")
    assert( api.user.lastName == "man")
    assert( api.user.isMale)
    assert( api.user.age == 35)
  }


  test("can get a class level annotation") {
    // NOTE: The annotation must be created with all parameters ( not named parameters )
    val anno = Reflector.getClassAnnotation(typeOf[UserApi], typeOf[Api]).asInstanceOf[Api]
    assert(anno.area == "app")
    assert(anno.name == "users")
    assert(anno.roles == "admin")
    assert(anno.protocol == "*")
    assert(anno.auth == "app-roles")
  }


  test("can get a method level annotation") {
    // NOTE: The annotation must be created with all parameters
    val anno = Reflector.getMemberAnnotation(typeOf[UserApi], typeOf[ApiAction], "activate").asInstanceOf[ApiAction]
    assert(anno.name == "activate")
    assert(anno.roles == "@parent")
  }


  test("can get a method level annotation with verb") {
    // NOTE: The annotation must be created with all parameters
    val anno = Reflector.getMemberAnnotation(typeOf[UserApi], typeOf[ApiAction], "info").asInstanceOf[ApiAction]
    assert(anno.name == "")
    assert(anno.roles == "")
    assert(anno.verb == "get")
  }

/*
  test("can get a method level annotation with verb defaulted") {
    // NOTE: The annotation must be created with all parameters
    val anno = Reflector.getMemberAnnotation(typeOf[UserApi], typeOf[ApiAction], "activate").asInstanceOf[ApiAction]
    assert(anno.name == "activate")
    assert(anno.roles == "@parent")
    assert(anno.verb == null)
  }
*/

  test("can get a field level annotation") {
    // NOTE: The annotation must be created with all parameters
    val anno = Reflector.getFieldAnnotation(typeOf[User], typeOf[Field], "email").asInstanceOf[Field]
    assert(anno.name == "")
    assert(anno.length == 30 )
    assert(anno.required )
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


