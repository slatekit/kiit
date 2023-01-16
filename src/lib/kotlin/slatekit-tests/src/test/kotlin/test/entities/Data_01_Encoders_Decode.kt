package test.entities

import org.junit.Assert
import org.junit.Test
import slatekit.common.values.ListMap
import slatekit.common.values.RecordMap
import kiit.data.encoders.*
import java.util.*


class Data_01_Encoders_Decode {

    @Test
    fun can_decode_bool(){
        val tests = listOf(true to "1", false to "0", null to slatekit.data.Consts.NULL)
        ensure<Boolean, Boolean>(BoolEncoder(), tests)
    }


    @Test
    fun can_decode_string(){
        val tests = listOf("abc" to "'abc'", "   " to "'   '", null to slatekit.data.Consts.NULL)
        ensure<String, String>(StringEncoder(), tests)
    }


    @Test
    fun can_decode_short(){
        val tests = listOf(1.toShort() to "1", 0.toShort() to "0", null to slatekit.data.Consts.NULL)
        ensure<Short, Short>(ShortEncoder(), tests)
    }


    @Test
    fun can_decode_int(){
        val tests = listOf(100000000 to "100000000", 300000000 to "300000000", null to slatekit.data.Consts.NULL)
        ensure<Int, Int>(IntEncoder(), tests)
    }


    @Test
    fun can_decode_long(){
        val tests = listOf(1234567890L to "1234567890", 9876543210L to "9876543210", null to slatekit.data.Consts.NULL)
        ensure<Long, Long>(LongEncoder(), tests)
    }


    @Test
    fun can_decode_double(){
        val tests = listOf(12345.25 to "12345.25", 543210.25 to "543210.25", null to slatekit.data.Consts.NULL)
        ensure<Double, Double>(DoubleEncoder(), tests)
    }


    @Test
    fun can_decode_uuid(){
        val text = "ea3f7016-c7eb-4105-a743-41a03e7a01c4"
        val uuid = UUID.fromString(text)
        val tests = listOf(uuid to "'$text'", null to slatekit.data.Consts.NULL)
        ensure<UUID, String>(UUIDEncoder(), tests) { u -> u?.toString() }
    }


    private fun <T,A> ensure(encoder:SqlEncoder<T>, tests:List<Pair<T?, Any?>>, converter:((T?) -> A?)? = null) {
        tests.forEach {
            val input = converter?.invoke(it.first) ?: it.first
            val expect = it.first
            val map = RecordMap(ListMap(listOf(Pair("value", input))))
            val actual = encoder.decode(map, "value")
            Assert.assertEquals(expect, actual)
        }
    }
}