package slatekit.samples.common.models

import slatekit.common.DateTime
import slatekit.common.DateTimes
import slatekit.entities.Column
import slatekit.entities.EntityUpdatable
import slatekit.entities.EntityWithId
import java.util.*

data class Movie(
        /**
         * Primary key ( auto-increment )
         */
        override val id: Long = 0L,

        /**
         * Indexed also using uuid ( for sample purposes )
         */
        @property:Column(length = 50, indexed = true)
        val uuid: UUID = UUID.randomUUID(),


        @property:Column(required = true, length = 50)
        val title: String = "",


        @property:Column(length = 20)
        val category: String = "",


        @property:Column(required = true)
        val playing: Boolean = false,


        @property:Column(required = true)
        val delivery: Delivery = Delivery.Theater,


        @property:Column(required = true)
        val cost: Int,


        @property:Column(required = true)
        val rating: Double,


        @property:Column(required = true)
        val released: DateTime

) : EntityWithId<Long>, EntityUpdatable<Long, Movie> {
    override fun isPersisted(): Boolean = id > 0

    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    override fun withId(id: Long): Movie {
        return this.copy(id = id)
    }

    companion object {
        fun sample():Movie {
            return sample(
                        title = "Contact",
                        category = "sci-fi",
                        playing = false,
                        cost = 10,
                        rating = 4.5,
                        released = DateTimes.of(1995, 8, 10)
                )
        }

        fun sample(title:String, category:String, playing: Boolean, cost:Int, rating:Double, released: DateTime):Movie {
            return Movie(
                    title = "Contact",
                    category = "sci-fi",
                    playing = false,
                    cost = 10,
                    rating = 4.5,
                    released = DateTimes.of(1995, 8, 10)
            )
        }
    }
}