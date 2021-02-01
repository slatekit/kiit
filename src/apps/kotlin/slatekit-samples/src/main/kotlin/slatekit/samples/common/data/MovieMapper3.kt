package slatekit.samples.common.data

import slatekit.data.core.LongId
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.entities.mapper.EntityMapper
import slatekit.meta.models.Model
import slatekit.samples.common.models.Movie

/**
 * Option 3: Schema automatically loaded from annotation
 * Pros: More convenient due to setting up schema once, no manual encoding/decoding
 * Cons: Less performant than manual
 */
object MovieMapper3 : EntityMapper<Long, Movie>(
        model = Model.loadSchema(Movie::class),
        meta = Meta(LongId { m -> m.id }, Table("movie")),
        idClass = Long::class,
        enClass = Movie::class)