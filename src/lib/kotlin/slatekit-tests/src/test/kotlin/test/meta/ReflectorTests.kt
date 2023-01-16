/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package slate.test

import org.junit.Assert
import org.junit.Test
import kiit.apis.*
import kiit.common.*
import kiit.common.args.Args
import kiit.common.conf.Config
import kiit.common.data.DbConString
import kiit.common.data.Connections
import kiit.common.envs.Envs
import kiit.common.info.About
import kiit.common.info.Info
import kiit.common.log.LogsDefault
import kiit.utils.smartvalues.PhoneUS
import kiit.utils.smartvalues.SmartValued
import kiit.db.Db
import kiit.meta.Reflector
import kiit.entities.Entities
import kiit.connectors.entities.AppEntContext
import kiit.entities.Column
import kiit.meta.KTypes
import kiit.results.Notice
import kiit.results.Success
import kiit.results.getOrElse
import test.TestApp
import test.setup.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaType



class ReflectorTests : TestSupport {

    val ctx: AppEntContext = AppEntContext (
            app = app,
            args  = Args.empty(),
            envs  = Envs.defaults().select("loc"),
            conf  = Config(app),
            logs = LogsDefault,
            ent  = Entities({ con -> Db(con) }),
            info = Info.of(
                    About("tests", "myapp", "sample app", "slatekit", "ny", "", "",  "")
            ),
            dbs  = Connections.of(DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/World_shard2", "root", "abcdefghi")),
            enc  = MyEncryptor
    )


    @Test fun can_match_java_to_kotlin_types() {
        Assert.assertTrue(Types.JStringClass        == KTypes.KStringClass       .java)
        Assert.assertTrue(Types.JBoolClass          == KTypes.KBoolClass         .java)
        Assert.assertTrue(Types.JShortClass         == KTypes.KShortClass        .java)
        Assert.assertTrue(Types.JIntClass           == KTypes.KIntClass          .java)
        Assert.assertTrue(Types.JLongClass          == KTypes.KLongClass         .java)
        Assert.assertTrue(Types.JFloatClass         == KTypes.KFloatClass        .java)
        Assert.assertTrue(Types.JDoubleClass        == KTypes.KDoubleClass       .java)
        Assert.assertTrue(Types.JDateTimeClass      == KTypes.KDateTimeClass     .java)
        Assert.assertTrue(Types.JLocalDateClass     == KTypes.KLocalDateClass    .java)
        Assert.assertTrue(Types.JLocalTimeClass     == KTypes.KLocalTimeClass    .java)
        Assert.assertTrue(Types.JLocalDateTimeClass == KTypes.KLocalDateTimeClass.java)
    }



    @Test fun can_check_subtype(){
        val tpe = PhoneUS::class
        tpe.supertypes.forEach { println(it) }
        val ndx = tpe.supertypes.indexOf(SmartValued::class.createType())
        Assert.assertTrue(ndx == 0)
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
      Assert.assertTrue(i2 is UserNormal1)
    }


    @Test fun can_create_normal_class_with_args() {
        val user = Reflector.createWithArgs<UserNormal2>(UserNormal2::class, arrayOf("k@abc.com", "ki"))
        Assert.assertTrue(user is UserNormal2)
        Assert.assertTrue(user.name == "ki")
        Assert.assertTrue(user.email == "k@abc.com")
    }


    @Test fun can_create_data_class_with_args() {
        val user = Reflector.createWithArgs<User3>(User3::class, arrayOf("k@abc.com", "ki"))
        Assert.assertTrue(user is User3)
        Assert.assertTrue(user.name == "ki")
        Assert.assertTrue(user.email == "k@abc.com")
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
      Assert.assertTrue(actual == user.email)
    }


    @Test fun can_get_data_class_field_value() {
        val email = "johndoe@home.com"
        val user = User3(email, "johndoe")
        val actual = Reflector.getFieldValue(user, "email")
        Assert.assertTrue(actual == email)
    }


    @Test fun can_get_member_method() {

      val sym = Reflector.getMethod(UserApi::class, "activate")
      Assert.assertTrue(sym!!.name == "activate")
    }


    @Test fun can_get_member_method_parameters() {

        val args = Reflector.getMethodArgs(UserApi::class, "activate")
        Assert.assertTrue(args!!.size - 1 == 4)
    }


    @Test fun can_get_member_method_parameter_types() {
        val params = Reflector.getMethodArgs(UserApi::class, "activate")
        val args = params!!.toList()

        fun assertArg(param:KParameter, name:String, type: KClass<*>){
            Assert.assertTrue(param.name == name)
            val paramTypeName = param.type.javaType.typeName
            val typeName= type.java.name
            Assert.assertTrue(paramTypeName == typeName)
        }
        Assert.assertTrue(args.size == 5)

        assertArg(args[1], "phone", String::class)
        assertArg(args[2], "code", Int::class)
        assertArg(args[3], "isPremiumUser", Boolean::class)
        assertArg(args[4], "date", DateTime::class)
    }


    @Test fun can_call_method() {
        val api = ActivationService
        val res = Reflector.callMethod(ActivationService::class, api, ActivationService::activate.name, arrayOf("123456789", 987, true, DateTimes.of(2017, 5, 27)))
        val result = res as Notice<String>
        val v = result.getOrElse { "" }
        Assert.assertTrue(v == "ok")
        Assert.assertTrue(result.desc == "activated 123456789, 987, true, 2017-05-27T00:00-04:00[America/New_York]")
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
        Assert.assertEquals(6, props.size)
        Assert.assertEquals("email" , props[0].name)
        Assert.assertEquals("id"    , props[1].name)
        Assert.assertEquals("active", props[2].name)
        Assert.assertEquals("age"   , props[3].name)
        Assert.assertEquals("salary", props[4].name)
        Assert.assertEquals("starts", props[5].name)
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
        Assert.assertEquals(6, props.size)
        Assert.assertEquals("active", props[0].name)
        Assert.assertEquals("age"   , props[1].name)
        Assert.assertEquals("email" , props[2].name)
        Assert.assertEquals("id"    , props[3].name)
        Assert.assertEquals("salary", props[4].name)
        Assert.assertEquals("starts", props[5].name)
    }


    @Test fun can_get_annotation_for_class(){
        val api:Api = Reflector.getAnnotationForClass<Api>(UserApi::class, Api::class)
        Assert.assertEquals( "app"      , api.area     )
        Assert.assertEquals( "users"    , api.name     )
        Assert.assertEquals( "admin"    , api.roles[0] )
        Assert.assertEquals( 1          , api.roles.size )
        Assert.assertEquals( AuthMode.Token.name , api.auth     )
        Assert.assertEquals(  Sources.ALL       , api.sources[0])
        Assert.assertEquals(  1         , api.sources.size)
        Assert.assertEquals( Verbs.AUTO          , api.verb     )
    }


    @Test fun can_get_annotation_for_method_declared() {
        val members = Reflector.getAnnotatedMembers<Action>(UserApi::class, Action::class, true)
        Assert.assertEquals(25, members.size )
        Assert.assertEquals("activate", members[0].second.name)
        Assert.assertEquals("", members[1].second.name)
    }


    @Test fun can_get_annotation_for_method_with_inherited() {
        val members = Reflector.getAnnotatedMembers<Action>(SampleAnnoExtendedApi::class, Action::class, false)
        Assert.assertEquals(3, members.size )
        Assert.assertEquals("seconds", members[0].second.name)
        Assert.assertEquals("", members[1].second.name)
    }


    @Test fun can_get_annotation_for_parameter(){
        User3::class.members.forEach { mem ->
            println(mem.name)
            println(mem.annotations.size)
            mem.annotations.forEach { ano -> println(ano) }
        }
        println("done")
        val props = Reflector.getAnnotatedProps<Column>(User3::class, Column::class)
        Assert.assertEquals(2, props.size)
        Assert.assertEquals(props[0].second?.name, "email")
        Assert.assertEquals(props[0].second?.required, true)
        Assert.assertEquals(props[0].second?.example, "clark@metro.com")
        Assert.assertEquals(props[1].second?.name, "")
        Assert.assertEquals(props[1].second?.required, false)
        Assert.assertEquals(props[1].second?.example, "clark kent")
    }

    @Test fun can_access_property_values_from_fields() {
        val author = AuthorW()
        author.email = "poster1@abc.com"
        Reflector.setFieldValue(AuthorW::class, author, "email", "poster2@abc.com")
        //val obj = author as Any
        // val prop = AuthorW::class.declaredMemberProperties.find { it.name == "email" }

        //AuthorW::email.javaSetter?.invoke(obj, "poster2@abc.com")
        Assert.assertTrue(author.email == "poster2@abc.com")
    }


    object ActivationService {
        @Action(desc = "activate")
        fun activate(phone: String, code: Int, isPremiumUser: Boolean, date: DateTime): Notice<String> =
                Success("ok", msg = "activated $phone, $code, $isPremiumUser, $date")

    }

    /*


  describe("Reflected") {

    it("can get full api"){
      val re = new ReflectedClassT[User]()
      Assert.assertTrue( re.api == "User" )
      Assert.assertTrue( re.fullname == "slate.test._setup.User" )
    }


    it("can get method"){
      val re = new ReflectedClassT[User]()
      Assert.assertTrue( re.api == "User" )
      Assert.assertTrue( re.fullname == "slate.test._setup.User" )
    }


    it("can get a field value") {
      val re = new ReflectedClassT[User]()
      val user = new User()
      user.email = "johndoe@home.com"
      val email = re.getValue(user, "email")
      Assert.assertTrue(email == user.email)
    }


    it("can set a field value") {
      val re = new ReflectedClassT[User]()
      val user = new User()
      re.setValue(user, "email", "johndoe@work.com")
      Assert.assertTrue(user.email == "johndoe@work.com")
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


