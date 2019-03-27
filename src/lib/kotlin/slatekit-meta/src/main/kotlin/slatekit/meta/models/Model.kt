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
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

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

    /**
     * The name of the table
     */
    val table = tableName.nonEmptyOrDefault(name)

    /**
     * gets the list of fields in this model or returns an emptylist if none
     * @return
     */
    val fields: List<ModelField> = modelFields ?: listOf()

    /**
     * The field that represents the id
     */
    val idField: ModelField? get() = fields.find { p -> p.category == ModelFieldCategory.Id }

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


    /**
     * builds a new model by adding an text field to the list of fields
     * @param desc
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        val fieldType = ModelUtils.fieldType(field)
        return addField(
                field,
                field.name,
                KTypes.getClassFromType(field.returnType),
                fieldType,
                desc,
                !field.returnType.isMarkedNullable,
                minLength, maxLength, storedName, defaultValue, encrypt, tag, cat
        )
    }/**
     * builds a new model by adding an text field to the list of fields
     * @param desc
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
            required:Boolean,
            desc: String = "",
            minLength: Int = 0,
            maxLength: Int = 50,
            storedName: String? = null,
            defaultValue: String = "",
            encrypt: Boolean = false,
            tag: String = "",
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        val fieldType = ModelUtils.fieldType(field)
        return addField(
                field,
                field.name,
                KTypes.getClassFromType(field.returnType),
                fieldType,
                desc,
                required,
                minLength, maxLength, storedName, defaultValue, encrypt, tag, cat
        )
    }

    /**
     * builds a new model by adding an id field to the list of fields
     * @param autoIncrement
     * @return
     */
    fun addId(field: KProperty<*>, autoIncrement: Boolean = false): Model {
        val type = KTypes.getClassFromType(field.returnType)
        return addId(field.name, type, autoIncrement)
    }

    /**
     * builds a new model by adding an id field to the list of fields
     * @param name
     * @param dataType
     * @param autoIncrement
     * @return
     */
    fun addId(name: String, dataType: KClass<*>, autoIncrement: Boolean = false): Model {
        val fieldType = when(dataType){
            Int::class -> ModelFieldType.typeInt
            Long::class -> ModelFieldType.typeLong
            UUID::class -> ModelFieldType.typeUUID
            else -> throw Exception("Unexpected id type for model for : ${dataType.qualifiedName}")
        }
        return addField(null, name, dataType, fieldType, "", true, 0, 0, name, 0, cat = ModelFieldCategory.Id)
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
    fun addString(
            name: String,
            desc: String = "",
            isRequired: Boolean = false,
            minLength: Int = 0,
            maxLength: Int = 50,
            storedName: String? = null,
            defaultValue: String = "",
            encrypt: Boolean = false,
            tag: String = "",
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(
                name, String::class, ModelFieldType.typeString, desc, isRequired, minLength, maxLength, storedName,
                defaultValue, encrypt, tag, cat
        )
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(
                name, String::class, ModelFieldType.typeText, desc, isRequired, minLength, maxLength, storedName,
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(
                name,
                Boolean::class,
                ModelFieldType.typeBool,
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(
                name,
                LocalDate::class,
                ModelFieldType.typeLocalDate,
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(
                name,
                LocalTime::class,
                ModelFieldType.typeLocalTime,
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(
                name,
                LocalDateTime::class,
                ModelFieldType.typeLocalDateTime,
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(
                name,
                DateTime::class,
                ModelFieldType.typeDateTime,
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(name, Short::class, ModelFieldType.typeShort, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(name, Int::class, ModelFieldType.typeInt, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(name, Long::class, ModelFieldType.typeLong, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(name, Float::class, ModelFieldType.typeFloat, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
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
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        return addField(name, Double::class, ModelFieldType.typeDouble, desc, isRequired, 0, 0, storedName, 0, false, tag, cat)
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
                ModelFieldType.typeObject,
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
                ModelFieldType.typeEnum,
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
            dataCls: KClass<*>,
            dataTpe: ModelFieldType,
            desc: String = "",
            isRequired: Boolean = false,
            minLength: Int = -1,
            maxLength: Int = -1,
            destName: String? = null,
            defaultValue: Any? = null,
            encrypt: Boolean = false,
            tag: String = "",
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        val field = ModelField.build(
                null, name, desc, dataCls, dataTpe, isRequired,
                false, false, true,
                minLength, maxLength, destName, defaultValue, encrypt, tag, cat
        )
        return add(field)
    }


    fun addFields(fields:List<ModelField>): Model {
        val newFields = fields.plus(fields)
        return Model(this.name, fullName, this.dataType, desc, table, newFields)
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
            dataCls: KClass<*>,
            dataTpe: ModelFieldType,
            desc: String = "",
            isRequired: Boolean = false,
            minLength: Int = -1,
            maxLength: Int = -1,
            destName: String? = null,
            defaultValue: Any? = null,
            encrypt: Boolean = false,
            tag: String = "",
            cat: ModelFieldCategory = ModelFieldCategory.Data
    ): Model {
        val field = ModelField.build(
                prop, name, desc, dataCls, dataTpe, isRequired,
                false, false, true,
                minLength, maxLength, destName, defaultValue, encrypt, tag, cat
        )
        return add(field)
    }

    fun add(field: ModelField): Model {
        val newPropList = fields.plus(field)
        return Model(this.name, fullName, this.dataType, desc, table, newPropList)
    }
}
