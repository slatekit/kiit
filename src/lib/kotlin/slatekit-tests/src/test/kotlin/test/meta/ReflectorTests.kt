package slate.test

import org.junit.Assert
import org.junit.Test
import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.common.*
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.envs.Env
import slatekit.common.envs.EnvMode
import slatekit.common.info.About
import slatekit.common.log.LogsDefault
import slatekit.common.types.PhoneUS
import slatekit.meta.Reflector
import slatekit.entities.core.Entities
import slatekit.integration.common.AppEntContext
import slatekit.meta.KTypes
import slatekit.meta.kClass
import test.setup.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaSetter
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



    val ctx: AppEntContext = AppEntContext (
            arg  = Args.default(),
            env  = Env("local", EnvMode.Dev),
            cfg  = Config(),
            logs = LogsDefault,
            ent  = Entities(),
            inf  = About("myapp", "sample app", "product group 1", "slatekit", "ny", "", "", "", "1.1.0", "", ""),
            dbs  = DbLookup.defaultDb(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi")),
            enc  = MyEncryptor
    )


    @Test fun can_match_java_to_kotlin_types() {
        assert(Types.JStringClass        == KTypes.KStringClass       .java)
        assert(Types.JBoolClass          == KTypes.KBoolClass         .java)
        assert(Types.JShortClass         == KTypes.KShortClass        .java)
        assert(Types.JIntClass           == KTypes.KIntClass          .java)
        assert(Types.JLongClass          == KTypes.KLongClass         .java)
        assert(Types.JFloatClass         == KTypes.KFloatClass        .java)
        assert(Types.JDoubleClass        == KTypes.KDoubleClass       .java)
        assert(Types.JDateTimeClass      == KTypes.KDateTimeClass     .java)
        assert(Types.JLocalDateClass     == KTypes.KLocalDateClass    .java)
        assert(Types.JLocalTimeClass     == KTypes.KLocalTimeClass    .java)
        assert(Types.JLocalDateTimeClass == KTypes.KLocalDateTimeClass.java)
    }


    @Test fun can_match_types_dynamically() {

        val id:Any = 0
        val actualCls = id.javaClass
        assert(Types.JIntAnyClass == actualCls)
    }


    @Test fun can_check_subtype(){
        val tpe = PhoneUS::class
        tpe.supertypes.forEach { println(it) }
        val ndx = tpe.supertypes.indexOf(SmartString::class.createType())
        assert(ndx == 0)
    }


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
      assert(i2 is UserNormal1)
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


    @Test fun can_check_for_slatekit_enum(){
        Assert.assertTrue(Reflector.isSlateKitEnum(StatusEnum::class))
        Assert.assertFalse(Reflector.isSlateKitEnum(RoleEnum::class))
    }


    @Test fun can_parse_enum_value_using_name(){
        val expected = StatusEnum.Active
        val actual = Reflector.getEnumValue(StatusEnum::class, "Active")
        Assert.assertTrue(expected == actual)
    }


    @Test fun can_parse_enum_value_using_number(){
        val expected = StatusEnum.Active
        val actual = Reflector.getEnumValue(StatusEnum::class, 1)
        Assert.assertTrue(expected == actual)
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
        ctx.ent.register<User>( User::class)
        val api = UserApi(ctx)
        val res = Reflector.callMethod(UserApi::class, api, "activate", arrayOf("123456789", 987, true, DateTime.of(2017, 5, 27)))
        val result = res as ResultMsg<String>
        val v = result.getOrElse { "" }
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


    @Test fun can_get_annotation_for_method_declared() {
        val members = Reflector.getAnnotatedMembers<ApiAction>(UserApi::class, ApiAction::class, true)
        assertEquals(25, members.size )
        assertEquals("activate", members[0].second.name)
        assertEquals("", members[1].second.name)
    }


    @Test fun can_get_annotation_for_method_with_inherited() {
        val members = Reflector.getAnnotatedMembers<ApiAction>(UserApi::class, ApiAction::class, false)
        assertEquals(41, members.size )
        assertEquals("activate", members[0].second.name)
        assertEquals("", members[1].second.name)
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

    @Test fun can_access_property_values_from_fields() {
        val author = AuthorW()
        author.email = "poster1@abc.com"
        Reflector.setFieldValue(AuthorW::class, author, "email", "poster2@abc.com")
        //val obj = author as Any
        // val prop = AuthorW::class.declaredMemberProperties.find { it.name == "email" }

        //AuthorW::email.javaSetter?.invoke(obj, "poster2@abc.com")
        assert(author.email == "poster2@abc.com")
    }

    /*


  describe("Reflected") {

    it("can get full api"){
      val re = new ReflectedClassT[User]()
      assert( re.api == "User" )
      assert( re.fullname == "slate.test._setup.User" )
    }


    it("can get method"){
      val re = new ReflectedClassT[User]()
      assert( re.api == "User" )
      assert( re.fullname == "slate.test._setup.User" )
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


