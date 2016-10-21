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
import slate.entities.core._
import slate.common.{Field, DateTime, RandomGen, Result}
//</doc:import_required>

//<doc:import_examples>
import slate.core.cmds.Cmd
//</doc:import_examples>

class Example_Entities extends Cmd("types") with ResultSupportIn
{
  //<doc:examples>
  // CASE 1 : Create a new entity class that implements IEntity trait
  // with support for
  // 1. id field ( primary key )
  // 2. timestamps ( date created, date updated )
  // 3. audit fields ( created by, updated by )
  class EmployeeV1 extends IEntity {
    var id: Long = 0
    var createdAt: DateTime = DateTime.now()
    var updatedAt: DateTime = DateTime.now()
    var updatedBy: Int = 0
    var createdBy: Int = 0

    var firstName = "John"
    var lastName = "Doe"
  }


  // CASE 2 : Create a new entity class that extends Entity base class with built in support for
  // 1. id field ( primary key )
  // 2. timestamps ( created, updated )
  // 3. audit fields ( created by, updated by )
  class EmployeeV2 extends Entity {

    var firstName = "John"
    var lastName = "Doe"
  }


  // CASE 3 : Create a new entity class that extends Entity and also UniqueId
  // 1. id field ( primary key )
  // 2. timestamps ( created, updated )
  // 3. audit fields ( created by, updated by )
  class EmployeeV3 extends Entity with IEntityUnique {

    var firstName = "John"
    var lastName = "Doe"

    // The unique id is a guid and unique regardless of environment ( dev, qa, staging, prod )
    // It serves as a easy way to check for existing items across different environments and
    // also makes it easy to import/export items from 1 environment to another ( e.g. pro to dev )
    var uniqueId: String = RandomGen.stringGuid(false)
  }


  // CASE 4 : Create a new entity class that extends Entity and also has annotations on fields
  // to indicate fields that are persisted to a database ( mapped to table columns )
  class EmployeeV4 extends Entity with IEntityUnique {

    @Field("", true, 20)
    var firstName  = ""

    @Field("", true, 20)
    var lastName  = ""

    @Field("", true, 50)
    var lastLogin  = DateTime.now()

    @Field("", true, -1)
    var isEmailVerified  = false

    @Field("", true, -1)
    var status  = 0

    @Field("", true, 50)
    var uniqueId: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  }
  //</doc:examples>

  override protected def executeInternal(args: Any) : AnyRef =
  {
   ok()
  }
}
