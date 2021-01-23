/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.entities

import org.threeten.bp.*
import slatekit.common.EnumLike
import slatekit.common.Record
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.common.data.Mapper
import slatekit.common.data.Values
import slatekit.common.ids.UPID
import slatekit.data.encoders.Encoders
import slatekit.entities.core.EntityInfo
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import kotlin.reflect.KProperty
import slatekit.meta.models.Model
import slatekit.meta.models.ModelField
import slatekit.meta.models.ModelMapper
import slatekit.query.QueryEncoder

/**
 * Handles mapping an entity to/from a Record.
 * If using the EntityMapper, clients need to implement the methods here.
 *
 * DESIGN:
 * This is designed to:
 * 1. allow clients to map classes to records WHAT EVER WAY they want.
 * 2. only enforce types on Entity Id/Type
 * 3. support optional schema ( represented as a Model ) for mappings
 *
 * NOTE:
 * We are explicitly avoiding mapping via reflection and allows callers
 * to control how this is done. This also allows for potentially hooking
 * into some other mapping library to handle the heavy / tedious work.
 */
interface EntityMapper<TId, T> : Mapper<TId, T> where TId : Comparable<TId>, T:Any {

    val info:EntityInfo

    /**
     * Gets the optional Model schema which stores field/properties
     * and their corresponding column metadata
     */
    fun schema(): Model? = null

    /**
     * Gets the column name for the Kproperty from the model schema if available
     * or defaults to the property name.
     */
    fun columnName(prop: KProperty<*>): String {
        val model: Model? = schema()
        return when (model) {
            null -> prop.name
            else -> if (model.any) model.fields.first { it.name == prop.name }.storedName else prop.name
        }
    }
}

