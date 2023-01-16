package kiit.meta

import slatekit.common.data.DataType
import kiit.meta.models.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

open class Schema<TId, T>(val idType:KClass<*>, val enType:KClass<*>, val table:String = enType.simpleName!!) where TId : Comparable<TId>, T: Any {

    private var _model: Model = Model(enType, table)

    val model: Model get() = _model

    /**
     * Builds an id field using property reference to extract type information
     */
    fun id(
            prop: KProperty<*>,
            name: String? = null,
            type: DataType? = null,
            min: Int = -1,
            max: Int = -1,
            defaultValue: Any? = null,
            tags: List<String> = listOf()
    ): ModelField {
        val raw = field(prop, name, "", type, min, max, defaultValue, false, true,  FieldCategory.Id, tags)
        val field = raw.copy(isUnique = true, isIndexed = true, isUpdatable = false)
        return field
    }


    /**
     * Builds a normal field using property reference to extract type information
     */
    fun field(
            prop: KProperty<*>,
            name: String? = null,
            desc: String = "",
            type: DataType? = null,
            min: Int = -1,
            max: Int = -1,
            defaultValue: Any? = null,
            encrypt: Boolean = false,
            indexed: Boolean = false,
            category: FieldCategory = FieldCategory.Data,
            tags: List<String> = listOf()
    ): ModelField {
        val finalName = name ?: prop.name
        val finalType = prop.returnType
        val finalKClas = finalType.classifier as KClass<*>
        val required = !finalType.isMarkedNullable
        val fieldType = type ?: ModelUtils.getFieldType(finalType)
        val field = ModelField.build(
                prop, finalName, desc, finalKClas, fieldType, required,
                false, indexed, true,
                min, max, null, defaultValue, encrypt, tags, category
        )
        _model = _model.add(field)
        return field
    }


    /**
     * Builds a normal field using property reference to extract type information
     */
    fun field(
            name: String,
            type: KType,
            required:Boolean,
            desc: String = "",
            min: Int = -1,
            max: Int = -1,
            defaultValue: Any? = null,
            encrypt: Boolean = false,
            indexed: Boolean = false,
            category: FieldCategory = FieldCategory.Data,
            tags: List<String> = listOf()
    ): ModelField {
        val finalName = name
        val finalType = type
        val finalKClas = finalType.classifier as KClass<*>
        val fieldType = ModelUtils.getFieldType(finalType)
        val field = ModelField.build(
                null, finalName, desc, finalKClas, fieldType, required,
                false, indexed, true,
                min, max, null, defaultValue, encrypt, tags, category
        )
        _model = _model.add(field)
        return field
    }
}