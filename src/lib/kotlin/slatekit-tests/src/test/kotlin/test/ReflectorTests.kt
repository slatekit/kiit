package slate.test

import org.junit.Test
import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.Reflector
import slatekit.common.Result
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.About
import slatekit.common.log.LoggerConsole
import slatekit.core.common.AppContext
import slatekit.entities.core.Entities
import slatekit.tests.common.UserApi
import test.common.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType
import kotlin.test.assertEquals

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


class ReflectorTests {



    val ctx: AppContext = AppContext (
            arg  = Args.default(),
            env  = Env("local", Dev),
            cfg  = Config(),
            log  = LoggerConsole(),
            ent  = Entities(),
            inf  = About("myapp", "sample app", "product group 1", "slatekit", "ny", "", "", "", "1.1.0", "", ""),
            dbs  = DbLookup.defaultDb(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi")),
            enc  = MyEncryptor
    )


    @Test fun can_test_types(){
        val props = User4::class.primaryConstructor!!.parameters
        for( p in props) {
            println("param name       : " + p.name)
            println("param index      : " + p.index)
            println("param is optional: " + p.isOptional)
            println("param is vararg  : " + p.isVararg)
            println("param kind       : " + p.kind)
            println("param type       : " + p.type)
            println(p)
        }

        val mems = User3::class.declaredMemberProperties
        for( m in mems) {
            println("mem name       : " + m.name)
            println("mem return type: " + m.returnType.javaType.typeName)
        }

        val m = User4::class.members.find { it.name == "activate" }
        println(m!!.returnType)
        println("end")
    }


    @Test fun can_create_normal_class() {
      val i2 = Reflector.create<UserNormal1>(UserNormal1::class)
      assert(i2 is UserNormal1 )
    }


    @Test fun can_create_normal_class_with_args() {
        val user = Reflector.createWithArgs<UserNormal2>(UserNormal2::class, arrayOf("k@abc.com", "ki"))
        assert(user is UserNormal2)
        assert(user.name == "ki")
        assert(user.email == "k@abc.com")
    }


    @Test fun can_create_data_class_with_args() {
        val user = Reflector.createWithArgs<User3>(User3::class, arrayOf("k@abc.com", "ki"))
        assert(user is User3)
        assert(user.name == "ki")
        assert(user.email == "k@abc.com")
    }


    @Test fun can_get_normal_class_field_value() {
      val user = UserNormal1()
      user.email = "johndoe@home.com"
      val actual = Reflector.getFieldValue(user, "email")
      assert(actual == user.email)
    }


    @Test fun can_get_data_class_field_value() {
        val email = "johndoe@home.com"
        val user = User3(email, "johndoe")
        val actual = Reflector.getFieldValue(user, "email")
        assert(actual == email)
    }


    @Test fun can_get_member_method() {

      val sym = Reflector.getMethod(UserApi::class, "activate")
      assert(sym!!.name == "activate")
    }


    @Test fun can_get_member_method_parameters() {

        val args = Reflector.getMethodArgs(UserApi::class, "activate")
        assert(args!!.size - 1 == 4)
    }


    @Test fun can_get_member_method_parameter_types() {
        val params = Reflector.getMethodArgs(UserApi::class, "activate")
        val args = params!!.toList()

        fun assertArg(param:KParameter, name:String, type: KClass<*>){
            assert(param.name == name)
            val paramTypeName = param.type.javaType.typeName
            val typeName= type.java.name
            assert(paramTypeName == typeName)
        }
        assert(args.size == 5)

        assertArg(args[1], "phone", String::class)
        assertArg(args[2], "code", Int::class)
        assertArg(args[3], "isPremiumUser", Boolean::class)
        assertArg(args[4], "date", DateTime::class)
    }


    @Test fun can_call_method() {
        ctx.ent.register<User>(false, User::class)
        val api = UserApi(ctx)
        val res = Reflector.callMethod(UserApi::class, api, "activate", arrayOf("123456789", 987, true, DateTime.of(2017, 5, 27)))
        val result = res as Result<String>
        val v = result.value
        assert(v == "ok")
        assert(result.msg == "activated 123456789, 987, true, 2017-05-27T00:00-04:00[America/New_York]")
    }


    @Test fun can_get_properties_from_data_class() {

        /*
        val email:String,
        val id:Long,
        val active:Boolean,
        val age:Int,
        val salary:Double,
        val starts: DateTime
        */
        val props = Reflector.getProperties(User4::class)
        assertEquals(6, props.size)
        assertEquals("email" , props[0].name)
        assertEquals("id"    , props[1].name)
        assertEquals("active", props[2].name)
        assertEquals("age"   , props[3].name)
        assertEquals("salary", props[4].name)
        assertEquals("starts", props[5].name)
    }


    @Test fun can_get_properties_from_normal_class() {

        /*
        val email:String,
        val id:Long,
        val active:Boolean,
        val age:Int,
        val salary:Double,
        val starts: DateTime
        */
        val props = Reflector.getProperties(UserNormal1::class)
        assertEquals(6, props.size)
        assertEquals("active", props[0].name)
        assertEquals("age"   , props[1].name)
        assertEquals("email" , props[2].name)
        assertEquals("id"    , props[3].name)
        assertEquals("salary", props[4].name)
        assertEquals("starts", props[5].name)
    }


    @Test fun can_get_annotation_for_class(){
        val api:Api = Reflector.getAnnotationForClass<Api>(UserApi::class, Api::class)
        assertEquals( api.area     , "app"      )
        assertEquals( api.name     , "users"    )
        assertEquals( api.roles    , "admin"    )
        assertEquals( api.auth     , "app-roles")
        assertEquals( api.protocol , "*"        )
        assertEquals( api.verb     , "*"        )
    }


    @Test fun can_get_annotation_for_method() {
        val members = Reflector.getAnnotatedMembers<ApiAction>(UserApi::class, ApiAction::class)
        assertEquals(36, members.size )
        assertEquals("activate", members[0].second?.name)
        assertEquals("", members[1].second?.name)
    }


    @Test fun can_get_annotation_for_parameter(){
        User3::class.members.forEach { mem ->
            println(mem.name)
            println(mem.annotations.size)
            mem.annotations.forEach { ano -> println(ano) }
        }
        println("done")
        val props = Reflector.getAnnotatedProps<Field>(User3::class, Field::class)
        assertEquals(2, props.size)
        assertEquals(props[0].second?.name, "email")
        assertEquals(props[0].second?.required, true)
        assertEquals(props[0].second?.eg, "clark@metro.com")
        assertEquals(props[1].second?.name, "")
        assertEquals(props[1].second?.required, false)
        assertEquals(props[1].second?.eg, "clark kent")
    }

    /*


  describe("Reflected") {

    it("can get full api"){
      val re = new ReflectedClassT[User]()
      assert( re.api == "User" )
      assert( re.fullname == "slate.test.common.User" )
    }


    it("can get method"){
      val re = new ReflectedClassT[User]()
      assert( re.api == "User" )
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

*/
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


