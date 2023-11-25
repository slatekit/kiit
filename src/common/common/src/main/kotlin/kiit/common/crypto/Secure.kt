
package kiit.common.crypto

interface Secure {
    fun save(name:String, data:ByteArray?) : Boolean
    fun load(name:String) : ByteArray?
    fun remove(name:String) : Boolean

    fun saveText(name:String, text:String) : Boolean
    fun loadText(name:String) : String?
}