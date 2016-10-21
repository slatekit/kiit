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
import slate.common.results.ResultSupportIn

import scala.reflect.runtime.universe._
import slate.entities.core._
import slate.common.{Field, DateTime, Reflector, Result}
import slate.common.databases.DbBuilder
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
//</doc:import_examples>


class Example_Mapper extends Cmd("types") with ResultSupportIn {

  //<doc:setup>
  class Consultant extends Entity with IEntityUnique {

    @Field("", true, 20)
    var firstName  = ""

    @Field("", true, 20)
    var lastName  = ""

    @Field("", true, 50)
    var email  = ""

    @Field("", true, 50)
    var lastLogin  = DateTime.now()

    @Field("", true, -1)
    var isEmailVerified  = false

    @Field("", true, -1)
    var status  = 0

    @Field("", true, 50)
    var uniqueId: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  }
  //</doc:setup>


  override protected def executeInternal(args: Any) : AnyRef =
  {
    //<doc:examples>
    // CASE 1: Create instance of entity via reflection
    val person = Reflector.createInstance(typeOf[Consultant]).asInstanceOf[Consultant]

    // CASE 2: Load the mapper with schema from the annotations on the model
    val mapper = new EntityMapper(null)
    val model = mapper.loadSchema(person, typeOf[Consultant])

    // CASE 3: Create instance for testing
    person.firstName = "share"
    person.lastName = "job"
    person.lastLogin = DateTime.now()
    person.email = "john.doe@gmail.com"
    person.isEmailVerified = false
    person.status = 0
    person.createdAt = DateTime.now()
    person.updatedAt = DateTime.now()

    // CASE 4: Get the sql for create
    val sqlCreate = mapper.mapToSql(person, update = false, fullSql = true)
    println(sqlCreate)

    // CASE 5: Get the sql for update
    val sqlForUpdate = mapper.mapToSql(person, update = true, fullSql = true)
    println(sqlForUpdate)

    // CASE 6: Generate the table schema for mysql from the model
    println( "table sql : " + new DbBuilder().addTable(model))
    //</doc:examples>

    // CASE 6: add a field for uniqueness
    ok()
  }
}
