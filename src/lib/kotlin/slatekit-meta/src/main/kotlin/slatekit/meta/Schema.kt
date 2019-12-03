package slatekit.meta

import slatekit.meta.models.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class Schema<TId, T>(val idType:KClass<*>, val enType:KClass<*>, val table:String = enType.simpleName!!) : Builder where TId : Comparable<TId>, T: Any {

    private var _model: Model = Model(enType, table)

    val model: Model get() = _model

    fun id(
            prop: KProperty<*>,
            name: String? = null,
            type: ModelFieldType? = null,
            min: Int = -1,
            max: Int = -1,
            defaultValue: Any? = null,
            encrypt: Boolean = false,
            tags: List<String> = listOf()
    ): ModelField {
        val finalName = name ?: prop.name
        val finalType = prop.returnType
        val finalKClas = finalType.classifier as KClass<*>
        val required = !finalType.isMarkedNullable
        val fieldType = type ?: ModelUtils.getFieldType(finalType)
        val field = ModelField.build(
                prop, finalName, "", finalKClas, fieldType, required,
                true, true, true,
                min, max, null, defaultValue, encrypt, tags, ModelFieldCategory.Id
        )
        _model = _model.add(field)
        return field
    }


    fun field(
            prop: KProperty<*>,
            name: String? = null,
            type: ModelFieldType? = null,
            min: Int = -1,
            max: Int = -1,
            defaultValue: Any? = null,
            encrypt: Boolean = false,
            indexed: Boolean = false,
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
                min, max, null, defaultValue, encrypt, tags, ModelFieldCategory.Data
        )
        _model = _model.add(field)
        return field
    }
}