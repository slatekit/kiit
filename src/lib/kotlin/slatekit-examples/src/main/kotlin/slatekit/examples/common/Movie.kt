package slatekit.examples.common

import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.common.Field
import slatekit.entities.core.EntityWithId

// NOTES: You can entities properties to be persisted
// in 3 different ways:
// 1. annotations on the Entity
// 2. building a Model and manually registering property references
// 3. building a Model and manually registering via field/names
//
// See Example_Mapper.kt and slatekit.common.Model for more info.
//
// IMMUTABILITY:
// The ORM is originally built for immutable Entities ( Data Classes )
// It also supports Entities with "vars", but has not been tested.
// In a future release, we will fully support var properties
data class Movie(
        override val id :Long = 0L,


        @property:Field(required = true, length = 50)
        val title :String = "",


        @property:Field(length = 20)
        val category :String = "",


        @property:Field(required = true)
        val playing :Boolean = false,


        @property:Field(required = true)
        val cost:Int,


        @property:Field(required = true)
        val rating: Double,


        @property:Field(required = true)
        val released: DateTime,


        // These are the timestamp and audit fields.
        @property:Field(required = true)
        val createdAt : DateTime = DateTime.now(),


        @property:Field(required = true)
        val createdBy :Long  = 0,


        @property:Field(required = true)
        val updatedAt : DateTime =  DateTime.now(),


        @property:Field(required = true)
        val updatedBy :Long  = 0
)
    : EntityWithId<Long>
{
    override fun isPersisted(): Boolean = id > 0

    companion object {
        fun samples():List<Movie> = listOf(
                Movie(
                        title = "Indiana Jones: Raiders of the Lost Ark",
                        category = "Adventure",
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTimes.of(1985, 8, 10)
                ),
                Movie(
                        title = "WonderWoman",
                        category = "action",
                        playing = true,
                        cost = 100,
                        rating = 4.2,
                        released = DateTimes.of(2017, 7, 4)
                )
        )


    }
}