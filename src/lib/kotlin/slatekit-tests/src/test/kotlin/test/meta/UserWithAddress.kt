package test.meta

import slatekit.common.ids.UPID
import slatekit.common.ids.UPIDs
import slatekit.entities.EntityWithId
import slatekit.meta.models.Id
import slatekit.meta.models.Field
import test.setup.UUIDSamples
import java.util.*

data class UserWithAddress(
        @property:Id(generated = true)
        override val id: Long             = 0,

        @property:Field(required = true)
        val email:String = "",

        @property:Field(required = true)
        val isActive:Boolean = false,

        @property:Field(required = true)
        val age:Int = 35,

        @property:Field(required = true)
        val salary:Double = 20.5,

        @property:Field(required = false)
        val addr: Address? = null,

        @property:Field(required = true)
        val uid: UUID = UUID.fromString(UUIDSamples.sampleUUID1),

        @property:Field(required = true)
        val shardId: UPID = UPIDs.parse(UUIDSamples.sampleUUID2)
) : EntityWithId<Long> {
        override fun isPersisted(): Boolean = id > 0
}