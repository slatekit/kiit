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

import slatekit.common.Namer
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import kotlin.reflect.*


data class ModelField(
        val name: String,
        val desc: String = "",
        val prop: KProperty<*>? = null,
        val dataCls: KClass<*>,
        val dataTpe: KType,
        val storedName: String = "",
        val pos: Int = 0,
        val isRequired: Boolean = true,
        val minLength: Int = -1,
        val maxLength: Int = -1,
        val isEnum: Boolean = false,
        val isUnique:Boolean = false,
        val isUpdatable:Boolean = true,
        val defaultVal: Any? = null,
        val encrypt: Boolean = false,
        val key: String = "",
        val extra: String = "",
        val example: String = "",
        val tag: String = "",
        val cat: String = "",
        val model: Model? = null
) {

    override fun toString(): String {
        val text = StringBuilder()

        text.append("( name" + " : " + name)
        text.append(", desc" + " : " + desc)
        text.append(", dataCls" + " : " + dataCls)
        text.append(", storedName" + " : " + storedName)
        text.append(", pos" + " : " + pos)
        text.append(", isRequired" + " : " + isRequired)
        text.append(", minLength" + " : " + minLength)
        text.append(", maxLength" + " : " + maxLength)
        text.append(", defaultVal" + " : " + defaultVal)
        text.append(", encrypt" + " : " + encrypt)
        text.append(", example" + " : " + example)
        text.append(", key" + " : " + key)
        text.append(", extra" + " : " + extra)
        text.append(", tag" + " : " + tag)
        text.append(", cat" + " : " + cat)
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
         * @param autoIncrement
         * @return
         */
        fun id(name: String, dataType: KClass<*>, dataKType: KType): ModelField {
            return build(null, name, "", dataType, dataKType, true, 0, 0, name, 0, cat = "id")
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
        fun build(
                prop: KProperty<*>?,
                name: String,
                desc: String = "",
                dataType: KClass<*>,
                dataKType: KType,
                isRequired: Boolean = false,
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
                    minLength = minLength,
                    maxLength = maxLength,
                    defaultVal = defaultValue,
                    encrypt = encrypt,
                    key = "",
                    tag = tag,
                    cat = cat
            )
            return field
        }


        fun buildDestName(name: String, destName: String?, namer: Namer?): String {
            return when (destName) {
                null -> namer?.rename(name) ?: name
                else -> destName
            }
        }
    }
}
