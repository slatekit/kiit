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

package slatekit.meta

import slatekit.common.EnumLike
import slatekit.common.EnumSupport
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaSetter

/**
 * Created by kishorereddy on 5/23/17.
 */
object Reflector {

    /**
     * Creates an instance of the type supplied ( assumes existance of a 0 param constructor )
     *
     * @tparam T
     * @return
     */

    @Suppress("UNCHECKED_CAST")
    fun <T> create(cls: KClass<*>): T {
        val con = cls.primaryConstructor!!
        val res = con.call()
        return res as T
    }

    /**
     * Creates an instance of the type supplied ( assumes existance of a 0 param constructor )
     *
     * @tparam T
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> createWithArgs(cls: KClass<*>, args: Array<Any?>): T {
        val con = cls.primaryConstructor!!
        val res = con.call(*args)
        return res as T
    }

    fun isSlateKitEnum(cls: KClass<*>): Boolean {
        val companion = cls.companionObjectInstance
        return when (companion) {
            is EnumSupport -> true
            else -> {
                val superCompanion = getSuperClassCompanion(cls)
                val result = when(superCompanion){
                    is EnumSupport -> true
                    else -> false
                }
                result
            }
        }
    }

    fun getEnumSample(cls: KClass<*>): Int {
        val companion = cls.companionObjectInstance
        return when (companion) {
            is EnumSupport -> companion.all()[0].value
            else -> -1
        }
    }

    fun getEnumValue(cls: KClass<*>, value: Any?): EnumLike {
        val companion = cls.companionObjectInstance
        return when (companion) {
            is EnumSupport -> getEnumValue(cls, companion, value)
            else -> {
                val superCompanion = getSuperClassCompanion(cls)
                when(superCompanion){
                    is EnumSupport -> getEnumValue(cls, superCompanion, value)
                    else -> throw Exception("Unable to dynamically parse enum : " + cls.qualifiedName + ", enum does not extend EnumSupport")
                }
            }
        }
    }

    fun getSuperClassCompanion(cls:KClass<*>):Any? {
        return if(cls.supertypes.isNotEmpty()) {
            val first = cls.supertypes.firstOrNull()?.classifier
            first?.let {
                val parent = it as KClass<*>
                val comp = parent.companionObjectInstance
                comp
            }
        } else {
            null
        }
    }

    fun getEnumValue(cls:KClass<*>, companion:EnumSupport, value:Any?): EnumLike {
        return when (value) {
            is EnumLike -> value
            is Int -> companion.convert(value)
            is Long -> companion.convert(value.toInt())
            is String -> companion.parse(value)
            null -> throw Exception("Unable to dynamically parse enum : " + cls.qualifiedName + ", with null value")
            else -> throw Exception("Unable to dynamically parse enum : " + cls.qualifiedName + ", with value : " + value)
        }
    }

    fun getFieldValue(inst: Any, name: String): Any? {
        val item = findField(inst, name)
        return item?.getter?.call(inst)
    }

    fun getFieldValue(inst: Any, prop: KProperty<*>): Any? {

        return prop.getter.call(inst)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAnnotationForClass(cls: KClass<*>, anoType: KClass<*>): T {

        val ano = cls.annotations.filter { it -> it.annotationClass == anoType }.first()
        return ano as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAnnotationForClassOpt(cls: KClass<*>, anoType: KClass<*>): T? {

        val ano = cls.annotations.filter { it -> it.annotationClass == anoType }.firstOrNull()
        return ano as? T
    }

    @Suppress("UNCHECKED_CAST")
    fun getMembers(
        cls: KClass<*>,
        declared: Boolean,
        filterOutBuiltins: Boolean,
        visibility: KVisibility? = null
    ): List<KCallable<*>> {
        val members = (if (declared) cls.declaredMembers else cls.members).filter { it ->
            visibility?.let { v -> v == it.visibility } ?: true
        }
        return members.filter { mem -> if (filterOutBuiltins) !isBuiltIn(mem) else true }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAnnotationForMember(member: KCallable<*>, anoType: KClass<*>): T? {

        val anno = member.annotations.filter { it -> it.annotationClass == anoType }.firstOrNull()
        return anno as? T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAnnotatedMembers(cls: KClass<*>, anoType: KClass<*>, declared: Boolean = true): List<Pair<KCallable<*>, T>> {

        val members = if (declared) cls.declaredMemberFunctions else cls.memberFunctions
        val filtered = members.map { member ->
            Pair(member, member.annotations.filter { annotation ->
                annotation.annotationClass == anoType
            }.firstOrNull())
        }
        // 1. filter out ones with annotation supplied
        // 2. convert them to type T
        return filtered.filter { pair -> pair.second != null }
                .map { (first, second) -> Pair(first, second as T) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAnnotatedMembersOpt(cls: KClass<*>, anoType: KClass<*>, declared: Boolean = true): List<Pair<KCallable<*>, T?>> {

        val members = if (declared) cls.declaredMemberFunctions else cls.memberFunctions
        val filtered = members.filter { it.visibility == KVisibility.PUBLIC }
                              .map { Pair(it, it.annotations.filter { anno -> anno.annotationClass == anoType }.firstOrNull() as? T) }
        return filtered
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAnnotatedProps(cls: KClass<*>, anoType: KClass<*>): List<Pair<KProperty<*>, T?>> {

        val props = getProperties(cls).map { prop ->
            Pair(prop, prop.annotations.filter { annotation ->
                annotation.annotationClass == anoType
            }.firstOrNull())
        }
        // 1. filter out ones with annotation supplied
        // 2. convert them to type T
        return props.filter { pair -> pair.second != null }
                .map { (first, second) -> Pair(first, second as T) }
    }

    fun setFieldValue(cls: KClass<*>, inst: Any?, name: String, value: Any?) {
        val prop = cls.declaredMemberProperties.find { it.name == name }
        val item = prop as KMutableProperty1<Any, *>
        item.javaSetter?.invoke(inst, value)
    }

    fun setFieldValue(inst: Any?, prop: KProperty<*>, value: Any?) {
        inst?.let { inst ->
            val item = prop as KMutableProperty1<Any, *>
            item.javaSetter?.invoke(inst, value)
        }
    }

    fun findField(inst: Any, name: String): KProperty1<Any, *>? {
        val item = inst.kClass.declaredMemberProperties.find { it.name == name }
        return item
    }

    fun findProperty(cls: KClass<*>, name: String): KProperty<*>? {
        val item = cls.declaredMemberProperties.find { it.name == name }
        return item
    }

    fun findPropertyExtended(cls: KClass<*>, name: String): KProperty<*>? {
        val item = cls.memberProperties.find { it.name == name }
        return item
    }

    fun getMethod(cls: KClass<*>, name: String): KCallable<*>? {
        val mem = cls.members.find { m -> m.name == name }
        return mem
    }

    fun getMethodArgs(cls: KClass<*>, name: String): Collection<KParameter>? {
        val mem = cls.members.find { m -> m.name == name }
        return mem?.parameters
    }

    fun callMethod(cls: KClass<*>, inst: Any, name: String, args: Array<Any?>): Any? {
        val mem = cls.members.find { m -> m.name == name }
        val params = arrayOf(inst, *args)
        return mem?.call(*params)
    }

    fun getProperties(cls: KClass<*>): List<KProperty<*>> {
        return if (cls.isData) {
            val propsMap = cls.memberProperties.map { it.name to it }.toMap()
            val items = cls.primaryConstructor!!.parameters.map { it -> propsMap[it.name]!! }
            items
        } else {
            cls.memberProperties.toList()
        }
    }

    fun isBuiltIn(mem: KCallable<*>): Boolean = mem.name in arrayOf("equals", "hashCode", "toString")

    fun isDataClass(cls: KClass<*>): Boolean = cls.isData

    // fun getTypeFromProperty(tpe: KProperty<*>): KClass<*> {
    //    return when (tpe.returnType.toString()) {
    //        "kotlin.String"            -> KTypes.KStringClass
    //        "kotlin.Boolean"           -> KTypes.KBoolClass
    //        "kotlin.Short"             -> KTypes.KShortClass
    //        "kotlin.Int"               -> KTypes.KIntClass
    //        "kotlin.Long"              -> KTypes.KLongClass
    //        "kotlin.Float"             -> KTypes.KFloatClass
    //        "kotlin.Double"            -> KTypes.KDoubleClass
    //        "java.time.LocalDate"      -> KTypes.KLocalDateClass
    //        "java.time.LocalTime"      -> KTypes.KLocalTimeClass
    //        "java.time.LocalDateTime"  -> KTypes.KLocalDateTimeClass
    //        "slatekit.common.DateTime" -> KTypes.KDateTimeClass
    //        "java.util.UUID"           -> KTypes.KUUIDClass
    //        "slatekit.common.ids.UniqueId" -> KTypes.KUniqueIdClass
    //        else                       -> Any::class
    //    }
    // }

    //    fun findFieldJ(inst:Any, name:String): Field? {
    //        val item = inst.kClass.java.declaredFields.find { it.name == name }
    //        for(m in inst.kClass.java.declaredFields){
    //            if(m.name == name){
    //                println(m)
    //            }
    //        }
    //        return item
    //    }
}

val <T : Any> T.kClass: KClass<T> get() = javaClass.kotlin
