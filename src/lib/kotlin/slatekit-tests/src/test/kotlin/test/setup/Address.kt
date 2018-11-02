package test.setup

import slatekit.common.Field
import slatekit.common.UniqueId
import test.entities.Entity_Mapper_ResultSet_Tests
import java.util.*

data class Address(

        @property:Field(required = true, length = 40)
        val addr   : String,


        @property:Field(required = true, length = 30)
        val city   : String,


        @property:Field(required = true, length = 20)
        val state  : String,


        @property:Field(required = true)
        val country: Int,


        @property:Field(required = true, length = 5)
        val zip    : String,


        @property:Field(required = true)
        val isPOBox: Boolean
)


data class UserWithAddress(
        @property:Field(required = true)
        val id: Long             = 0,

        @property:Field(required = true)
        val email:String = "",

        @property:Field(required = true)
        val isActive:Boolean = false,

        @property:Field(required = true)
        val age:Int = 35,

        @property:Field(required = true)
        val salary:Double = 20.5,

        @property:Field(required = false)
        val addr:Address? = null,

        @property:Field(required = true)
        val uid: UUID = UUID.fromString(Entity_Mapper_ResultSet_Tests.sampleUUID1),

        @property:Field(required = true)
        val shardId: UniqueId = UniqueId.fromString(Entity_Mapper_ResultSet_Tests.sampleUUID2)
)



