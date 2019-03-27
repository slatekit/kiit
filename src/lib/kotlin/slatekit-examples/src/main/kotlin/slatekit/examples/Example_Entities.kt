/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.common.*
//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd
import slatekit.entities.*
import slatekit.results.Try
import slatekit.results.Success

//</doc:import_examples>

class Example_Entities : Cmd("entities") {

  //<doc:examples>
  // CASE 1 : Create a new entity class that extends Entity base class with built in support for
  // 1. id field ( primary key )
  data class EmployeeV1 (
                          override val id        : Long = 0,
                          val firstName : String = "John",
                          val lastName  : String = "Doe"
                        ) : EntityWithId<Long> {

    override fun isPersisted(): Boolean = id > 0
  }


  // CASE 2 : Create a new entity class with support for
  // 1. id   ( primary key + auto inc )
  // 2. time ( created at, updated at )
  // 3. user ( created by, updated by )
  data class EmployeeV2 (
                            override val id        : Long = 0,
                            val firstName : String = "John",
                            val lastName  : String = "Doe",
                            override val createdAt : DateTime = DateTime.now(),
                            override val updatedAt : DateTime = DateTime.now(),
                            override val updatedBy : String = "",
                            override val createdBy : String = ""
                        )
    : EntityWithId<Long>, EntityWithTime, EntityWithUser {

    override fun isPersisted(): Boolean = id > 0
  }



  // CASE 3 : Create a new entity class with support for:
  // 1. id   ( primary key + auto inc )
  // 2. time ( created at, updated at )
  // 3. user ( created by, updated by )
  // 4. guid ( unique id              )
  data class EmployeeV3(
                         override val id        : Long = 0,
                         val firstName : String = "John",
                         val lastName  : String = "Doe",
                         override val createdAt : DateTime = DateTime.now(),
                         override val updatedAt : DateTime = DateTime.now(),
                         override val updatedBy : String = "",
                         override val createdBy : String = "",
                         override val uuid  : String = Random.uuid()
                       )
    : EntityWithId<Long>, EntityWithTime, EntityWithUser, EntityWithUUID
  {

    override fun isPersisted(): Boolean = id > 0

    // The unique id is a guid and unique regardless of environment ( dev, qa, staging, prod )
    // It serves as a easy way to check for existing items across different environments and
    // also makes it easy to import/export items from 1 environment to another ( e.g. pro to dev )
  }


  // CASE 4 : Create a new entity class with support for:
  // 1. id   ( primary key + auto inc )
  // 2. time ( created at, updated at )
  // 3. user ( created by, updated by )
  // 4. guid ( unique id              )
  // using IEntityWithMeta trait which combines IEntityWithTime, IEntityWithUser, IEntityWithGuid
  data class EmployeeV4(
                          override val id        : Long = 0,
                          val firstName : String = "John",
                          val lastName  : String = "Doe",
                          override val createdAt : DateTime = DateTime.now(),
                          override val updatedAt : DateTime = DateTime.now(),
                          override val updatedBy : String = "",
                          override val createdBy : String = "",
                          override val uuid  : String = Random.uuid()
                        )
    : EntityWithId<Long>, EntityWithMeta {

    override fun isPersisted(): Boolean = id > 0
  }

//</doc:examples>

    override fun executeInternal(args: Array<String>?) : Try<Any>
    {
      return Success("")
    }
}
