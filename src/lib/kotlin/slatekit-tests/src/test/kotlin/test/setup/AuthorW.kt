package test.setup

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.Random
import slatekit.common.UniqueId
import test.entities.MapperTests
import java.util.*

class AuthorW {
    @property:Field(required = true)
    var id: Long = 0

    @property:Field(required = true)
    var uuid: String = Random.stringGuid()

    @property:Field(required = true)
    var createdAt: DateTime = DateTime.now()

    @property:Field(required = true)
    var createdBy: Long = 0

    @property:Field(required = true)
    var updatedAt: DateTime = DateTime.now()

    @property:Field(required = true)
    var updatedBy: Long = 0

    @property:Field(required = true)
    var email: String = ""

    @property:Field(required = true)
    var isActive: Boolean = false

    @property:Field(required = true)
    var age: Int = 35

    @property:Field(required = true)
    var status:StatusEnum = StatusEnum.Pending

    @property:Field(required = true)
    var salary: Double = 20.5

    @property:Field(required = true)
    var uid: UUID = UUID.fromString(MapperTests.sampleUUID1)

    @property:Field(required = true)
    var shardId: UniqueId = UniqueId.fromString(MapperTests.sampleUUID2)
}