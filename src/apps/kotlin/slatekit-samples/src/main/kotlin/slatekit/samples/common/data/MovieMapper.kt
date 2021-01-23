package slatekit.samples.common.data

import slatekit.common.Record
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.common.data.Mapper
import slatekit.common.data.Value
import slatekit.common.data.Values
import slatekit.data.core.LongId
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.data.encoders.Encoders
import slatekit.entities.mapper.EntityMapper
import slatekit.meta.models.Model
import slatekit.meta.models.ModelMapper
import slatekit.samples.common.models.Delivery
import slatekit.samples.common.models.Movie

/**
 * Option 1: Reflection-Less mapper
 * Pros: More performant due to non-reflection use
 * Cons: Manual coding
 */
class MovieMapperManual(val encoders:Encoders<Long, Movie>) : Mapper<Long, Movie> {

    override fun decode(record: Record, enc: Encryptor?): Movie? {
        return Movie(
                id       = record.getLong(Movie::id.name),
                uuid     = record.getUUID(Movie::uuid.name),
                title    = record.getString(Movie::title.name),
                category = record.getString(Movie::category.name),
                playing  = record.getBool(Movie::playing.name),
                delivery = Delivery.convert(record.getInt(Movie::delivery.name)) as Delivery,
                cost     = record.getInt(Movie::cost.name),
                rating   = record.getDouble(Movie::rating.name),
                released = record.getDateTime(Movie::released.name)
        )
    }

    override fun encode(model: Movie, action: DataAction, enc: Encryptor?): Values {
        return listOf(
                Value(Movie::uuid.name     , encoders.uuids.encode(model.uuid)),
                Value(Movie::title.name    , encoders.strings.encode(model.title)),
                Value(Movie::category.name , encoders.strings.encode(model.category)),
                Value(Movie::playing.name  , encoders.bools.encode(model.playing)),
                Value(Movie::delivery.name , encoders.enums.encode(model.delivery)),
                Value(Movie::cost.name     , encoders.ints.encode(model.cost)),
                Value(Movie::rating.name   , encoders.doubles.encode(model.rating)),
                Value(Movie::released.name , encoders.dateTimes.encode(model.released))
        )
    }
}

val model = Model.of<Long, Movie> {
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
object MovieMapperSchema : EntityMapper<Long, Movie>(
        model = model,
        meta = Meta( LongId { m -> m.id }, Table("movie")),
        idClass = Long::class,
        enClass = Movie::class)



/**
 * Option 3: Schema automatically loaded from annotation
 * Pros: More convenient due to setting up schema once, no manual encoding/decoding
 * Cons: Less performant than manual
 */
object MovieMapperAnnotations : EntityMapper<Long, Movie>(
        model = ModelMapper.loadSchema(Movie::class),
        meta = Meta( LongId { m -> m.id }, Table("movie")),
        idClass = Long::class,
        enClass = Movie::class)

