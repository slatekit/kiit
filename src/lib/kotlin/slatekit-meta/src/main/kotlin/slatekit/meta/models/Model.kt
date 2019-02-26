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

import slatekit.common.DateTime
import slatekit.common.naming.Namer
import slatekit.common.nonEmptyOrDefault
import slatekit.meta.KTypes
//import java.time.*
import org.threeten.bp.*
import slatekit.common.DateTimes
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * Stores the schema of a data-model with properties.
 */
class Model(
    val name: String,
    val fullName: String,
    val dataType: KClass<*>? = null,
    val desc: String = "",
    tableName: String = "",
    private val _propList: List<ModelField>? = null,
    val namer: Namer? = null
) {

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
    fun add(
        field: KProperty<*>,
        desc: String = "",
        minLength: Int = 0,
        maxLength: Int = 50,
        storedName: String? = null,
        defaultValue: String = "",
        encrypt: Boolean = false,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(
            field,
            field.name,
            KTypes.getClassFromType(field.returnType),
            field.returnType,
            desc,
            !field.returnType.isMarkedNullable,
            minLength, maxLength, storedName, defaultValue, encrypt, tag, cat
        )
    }

    /**
     * builds a new model by adding an id field to the list of fields
     * @param name
     * @param dataType
     * @param autoIncrement
     * @return
     */
    fun addId(field: KProperty<*>, autoIncrement: Boolean = false): Model {
        return addField(
            field, field.name,
            KTypes.getClassFromType(field.returnType),
            field.returnType, "", true, 0, 0, name, 0, cat = "id"
        )
    }

    /**
     * builds a new model by adding an id field to the list of fields
     * @param name
     * @param dataType
     * @param autoIncrement
     * @return
     */
    fun addId(name: String, dataType: KClass<*>, autoIncrement: Boolean = false): Model {
        return addField(null, name, Long::class, dataType.createType(), "", true, 0, 0, name, 0, cat = "id")
    }

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
    fun addText(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        minLength: Int = 0,
        maxLength: Int = 50,
        storedName: String? = null,
        defaultValue: String = "",
        encrypt: Boolean = false,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(
            name, String::class, KTypes.KStringType, desc, isRequired, minLength, maxLength, storedName,
            defaultValue, encrypt, tag, cat
        )
    }

    /**
     * builds a new model by adding a bool field to the list of fields
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addBool(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(
            name,
            Boolean::class,
            KTypes.KBoolType,
            desc,
            isRequired,
            0,
            0,
            storedName,
            false,
            false,
            tag,
            cat
        )
    }

    /**
     * builds a new model by adding a date field to the list of fields
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addLocalDate(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(
            name,
            LocalDate::class,
            KTypes.KLocalDateType,
            desc,
            isRequired,
            0,
            0,
            storedName,
            DateTimes.MIN,
            false,
            tag,
            cat
        )
    }

    /**
     * builds a new model by adding a time field to the list of fields
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addLocalTime(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(
            name,
            LocalTime::class,
            KTypes.KLocalTimeType,
            desc,
            isRequired,
            0,
            0,
            storedName,
            DateTimes.MIN,
            false,
            tag,
            cat
        )
    }

    /**
     * builds a new model by adding a datetime field to the list of fields
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addLocalDateTime(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(
            name,
            LocalDateTime::class,
            KTypes.KLocalDateTimeType,
            desc,
            isRequired,
            0,
            0,
            storedName,
            DateTimes.MIN,
            false,
            tag,
            cat
        )
    }

    /**
     * builds a new model by adding a date field to the list of fields
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addDateTime(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(
            name,
            DateTime::class,
            KTypes.KDateTimeType,
            desc,
            isRequired,
            0,
            0,
            storedName,
            DateTimes.MIN,
            false,
            tag,
            cat
        )
    }

    /**
     * builds a new model by adding a short field to the list of fields.
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addShort(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(name, Short::class, KTypes.KShortType, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
    }

    /**
     * builds a new model by adding a new integer field to the list of fields.
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addInt(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(name, Int::class, KTypes.KIntType, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
    }

    /**
     * builds a new model by adding a new long field to the list of fields.
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addLong(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(name, Long::class, KTypes.KLongType, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
    }

    /**
     * builds a new model by adding a new double field to the list of fields.
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addFloat(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(name, Float::class, KTypes.KFloatType, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
    }

    /**
     * builds a new model by adding a new double field to the list of fields.
     * @param name
     * @param desc
     * @param isRequired
     * @param storedName
     * @param tag
     * @param cat
     * @return
     */
    fun addDouble(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        storedName: String? = null,
        tag: String = "",
        cat: String = "data"
    ): Model {
        return addField(name, Double::class, KTypes.KDoubleType, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
    }

    /**
     * builds a new model by adding a new object field to the list of fields.
     * @param name
     * @param desc
     * @param isRequired
     * @param dataType
     * @param storedName
     * @param defaultValue
     * @return
     */
    fun addObject(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        dataType: KClass<*>,
        storedName: String? = null,
        defaultValue: Any? = null
    ): Model {
        return addField(
            null,
            name,
            dataType,
            dataType.createType(),
            desc,
            isRequired,
            0,
            0,
            storedName,
            defaultValue,
            false
        )
    }

    /**
     * builds a new model by adding a new object field to the list of fields.
     * @param name
     * @param desc
     * @param isRequired
     * @param dataType
     * @param storedName
     * @param defaultValue
     * @return
     */
    fun addEnum(
        name: String,
        desc: String = "",
        isRequired: Boolean = false,
        dataType: KClass<*>,
        storedName: String? = null,
        defaultValue: Any? = null
    ): Model {
        return addField(
            null,
            name,
            dataType,
            dataType.createType(),
            desc,
            isRequired,
            0,
            0,
            storedName,
            defaultValue,
            false
        )
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
        name: String,
        dataType: KClass<*>,
        dataKType: KType,
        desc: String = "",
        isRequired: Boolean = false,
        minLength: Int = -1,
        maxLength: Int = -1,
        destName: String? = null,
        defaultValue: Any? = null,
        encrypt: Boolean = false,
        tag: String = "",
        cat: String = "data"
    ): Model {
        val field = ModelField.build(
            null, name, desc, dataType, dataKType, isRequired,
            false, false, true,
            minLength, maxLength, destName, defaultValue, encrypt, tag, cat
        )
        return add(field)
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
    private fun addField(
        prop: KProperty<*>?,
        name: String,
        dataType: KClass<*>,
        dataKType: KType,
        desc: String = "",
        isRequired: Boolean = false,
        minLength: Int = -1,
        maxLength: Int = -1,
        destName: String? = null,
        defaultValue: Any? = null,
        encrypt: Boolean = false,
        tag: String = "",
        cat: String = "data"
    ): Model {
        val field = ModelField.build(
            prop, name, desc, dataType, dataKType, isRequired,
            false, false, true,
            minLength, maxLength, destName, defaultValue, encrypt, tag, cat
        )
        return add(field)
    }

    fun add(field: ModelField): Model {
        val newPropList = _propList?.plus(field) ?: listOf(field)
        return Model(this.name, fullName, this.dataType, desc, table, newPropList)
    }
}
