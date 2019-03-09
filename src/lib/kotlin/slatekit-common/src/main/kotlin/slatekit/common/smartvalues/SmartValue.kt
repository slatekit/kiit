package slatekit.common.smartvalues

interface SmartValue {
    val required:Boolean
    val metadata:SmartMetadata
}


interface SmartStr : SmartValue {
    val min:Int
    val max:Int
}


interface SmartInt : SmartValue {
    val min:Int
    val max:Int
}


interface SmartLong : SmartValue {
    val min:Long
    val max:Long
}