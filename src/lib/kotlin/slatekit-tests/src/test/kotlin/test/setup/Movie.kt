package test.setup


import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.entities.core.EntityWithId

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
    : EntityWithId
{
    companion object {
        fun samples():List<Movie> = listOf(
                Movie(
                        id = 1L,
                        title = "Indiana Jones: Raiders of the Lost Ark",
                        category = "Adventure",
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTime.of(1985, 8, 10)
                ),
                Movie(
                        id = 2L,
                        title = "WonderWoman",
                        category = "action",
                        playing = true,
                        cost = 100,
                        rating = 4.2,
                        released = DateTime.of(2017, 7, 4)
                )
        )


    }
}