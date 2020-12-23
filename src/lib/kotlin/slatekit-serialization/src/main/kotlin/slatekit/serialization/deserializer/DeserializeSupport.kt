package slatekit.serialization.deserializer


interface DeserializeSupport {

    fun handle(raw:Any?, nullValue:Any?, elseValue:() -> Any?):Any? {
        return when (raw) {
            null   -> nullValue
            else   -> elseValue()
        }
    }
}