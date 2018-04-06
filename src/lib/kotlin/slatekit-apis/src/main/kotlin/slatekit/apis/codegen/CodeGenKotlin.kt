package slatekit.apis.codegen

import slatekit.common.Result
import slatekit.common.newline
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType


class CodeGenKotlin(settings:CodeGenSettings) : CodeGenBase(settings) {

    override val basicTypes = listOf(
            // Basic types
            Pair(KTypes.KStringType        , TypeInfo(true, false, "String"  , "String"  , KTypes.KStringClass       , KTypes.KStringClass       , "String"  + ".class")),
            Pair(KTypes.KBoolType          , TypeInfo(true, false, "Boolean" , "Boolean" , KTypes.KBoolClass         , KTypes.KBoolClass         , "Boolean" + ".class")),
            Pair(KTypes.KShortType         , TypeInfo(true, false, "short"   , "Short"   , KTypes.KShortClass        , KTypes.KShortClass        , "Short"   + ".class")),
            Pair(KTypes.KIntType           , TypeInfo(true, false, "int"     , "Integer" , KTypes.KIntClass          , KTypes.KIntClass          , "Integer" + ".class")),
            Pair(KTypes.KLongType          , TypeInfo(true, false, "long"    , "Long"    , KTypes.KLongClass         , KTypes.KLongClass         , "Long"    + ".class")),
            Pair(KTypes.KFloatType         , TypeInfo(true, false, "float"   , "Float"   , KTypes.KFloatClass        , KTypes.KFloatClass        , "Float"   + ".class")),
            Pair(KTypes.KDoubleType        , TypeInfo(true, false, "double"  , "Double"  , KTypes.KDoubleClass       , KTypes.KDoubleClass       , "Double"  + ".class")),
            Pair(KTypes.KDateTimeType      , TypeInfo(true, false, "Date"    , "Date"    , KTypes.KDateTimeClass     , KTypes.KDateTimeClass     , "Date"    + ".class")),
            Pair(KTypes.KLocalDateType     , TypeInfo(true, false, "Date"    , "Date"    , KTypes.KLocalDateClass    , KTypes.KLocalDateClass    , "Date"    + ".class")),
            Pair(KTypes.KLocalTimeType     , TypeInfo(true, false, "Date"    , "Date"    , KTypes.KLocalTimeClass    , KTypes.KLocalTimeClass    , "Date"    + ".class")),
            Pair(KTypes.KLocalDateTimeType , TypeInfo(true, false, "Date"    , "Date"    , KTypes.KLocalDateTimeClass, KTypes.KLocalDateTimeClass, "Date"    + ".class")),
            Pair(KTypes.KZonedDateTimeType , TypeInfo(true, false, "Date"    , "Date"    , KTypes.KZonedDateTimeClass, KTypes.KZonedDateTimeClass, "Date"    + ".class")),
            Pair(KTypes.KDocType           , TypeInfo(true, false, "String"  , "String"  , KTypes.KDocClass          , KTypes.KDocClass          , "String"  + ".class")),
            Pair(KTypes.KVarsType          , TypeInfo(true, false, "String"  , "String"  , KTypes.KVarsClass         , KTypes.KVarsClass         , "String"  + ".class")),
            Pair(KTypes.KSmartStringType   , TypeInfo(true, false, "String"  , "String"  , KTypes.KSmartStringClass  , KTypes.KSmartStringClass  , "String"  + ".class")),
            Pair(KTypes.KContentType       , TypeInfo(true, false, "String"  , "String"  , KTypes.KContentClass      , KTypes.KContentClass      , "String"  + ".class")),
            Pair(KTypes.KDecStringType     , TypeInfo(true, false, "String"  , "String"  , KTypes.KDecStringClass    , KTypes.KDecStringClass    , "String"  + ".class")),
            Pair(KTypes.KDecIntType        , TypeInfo(true, false, "String"  , "String"  , KTypes.KDecIntClass       , KTypes.KDecIntClass       , "String"  + ".class")),
            Pair(KTypes.KDecLongType       , TypeInfo(true, false, "String"  , "String"  , KTypes.KDecLongClass      , KTypes.KDecLongClass      , "String"  + ".class")),
            Pair(KTypes.KDecDoubleType     , TypeInfo(true, false, "String"  , "String"  , KTypes.KDecDoubleClass    , KTypes.KDecDoubleClass    , "String"  + ".class")),
            Pair(KTypes.KAnyType           , TypeInfo(false,false, "Object"  , "Object"  , KTypes.KAnyClass          , KTypes.KAnyClass          , "Object"  + ".class"))

    )



    override fun buildModelInfo(cls:KClass<*>): String {
        val props = Reflector.getProperties(cls)
        val fields = props.foldIndexed( "", { ndx:Int, acc:String, prop: KProperty<*> ->
            val type = prop.returnType
            val typeInfo = buildTypeName(type)
            val suffix = if(ndx > 0) "," else ""
            val field = "val " + prop.name + " : " + typeInfo.targetParameterType + suffix + newline
            acc + (if (ndx > 0) "\t" else "") + field
        })
        return fields
    }

    /**
     * builds the name of the datatype for the target(Java) language.
     */
    override fun buildTypeName(tpe: KType): TypeInfo {
        return when (tpe) {
            else                      -> {
                val cls = tpe.classifier as KClass<*>
                if(cls == Result::class) {
                    val genType = tpe.arguments[0].type!!
                    val finalType = buildTypeName(genType)
                    finalType
                }
                else if (cls.supertypes.contains(KTypes.KSmartStringType)){
                    TypeInfo(true, false, "String"  , "String"  , KTypes.KSmartStringClass  , KTypes.KSmartStringClass, "String.class")
                }
                else if(cls == List::class){
                    val listType = tpe.arguments[0].type!!
                    val listCls = KTypes.getClassFromType(listType)
                    val listTypeInfo = buildTypeName(listType)
                    val typeSig = "List<" + listTypeInfo.targetReturnType + ">"
                    TypeInfo(false, true, typeSig, typeSig, List::class, listCls, listTypeInfo.conversionType)
                }
                else if(cls == Map::class){
                    val tpeKey = tpe.arguments[0].type!!
                    val tpeVal = tpe.arguments[1].type!!
                    //val clsKey = KTypes.getClassFromType(tpeKey)
                    val clsVal = KTypes.getClassFromType(tpeVal)
                    val keyTypeInfo = buildTypeName(tpeKey)
                    val valTypeInfo = buildTypeName(tpeVal)
                    val sig = "Map<" + keyTypeInfo.targetReturnType + "," + valTypeInfo.targetReturnType + ">"
                    TypeInfo(false, true, sig, sig, Map::class, clsVal, "${keyTypeInfo.conversionType},${valTypeInfo.conversionType}")
                }
                else if(cls == Pair::class) {
                    val tpeFirst = tpe.arguments[0].type!!
                    val tpeSecond = tpe.arguments[1].type!!
                    val firstTypeInfo = buildTypeName(tpeFirst)
                    val secondTypeInfo = buildTypeName(tpeSecond)
                    val sig = "Pair<" + firstTypeInfo.targetReturnType + "," + secondTypeInfo.targetReturnType + ">"
                    TypeInfo(false, false, sig, sig, cls, cls, "${firstTypeInfo.conversionType},${secondTypeInfo.conversionType}")
                }
                else {
                    val sig = cls.simpleName ?: ""
                    TypeInfo(false, false, sig, sig, cls, cls, sig + ".class")
                }
            }
        }
    }
}
