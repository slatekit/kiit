package kiit.entities

import kiit.utils.naming.Namer
import kiit.meta.Reflector
import kiit.meta.models.FieldCategory
import kiit.meta.models.Model
import kiit.meta.models.ModelField
import kiit.meta.models.ModelUtils
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

object Schema {

    /**
     * Builds a schema ( Model ) from the Class/Type supplied.
     * NOTE: The mapper then works off the Model class for to/from mapping of data to model.
     * @param dataType
     * @return
     */
    @JvmStatic
    fun load (dataType: KClass<*>, idFieldName: String? = null, namer: Namer? = null, table: String? = null, schema:String? = null): Model {
        val modelName = dataType.simpleName ?: ""
        val modelNameFull = dataType.qualifiedName ?: ""

        // Get Id
        val idFields = Reflector.getAnnotatedProps<Id>(dataType, Id::class)
        val idField = idFields.firstOrNull()

        // Now add all the fields.
        val matchedFields = Reflector.getAnnotatedProps<Column>(dataType, Column::class)

        // Loop through each field
        val withAnnos = matchedFields.filter { it.second != null }
        val fields = withAnnos.map { matchedField ->
            val modelField = column(matchedField.first, matchedField.second!!,
                namer, idField == null, idFieldName)
            val finalModelField = if (!modelField.isBasicType()) {
                val model = load(modelField.dataCls, namer = namer)
                modelField.copy(model = model)
            } else modelField
            finalModelField
        }
        val allFields = when(idField) {
            null -> fields
            else -> mutableListOf(ModelField.ofId(idField.first, "", namer)).plus(fields)
        }
        return Model(modelName, modelNameFull, schema ?: "", dataType, modelFields = allFields, namer = namer, tableName = table ?: modelName)
    }


    @JvmStatic
    fun column(prop: KProperty<*>, anno: Column, namer: Namer?, checkForId:Boolean, idFieldName:String?):ModelField {
        val name = if (anno.name.isNullOrEmpty()) prop.name else anno.name
        val cat = idFieldName?.let {
            if(it == name)
                FieldCategory.Id
            else
                FieldCategory.Data
        } ?: FieldCategory.Data

        // Get whether its required based on nullable property.
        val required = !prop.returnType.isMarkedNullable
        val length = anno.length
        val encrypt = anno.encrypt
        val fieldKType = prop.returnType
        val fieldType = ModelUtils.fieldType(prop)
        val fieldCls = fieldKType.jvmErasure
        val field = ModelField.build(
            prop = prop, name = name,
            dataType = fieldCls,
            dataFieldType = fieldType,
            isRequired = required,
            isIndexed = anno.indexed,
            isUnique = anno.unique,
            isUpdatable = anno.updatable,
            maxLength = length,
            encrypt = encrypt,
            cat = cat,
            namer = namer
        )
        return field
    }
}
