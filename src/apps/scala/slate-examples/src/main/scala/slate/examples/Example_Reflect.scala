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


package slate.examples

//<doc:import_required>
import slate.common.{Field, Reflector}
import slate.core.apis.{Api, ApiAction}
import scala.reflect.runtime.universe._
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
import slate.common.results.ResultSupportIn
import slate.examples.common.{User, UserApi}
//</doc:import_examples>


class Example_Reflect extends Cmd("types") with ResultSupportIn {


  override protected def executeInternal(args: Any) : AnyRef = {
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
    //    def init(first:String, last:String): User =
    //
    // 2. named parameters FAILS ( because they are not considered constants )
    //    @EntityField2(required = true, length = 12)
    //    var isMale = false
    //<doc:setup>

    //</doc:setup>

    //<doc:examples>
    val api = new UserApi()

    // CASE 1: Create instance of a class ( will pick a 0 parameter constructor )
    val user = Reflector.createInstance(typeOf[User]).asInstanceOf[User]
    println("user: " + user)


    // CASE 2: Get a field value
    user.email = "johndoe@home.com"
    val name = Reflector.getFieldValue(user, "email")
    println("email : " + name)


    // CASE 3: Set a field value
    Reflector.setFieldValue(user, "email", "johndoe@work.com")
    println("email : " + user.email)


    // CASE 4: Call a method with parameters
    val result = Reflector.callMethod(api, "create", Array[Any]("superman@metro.com", "super", "man", true, 35))
    println(result.asInstanceOf[User].toString())


    // CASE 5: Get a class level annotation
    // NOTE: The annotation must be created with all parameters ( not named parameters )
    val annoCls = Reflector.getClassAnnotation(typeOf[UserApi], typeOf[Api]).asInstanceOf[Api]
    println(annoCls)


    // CASE 6: Get a method level annotation
    // NOTE: The annotation must be created with all parameters
    val annoMem = Reflector.getMemberAnnotation(typeOf[UserApi], typeOf[ApiAction], "activate").asInstanceOf[ApiAction]
    println(annoMem)


    // CASE 7: Get a field level annotation
    // NOTE: The annotation must be created with all parameters
    val annoFld = Reflector.getFieldAnnotation(typeOf[User], typeOf[Field], "email").asInstanceOf[Field]
    println(annoFld)


    // CASE 8: print parameters
    val method = Reflector.getMethod(api, "activate")
    Reflector.printParams(method)

    // CASE 9: get all fields with annotations
    val fields = Reflector.getFieldsWithAnnotations(user, typeOf[User], typeOf[Field])


    // CASE 10: Get method
    val sym = Reflector.getMethod(api, "info")
    println(sym.name)


    // CASE 11: Get method parameters
    val symArgs = Reflector.getMethodParameters(sym)
    println(symArgs(0))


    // CASE 12: Is argument a basic type
    val argType = symArgs(0)
    println(argType.isBasicType())


    // CASE 13: Create instance from parameter
    val argInstance = Reflector.createInstance(symArgs(0).asType())
    println(argInstance)


    // CASE 14: Get fields of argument type
    val argInstanceFields = Reflector.getFieldsDeclared(argInstance.asInstanceOf[AnyRef])
    println(argInstanceFields)
    //</doc:examples>

    ok()
  }



  def test():Unit =
  {
    //checkScalaReflection
    //checkScalaFields()
    //checkJavaReflection()
  }


  def testTypeOf(): Unit =
  {
    val tpeString = Reflector.getFieldType(typeOf[User], "email")
    val tpeInt = Reflector.getFieldType(typeOf[User], "age")
    val tpeBool = Reflector.getFieldType(typeOf[User], "isMale")

    println(typeOf[String])
    println(tpeString)
    println(typeOf[String] == tpeString)

    println(typeOf[Int])
    println(tpeInt)
    println(typeOf[Int] == tpeInt)

    println(typeOf[Boolean])
    println(tpeInt)
    println(typeOf[Boolean] == tpeBool)

    println(typeOf[Double])

    val user1 = new User()
    println()
    Reflector.printFields(typeOf[User], typeOf[Field])
    //Reflector.getFieldValue()
  }

/*
  def checkJavaReflection() : Unit = {

    val api = new UserApi
    val user = new User()

    // Use case 1: Get field annotation
    ReflectorJ.getFieldAnnotations[EntityField](user, classOf[EntityField], (name:String, anno:EntityField) =>
    {
      println(s"$name, ${anno.name()}, ${anno.required()}, ${anno.length()}")
    })

    // Use case 1: Get class annotation
    val apiJ = ReflectorJ.getClassAnnotation[Api](api, classOf[Api])
    println(apiJ.name() + ", " + apiJ.desc() + ", " + apiJ.roles() )

    // Use case 2: Get method annotation
    val matches = ReflectorJ.getMethodsWithAnnotations[UserApi, ApiArg](classOf[UserApi],classOf[ApiArg])
    for(item <- matches)
    {
      println(item._1.getName)

      val arg =item._2.asInstanceOf[ApiArg]
      println(arg.name() + ", " + arg.desc() + ", " + arg.required + ", " + arg.eg())
    }

    // Test parameters
    println()
    val method = ReflectorJ.getMethod(api, "activate")
    ReflectorJ.printParams(method)
    println("done")
  }
  */
}





