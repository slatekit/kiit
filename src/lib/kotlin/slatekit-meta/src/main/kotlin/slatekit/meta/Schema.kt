package slatekit.meta

import slatekit.meta.models.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class Schema<TId, T>(val idType:KClass<*>, val enType:KClass<*>, val table:String = enType.simpleName!!) : Builder where TId : Comparable<TId>, T: Any {

    private var _model: Model = Model(enType, table)

    val model: Model get() = _model

    /**
     * Builds an id field using property reference to extract type information
     */
    fun id(
            prop: KProperty<*>,
            name: String? = null,
            type: FieldType? = null,
            min: Int = -1,
            max: Int = -1,
            defaultValue: Any? = null,
            tags: List<String> = listOf()
    ): ModelField {
        val raw = field(prop, name, type, min, max, defaultValue, false, true,  FieldCategory.Id, tags)
        val field = raw.copy(isUnique = true, isIndexed = true, isUpdatable = false)
        return field
    }


    /**
     * Builds a normal field using property reference to extract type information
     */
    fun field(
            prop: KProperty<*>,
            name: String? = null,
            type: FieldType? = null,
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
                prop, finalName, "", finalKClas, fieldType, required,
                false, indexed, true,
                min, max, null, defaultValue, encrypt, tags, category
        )
        _model = _model.add(field)
        return field
    }
}