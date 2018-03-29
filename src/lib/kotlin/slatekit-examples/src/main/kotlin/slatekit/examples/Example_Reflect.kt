/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.common.Field
import slatekit.meta.Reflector

//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.examples.common.User
import slatekit.examples.common.UserApi
import slatekit.integration.common.AppEntContext

//</doc:import_examples>


class Example_Reflect : Cmd("reflect") {

  override fun executeInternal(args: Array<String>?) : Result<Any> {
    // JAVA
    // 1. getFields NOT working
    // 2. getDeclaredFields working
    // 3. can NOT use duplicate annotations on a method

    // SCALA
    // 1. all parameters explicitly setup without named parameters works:
    //    @EntityField2("", true, 12)
    //    var isMale = false
    //
    //    @EntityField3("action:init", true, 12)
    //    fun init(first:String, last:String): User =
    //
    // 2. named parameters FAILS ( because they are not considered constants )
    //    @EntityField2(required = true, length = 12)
    //    var isMale = false
    //<doc:setup>

    //</doc:setup>

    //<doc:examples>
    val ctx = AppEntContext.sample("sample", "sample", "", "")
    val api = UserApi(ctx)

    // CASE 1: Create instance of a class ( will pick a 0 parameter constructor )
    val user = Reflector.create<User>(User::class)
    println("user: " + user)


    // CASE 2: Get a field value
    val user2 = user.copy(email = "johndoe@home.com")
    val name = Reflector.getFieldValue(user2, "email")
    println("email : " + name)


    // CASE 3: Set a field value
    Reflector.setFieldValue(user, User::email, "johndoe@work.com")
    println("email : " + user.email)


    // CASE 4: Call a method with parameters
    val result = Reflector.callMethod(UserApi::class, api, "create", arrayOf("superman@metro.com", "super", "man", true, 35))
    println((result as User ).toString())


    // CASE 5: Get a class level annotation
    // NOTE: The annotation must be created with all parameters ( not named parameters )
    val annoCls = Reflector.getAnnotationForClass<Api>(UserApi::class, Api::class)
    println(annoCls)


    // CASE 6: Get a method level annotations
    // NOTE: The annotation must be created with all parameters
    val annoMems = Reflector.getAnnotatedMembers<ApiAction>(UserApi::class, ApiAction::class)
    println(annoMems)


    // CASE 7: Get a field level annotations
    // NOTE: The annotation must be created with all parameters
    val annoFlds = Reflector.getAnnotatedProps<Field>(User::class, Field::class)
    println(annoFlds)


    // CASE 8: print parameters
    val method = Reflector.getMethod(UserApi::class, "activate")
    //Reflector.printParams(method)


    // CASE 10: Get method
    val sym = Reflector.getMethod(UserApi::class, "info")
    println(sym?.name)


    // CASE 11: Get method parameters
    val symArgs = Reflector.getMethodArgs(UserApi::class, "activate")
    println(symArgs)


    // CASE 12: Is argument a basic type
    val argType = symArgs!!.toList()[0]
    println(argType.type)


    // CASE 13: Create instance from parameter
    val argInstance = Reflector.create<Any>(symArgs!!.toList()[0].javaClass.kotlin)
    println(argInstance)
    //</doc:examples>

    return ok()
  }
}





