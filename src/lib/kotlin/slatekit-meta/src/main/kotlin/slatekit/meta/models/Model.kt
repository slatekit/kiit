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


import slatekit.common.nonEmptyOrDefault
import slatekit.meta.KTypes
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType


/**
 * Stores the schema of a data-model with properties.
 * @param name     :
 * @param fullName :
 * @param dataType :
 */
class Model(val name: String,
            val fullName: String,
            val dataType: KClass<*>? = null,
            val desc: String = "",
            tableName: String = "",
            private val _propList: List<ModelField>? = null) {

    constructor(dataType: KClass<*>) : this(dataType.simpleName!!, dataType.qualifiedName!!, dataType)

    /**
     * The name of the table
     */
    val table = tableName.nonEmptyOrDefault(name)


    /**
     * The field that represents the id
     */
    val idField: ModelField? get() = _propList?.find { p -> p.cat == "id" }


    /**
     * The mapping of property names to the fields.
     */
    val _propMap = _propList?.toHashSet()


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
    val size: Int get() = _propList?.size ?: 0


    /**
     * gets the list of fields in this model or returns an emptylist if none
     * @return
     */
    val fields: List<ModelField> get() = _propList ?: listOf<ModelField>()


    /**
     * builds a new model by adding an text field to the list of fields
     * @param name
     * @param desc
     * @param isRequired
     * @param minLength
     * @param maxLength
     * @param storedName
     * @param defaultValue
     * @param tag
     * @param cat
     * @return
     */
    fun add(field: KProperty<*>, desc: String = "", minLength: Int = 0, maxLength: Int = 50, storedName: String? = null,
            defaultValue: String = "", encrypt:Boolean = false, tag: String = "", cat: String = "data"
    ): Model {
        return addField(field,
                field.name,
                KTypes.getClassFromType(field.returnType),
                field.returnType,
                desc,
                !field.returnType.isMarkedNullable,
                minLength, maxLength, storedName, defaultValue, encrypt, tag, cat)
    }


    /**
     * builds a new model by adding an id field to the list of fields
     * @param name
     * @param dataType
     * @param autoIncrement
     * @return
     */
    fun addId(field: KProperty<*>, autoIncrement: Boolean = false): Model {
        return addField(field, field.name,
                KTypes.getClassFromType(field.returnType),
                field.returnType, "", true, 0, 0, name, 0, cat = "id")
    }


    /**
     * builds a new model by adding a new field to the list of fields using the supplied fields.
     * @param name
     * @param dataType
     * @param desc
     * @param isRequired
     * @param minLength
     * @param maxLength
     * @param destName
     * @param defaultValue
     * @param tag
     * @param cat
     * @return
     */
    fun addField(
            prop:KProperty<*>?,
            name:String,
            dataType: KClass<*>,
            dataKType: KType,
            desc: String = "",
            isRequired: Boolean = false,
            minLength: Int = -1,
            maxLength: Int = -1,
            destName: String? = null,
            defaultValue: Any? = null,
            encrypt:Boolean = false,
            tag: String = "",
            cat: String = "data"
    ): Model {
        val field = ModelField.build(prop, name, desc, dataType, dataKType, isRequired, minLength, maxLength, destName, defaultValue, encrypt, tag, cat)
        return add(field)
    }


    fun add(field: ModelField): Model {
        val newPropList = _propList?.plus(field) ?: listOf(field)
        return Model(this.name, fullName, this.dataType, desc, table, newPropList)
    }
}
