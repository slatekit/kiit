package test.common

import slatekit.common.DateTime


data class Book(
        val id: Long,
        val title: String,
        val category: String,
        val cost: Int,
        val rating: Double,
        val released: DateTime
) {


    companion object {
        fun samples(): List<Book> {
            return listOf(
                    Book(
                            id = 1,
                            title = "Catcher in the rye",
                            category = "fiction",
                            cost = 11,
                            rating = 4.1,
                            released = DateTime.of(1950, 1, 1)
                    ),
                    Book(
                            id = 2,
                            title = "Siddartha",
                            category = "fiction",
                            cost = 12,
                            rating = 4.2,
                            released = DateTime.of(1950, 2, 2)
                    )
            )
        }
    }
}