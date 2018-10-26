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
import slatekit.common.*
import slatekit.entities.core.*
//</doc:import_required>

//<doc:import_examples>
import slatekit.core.cmds.Cmd

//</doc:import_examples>

class Example_Entities : Cmd("entities") {

  //<doc:examples>
  // CASE 1 : Create a new entity class that extends Entity base class with built in support for
  // 1. id field ( primary key )
  data class EmployeeV1 (
                          override val id        : Long = 0,
                          val firstName : String = "John",
                          val lastName  : String = "Doe"
                        ) : EntityWithId


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
                            override val updatedBy : Long = 0,
                            override val createdBy : Long = 0
                        )
    : EntityWithId, EntityWithTime, EntityWithUser



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
                         override val updatedBy : Long = 0,
                         override val createdBy : Long = 0,
                         override val uuid  : String = Random.guid()
                       )
    : EntityWithId, EntityWithTime, EntityWithUser, EntityWithUUID
  {
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
                          override val updatedBy : Long = 0,
                          override val createdBy : Long = 0,
                          override val uuid  : String = Random.guid()
                        )
    : EntityWithId, EntityWithMeta

//</doc:examples>

    override fun executeInternal(args: Array<String>?) : ResultEx<Any>
    {
      return Success("")
    }
}
