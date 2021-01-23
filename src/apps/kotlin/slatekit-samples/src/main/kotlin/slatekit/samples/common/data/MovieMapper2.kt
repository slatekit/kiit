package slatekit.samples.common.data

import slatekit.data.core.LongId
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.entities.mapper.EntityMapper
import slatekit.meta.models.Model
import slatekit.samples.common.models.Movie



val movieModel = Model.of<Long, Movie> {
    id(Movie::id)
    field(Movie::uuid)
    field(Movie::title)
    field(Movie::category)
    field(Movie::playing)
    field(Movie::delivery)
    field(Movie::cost)
    field(Movie::rating)
    field(Movie::released)
}

/**
 * Option 2: Schema based mapper with reflection
 * Pros: More convenient due to setting up schema once, no manual encoding/decoding
 * Cons: Less performant than manual
 */
object MovieMapper2 : EntityMapper<Long, Movie>(
        model = movieModel,
        meta = Meta(LongId { m -> m.id }, Table("movie")),
        idClass = Long::class,
        enClass = Movie::class)