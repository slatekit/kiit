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

import slatekit.common.naming.Namer
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import kotlin.reflect.*

data class ModelField(
        @JvmField val name: String,
        @JvmField val desc: String = "",
        @JvmField val prop: KProperty<*>? = null,
        @JvmField val dataCls: KClass<*>,
        @JvmField val dataTpe: KType,
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
        @JvmField val tag: String = "",
        @JvmField val category: String = "",
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
        text.append(", tag : $tag")
        text.append(", category : $category")
        text.append(" )")
        return text.toString()
    }

    fun isBasicType(): Boolean = KTypes.isBasicType(dataCls) || isEnum

    fun isStandard(): Boolean {
        return when (tag) {
            "standard", "id", "meta" -> true
            else -> false
        }
    }

    companion object {

        /**
         * builds a new model field that is an id
         * @param name
         * @param dataType
         * @return
         */
        @JvmStatic
        fun id(name: String, dataType: KClass<*>, dataKType: KType): ModelField {
            return build(null, name, "", dataType, dataKType, true, true, true, false, 0, 0, name, 0, cat = "id")
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
            dataKType: KType,
            isRequired: Boolean = false,
            isUnique: Boolean = false,
            isIndexed: Boolean = false,
            isUpdatable: Boolean = true,
            minLength: Int = -1,
            maxLength: Int = -1,
            destName: String? = null,
            defaultValue: Any? = null,
            encrypt: Boolean = false,
            tag: String = "",
            cat: String = "data",
            namer: Namer? = null
        ): ModelField {

            val finalName = buildDestName(name, destName, namer)
            val isEnum = Reflector.isSlateKitEnum(dataType)
            val field = ModelField(
                    name = name,
                    desc = desc,
                    prop = prop,
                    dataCls = dataType,
                    dataTpe = dataKType,
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
                    tag = tag,
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
    }
}
