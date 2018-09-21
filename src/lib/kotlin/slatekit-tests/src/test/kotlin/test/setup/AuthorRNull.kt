package test.setup

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.Random
import slatekit.common.UniqueId
import test.entities.MapperTests
import java.util.*

data class AuthorRNull(
        @property:Field(required = false)
        val id: Long ? = null,

        @property:Field(required = false)
        val email:String? = null,

        @property:Field(required = false)
        val isActive:Boolean? = null,

        @property:Field(required = false)
        val age:Int?  = null,

        @property:Field(required = false)
        val status:StatusEnum = StatusEnum.Pending,

        @property:Field(required = false)
        val salary:Double? = null,

        @property:Field(required = false)
        val createdAt: DateTime? = null
)