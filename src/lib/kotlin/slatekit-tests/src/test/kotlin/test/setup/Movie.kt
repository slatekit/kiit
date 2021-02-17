package test.setup


import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.entities.Column
import slatekit.entities.EntityWithId
import slatekit.entities.Id

data class Movie(
        @property:Id()
        override val id :Long = 0L,


        @property:Column(required = true, length = 50)
        val title :String = "",


        @property:Column(length = 20)
        val category :String = "",


        @property:Column(required = true)
        val playing :Boolean = false,


        @property:Column(required = true)
        val cost:Int,


        @property:Column(required = true)
        val rating: Double,


        @property:Column(required = true)
        val released: DateTime,


        // These are the timestamp and audit fields.
        @property:Column(required = true)
        val createdAt : DateTime = DateTime.now(),


        @property:Column(required = true)
        val createdBy :Long  = 0,


        @property:Column(required = true)
        val updatedAt : DateTime =  DateTime.now(),


        @property:Column(required = true)
        val updatedBy :Long  = 0
)
    : EntityWithId<Long> {

    override fun isPersisted(): Boolean = id > 0

    companion object {
        fun samples():List<Movie> = listOf(
                Movie(
                        id = 1L,
                        title = "Indiana Jones: Raiders of the Lost Ark",
                        category = "Adventure",
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTimes.of(1985, 8, 10)
                ),
                Movie(
                        id = 2L,
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