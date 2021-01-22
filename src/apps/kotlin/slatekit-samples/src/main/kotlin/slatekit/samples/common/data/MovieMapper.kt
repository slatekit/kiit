package slatekit.samples.common.data

import slatekit.common.DateTime
import slatekit.common.Record
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.common.data.Mapper
import slatekit.common.data.Value
import slatekit.common.data.Values
import slatekit.entities.EntityMapper
import slatekit.meta.models.Model
import slatekit.meta.models.ModelMapper
import slatekit.samples.common.models.Delivery
import slatekit.samples.common.models.Movie
import java.util.*

/**
 * Option 1: Reflection-Less mapper
 * Pros: More performant due to non-reflection use
 * Cons: Manual coding
 */
class MovieMapperManual : Mapper<Long, Movie> {
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
                Value(Movie::uuid.name     , UUID.randomUUID()),
                Value(Movie::title.name    , ""),
                Value(Movie::category.name , ""),
                Value(Movie::playing.name  , false),
                Value(Movie::delivery.name , Delivery.Theater),
                Value(Movie::cost.name     , 0),
                Value(Movie::rating.name   , 4.0),
                Value(Movie::released.name , DateTime.now())
        )
    }
}



/**
 * Option 2: Schema based mapper with reflection
 * Pros: More convenient due to setting up schema once, no manual encoding/decoding
 * Cons: Less performant than manual
 */
object MovieMapperSchema : EntityMapper<Long, Movie> {
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


    override fun schema(): Model? = model


    override fun decode(record: Record, enc: Encryptor?): Movie? {

    }

    override fun encode(model: Movie, action: DataAction, enc: Encryptor?): Values {

    }
}


/**
 * Option 2: Schema based mapper with reflection
 * Pros: More convenient due to setting up schema once, no manual encoding/decoding
 * Cons: Less performant than manual
 */
object MovieMapperAuto : EntityMapper<Long, Movie> {
    val model = ModelMapper.loadSchema(Movie::class)


    override fun schema(): Model? = model


    override fun decode(record: Record, enc: Encryptor?): Movie? {
        TODO("Not yet implemented")
    }


    override fun encode(model: Movie, action: DataAction, enc: Encryptor?): Values {
        TODO("Not yet implemented")
    }
}