package slatekit.samples.common.data

import slatekit.common.Record
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.common.data.Mapper
import slatekit.common.data.Value
import slatekit.common.data.Values
import slatekit.data.encoders.Encoders
import slatekit.samples.common.models.Delivery
import slatekit.samples.common.models.Movie

/**
 * Option 1: Reflection-Less mapper
 * Pros: More performant due to non-reflection use
 * Cons: Manual coding
 */
class MovieMapper1(val encoders:Encoders<Long, Movie>) : Mapper<Long, Movie> {

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


