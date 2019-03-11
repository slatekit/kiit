package slatekit.info

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.Random
import slatekit.entities.core.EntityUpdatable
import slatekit.entities.core.EntityWithId


data class Dependency(
        @property:Field()
        override val id: Long = 0L,

        @property:Field(required = true, length = 30)
        val name:String = "",

        @property:Field(required = true, length = 30)
        val display:String = "",

        @property:Field(required = true, length = 200)
        val desc:String = "",

        @property:Field(required = true)
        val isActive:Boolean = false,

        @property:Field(required = true, length = 12)
        val version:String = "",

        @property:Field(required = true, length = 30)
        val namespace:String = "",

        @property:Field(required = true, length = 30)
        val jarfile:String = "",

        @property:Field(required = true, length = 30)
        val dependsOn:String = "",

        @property:Field(required = true)
        val createdAt: DateTime = DateTime.now(),

        @property:Field(required = true)
        val createdBy: Long = 0,

        @property:Field(required = true)
        val updatedAt: DateTime = DateTime.now(),

        @property:Field(required = true)
        val updatedBy: Long = 0

) : EntityWithId<Long>, EntityUpdatable<Long, Dependency> {

    override fun isPersisted(): Boolean = id > 0

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    override fun withId(id:Long): Dependency {
        return this.copy(id = id)
    }
}