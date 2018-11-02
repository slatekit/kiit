package test.setup

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.Random
import slatekit.common.UniqueId
import test.entities.Entity_Mapper_ResultSet_Tests
import java.util.*

data class AuthorR(
        @property:Field(required = true)
        val id: Long             = 0,

        @property:Field(required = true)
        val uuid: String            = Random.stringGuid(),

        @property:Field(required = true)
        val createdAt: DateTime = DateTime.now(),

        @property:Field(required = true)
        val createdBy: Long = 0,

        @property:Field(required = true)
        val updatedAt: DateTime = DateTime.now(),

        @property:Field(required = true)
        val updatedBy: Long = 0,

        @property:Field(required = true)
        val email:String = "",

        @property:Field(required = true)
        val isActive:Boolean = false,

        @property:Field(required = true)
        val age:Int = 35,

        @property:Field(required = true)
        val status:StatusEnum = StatusEnum.Pending,

        @property:Field(required = true)
        val salary:Double = 20.5,

        @property:Field(required = true)
        val uid: UUID = UUID.fromString(Entity_Mapper_ResultSet_Tests.sampleUUID1),

        @property:Field(required = true)
        val shardId: UniqueId = UniqueId.fromString(Entity_Mapper_ResultSet_Tests.sampleUUID2)
)
