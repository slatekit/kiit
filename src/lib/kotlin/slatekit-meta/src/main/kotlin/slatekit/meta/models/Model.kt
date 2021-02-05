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

package slatekit.meta.models

import slatekit.common.Field
import slatekit.common.Id
import slatekit.common.naming.Namer
import slatekit.common.ext.orElse
import slatekit.meta.Reflector
import slatekit.meta.Schema
import kotlin.reflect.KClass

/**
 * Stores the schema of a data-model with properties.
 */
class Model(
        val name: String,
        val fullName: String,
        val dataType: KClass<*>? = null,
        val desc: String = "",
        tableName: String = "",
        modelFields: List<ModelField>? = null,
        val namer: Namer? = null
) {

    constructor(dataType: KClass<*>, tableName: String = "") : this(dataType.simpleName!!, dataType.qualifiedName!!, dataType, tableName = tableName)
    constructor(dataType: KClass<*>, fields: List<ModelField>, tableName: String = "") : this(dataType.simpleName!!, dataType.qualifiedName!!, dataType, tableName = tableName, modelFields = fields)

    /**
     * The name of the table
     */
    val table = tableName.orElse(name)

    /**
     * gets the list of fields in this model or returns an emptylist if none
     * @return
     */
    val fields: List<ModelField> = modelFields ?: listOf()

    /**
     * Lookup of field names to column names
     */
    val lookup: Map<String, ModelField> = fields.map { it.name to it }.toMap()

    /**
     * The field that represents the id
     */
    val idField: ModelField? = fields.find { p -> p.category == FieldCategory.Id }

    /**
     * whether there are any fields in the model
     * @return
     */
    val any: Boolean get() = size > 0

    /**
     * whether this model has an id field
     * @return
     */
    val hasId: Boolean get() = idField != null

    /**
     * the number of fields in this model.
     * @return
     */
    val size: Int get() = fields.size


    fun add(field: ModelField): Model {
        val newPropList = fields.plus(field)
        return Model(this.name, fullName, this.dataType, desc, table, newPropList)
    }


    companion object {

        inline fun <reified TId, reified T> of(builder: Schema<TId, T>.() -> Unit ): Model where TId : Comparable<TId>, T:Any {
            val schema = Schema<TId, T>(TId::class, T::class)
            builder(schema)
            return schema.model
        }

        fun <TId, T> of(idType:KClass<*>, tType:KClass<*>, builder: Schema<TId, T>.() -> Unit ): Model where TId : Comparable<TId>, T:Any {
            val schema = Schema<TId, T>(idType, tType)
            builder(schema)
            return schema.model
        }

        /**
         * Builds a schema ( Model ) from the Class/Type supplied.
         * NOTE: The mapper then works off the Model class for to/from mapping of data to model.
         * @param dataType
         * @return
         */
        @JvmStatic
        fun loadSchema(dataType: KClass<*>, idFieldName: String? = null, namer: Namer? = null, table: String? = null): Model {
            val modelName = dataType.simpleName ?: ""
            val modelNameFull = dataType.qualifiedName ?: ""

            // Get Id
            val idFields = Reflector.getAnnotatedProps<Id>(dataType, Id::class)
            val idField = idFields.firstOrNull()

            // Now add all the fields.
            val matchedFields = Reflector.getAnnotatedProps<Field>(dataType, Field::class)

            // Loop through each field
            val withAnnos = matchedFields.filter { it.second != null }
            val fields = withAnnos.map { matchedField ->
                val modelField = ModelField.ofData(matchedField.first, matchedField.second!!,
                        namer, idField == null, idFieldName)
                val finalModelField = if (!modelField.isBasicType()) {
                    val model = loadSchema(modelField.dataCls, namer = namer)
                    modelField.copy(model = model)
                } else modelField
                finalModelField
            }
            val allFields = when(idField) {
                null -> fields
                else -> mutableListOf(ModelField.ofId(idField.first, "", namer)).plus(fields)
            }
            return Model(modelName, modelNameFull, dataType, modelFields = allFields, namer = namer, tableName = table ?: modelName)
        }
    }
}
