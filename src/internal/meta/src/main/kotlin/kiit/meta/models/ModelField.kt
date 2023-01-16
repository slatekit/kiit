/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.meta.models

import kiit.common.data.DataType
import kiit.utils.naming.Namer
import kiit.meta.KTypes
import kiit.meta.Reflector
import kotlin.reflect.*
import kotlin.reflect.jvm.jvmErasure

data class ModelField(
        @JvmField val name: String,
        @JvmField val desc: String = "",
        @JvmField val prop: KProperty<*>? = null,
        @JvmField val dataCls: KClass<*>,
        @JvmField val dataTpe: DataType = ModelUtils.fieldType(dataCls),
        @JvmField val storedName: String = "",
        @JvmField val pos: Int = 0,
        @JvmField val isRequired: Boolean = true,
        @JvmField val minLength: Int = -1,
        @JvmField val maxLength: Int = -1,
        @JvmField val isEnum: Boolean = false,
        @JvmField val isUnique: Boolean = false,
        @JvmField val isIndexed: Boolean = false,
        @JvmField val isUpdatable: Boolean = true,
        @JvmField val defaultValue: Any? = null,
        @JvmField val encrypt: Boolean = false,
        @JvmField val key: String = "",
        @JvmField val extra: String = "",
        @JvmField val example: String = "",
        @JvmField val format: String = "",
        @JvmField val tags: List<String> = listOf(),
        @JvmField val category: FieldCategory = FieldCategory.Data,
        @JvmField val model: Model? = null
) {

    override fun toString(): String {
        val text = StringBuilder()

        text.append("( name : $name")
        text.append(", desc : $desc")
        text.append(", dataCls : $dataCls")
        text.append(", storedName : $storedName")
        text.append(", pos : $pos")
        text.append(", isRequired : $isRequired")
        text.append(", minLength : $minLength")
        text.append(", maxLength : $maxLength")
        text.append(", defaultValue : $defaultValue")
        text.append(", encrypt : $encrypt")
        text.append(", example : $example")
        text.append(", format : $format")
        text.append(", key : $key")
        text.append(", extra : $extra")
        text.append(", tag : $tags")
        text.append(", category : $category")
        text.append(" )")
        return text.toString()
    }

    fun isBasicType(): Boolean = KTypes.isBasicType(dataCls) || isEnum

    fun isStandard(): Boolean {
        val result = listOf("standard", "id", "meta").filter { tags.contains(it) }
        return result.isNotEmpty()
    }

    companion object {

        /**
         * builds a new model field that is an id
         * @param name
         * @param dataType
         * @return
         */
        @JvmStatic
        fun id(name: String, dataType: KClass<*>, dataTpe: DataType): ModelField {
            return build(null, name, "", dataType, dataTpe, true,
                    true, true, false,
                    0, 0, name, 0, cat = FieldCategory.Id)
        }

        /**
         * builds an model field using all the fields supplied.
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
        @JvmStatic
        fun build(
                prop: KProperty<*>?,
                name: String,
                desc: String = "",
                dataType: KClass<*>,
                dataFieldType: DataType,
                isRequired: Boolean = false,
                isUnique: Boolean = false,
                isIndexed: Boolean = false,
                isUpdatable: Boolean = true,
                minLength: Int = -1,
                maxLength: Int = -1,
                destName: String? = null,
                defaultValue: Any? = null,
                encrypt: Boolean = false,
                tags: List<String> = listOf(),
                cat: FieldCategory = FieldCategory.Data,
                namer: Namer? = null
        ): ModelField {

            val finalName = buildDestName(name, destName, namer)
            val isEnum = Reflector.isSlateKitEnum(dataType)
            val field = ModelField(
                    name = name,
                    desc = desc,
                    prop = prop,
                    dataCls = dataType,
                    dataTpe = dataFieldType,
                    isEnum = isEnum,
                    storedName = finalName,
                    pos = 0,
                    isRequired = isRequired,
                    isUnique = isUnique,
                    isIndexed = isIndexed,
                    isUpdatable = isUpdatable,
                    minLength = minLength,
                    maxLength = maxLength,
                    defaultValue = defaultValue,
                    encrypt = encrypt,
                    key = "",
                    tags = tags,
                    category = cat
            )
            return field
        }

        @JvmStatic
        fun buildDestName(name: String, destName: String?, namer: Namer?): String {
            return when (destName) {
                null -> namer?.rename(name) ?: name
                else -> destName
            }
        }




        @JvmStatic
        fun ofId(prop:KProperty<*>, rawName:String, namer: Namer?):ModelField {
            val name = if (rawName.isNullOrEmpty()) prop.name else rawName
            val fieldKType = prop.returnType
            val fieldType = ModelUtils.fieldType(prop)
            val fieldCls = fieldKType.jvmErasure
            val optional = fieldKType.isMarkedNullable
            val field = ModelField.build(
                    prop = prop, name = name,
                    dataType = fieldCls,
                    dataFieldType = fieldType,
                    isRequired = !optional,
                    isIndexed = false,
                    isUnique = false,
                    isUpdatable = false,
                    cat = FieldCategory.Id,
                    namer = namer
            )
            return field
        }


        @JvmStatic
        fun ofData(prop:KProperty<*>, anno: Field, namer: Namer?, checkForId:Boolean, idFieldName:String?):ModelField {
            val name = if (anno.name.isNullOrEmpty()) prop.name else anno.name
            val cat = idFieldName?.let {
                if(it == name)
                    FieldCategory.Id
                else
                    FieldCategory.Data
            } ?: FieldCategory.Data

            val required = anno.required
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
}
