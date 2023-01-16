package kiit.entities.support

import kiit.entities.Entity
import kiit.meta.models.Model
import slatekit.results.Try

/**
 * Encoder for encoding/decoding to a String ( E.g. JSON )
 */
interface Encodable<TId, out T> where TId : Comparable<TId>, T : Entity<TId> {

    /**
     * Encodes the Entity to a String
     */
    fun <TId> encode(model: Model, instance: Entity<TId>): Try<String> where TId : Comparable<TId>

    /**
     * Decodes the string to an Entity
     */
    fun <TId> decode(model: Model, content: String): Try<Entity<TId>?> where TId : Comparable<TId>
}
