package test.entities

import org.junit.Assert
import org.junit.Test
import kiit.common.data.DataAction
import kiit.common.data.DataType
import kiit.data.core.LongId
import kiit.data.core.Meta
import kiit.data.core.Table
import kiit.entities.Schema
import kiit.entities.mapper.EntityEncoder
import kiit.entities.mapper.EntitySettings
import kiit.meta.models.Model
import kiit.meta.models.ModelUtils


class Data_02_Mappers_Encode {
    val expected = listOf(
            "`test_string`"         to "'abc'",
            "`test_string_enc`"     to "'u+DVLRDgvTFnnLRg2mTd2w'",
            "`test_bool`"           to "0",
            "`test_short`"          to "1",
            "`test_int`"            to "2",
            "`test_long`"           to "3",
            "`test_float`"          to "4.5",
            "`test_double`"         to "5.5",
            "`test_enum`"           to "1",
            "`test_localdate`"      to "'2021-01-20'",
            "`test_localtime`"      to "'13:30:45'",
            "`test_localdatetime`"  to "'2021-01-20 13:30:45'",
            "`test_zoneddatetime`"  to "'2021-01-20 13:30:45'",
            "`test_uuid`"           to "'497dea41-8658-4bb7-902c-361014799214'",
            "`test_uniqueId`"       to "'usa:314fef51-43a7-496c-be24-520e73758836'",
            "`test_object_addr`"    to "'street 1'",
            "`test_object_city`"    to "'city 1'",
            "`test_object_state`"   to "'state 1'",
            "`test_object_country`" to "1",
            "`test_object_zip`"     to "'12345'",
            "`test_object_isPOBox`" to "1"
    )


    @Test
    fun can_encode_model_immutable() {
        val model = Schema.load(SampleEntityImmutable::class)
        val mapper = EntityEncoder<Long, SampleEntityImmutable>(model, EntitySetup.meta, settings = EntitySettings(false), encryptor = EntitySetup.enc)
        val sample = EntitySetup.sampleImmutable()
        val values = mapper.encode(sample, DataAction.Create, EntitySetup.enc)
        values.forEachIndexed { ndx, v ->
            println(v.name + " = " + v.value)
            Assert.assertEquals(expected[ndx].first, v.name)
            Assert.assertEquals(expected[ndx].second, v.text)
        }
    }


    @Test
    fun can_encode_model_immutable_same_values() {
        val model = Schema.load(SampleEdgeCases::class)
        val meta = Meta<Long, SampleEdgeCases>(LongId { m -> m.id }, Table("sample1"))
        val mapper = EntityEncoder<Long, SampleEdgeCases>(model, meta, settings = EntitySettings(false), encryptor = EntitySetup.enc)
        val sample = SampleEdgeCases(1, "root")
        val values = mapper.encode(sample, DataAction.Create, EntitySetup.enc)
        Assert.assertEquals(4, values.size)
        Assert.assertEquals("`name`", values[0].name)
        Assert.assertEquals("root", values[0].value)
        Assert.assertEquals("`item_name`", values[2].name)
        Assert.assertEquals("sub1", values[2].value)

    }


    @Test
    fun can_encode_model_mutable() {
        val model = Schema.load(SampleEntityMutable::class)
        val meta = Meta<Long, SampleEntityMutable>(LongId { m -> m.id }, Table("sample1"))
        val mapper = EntityEncoder<Long, SampleEntityMutable>(model, meta, settings = EntitySettings(false), encryptor = EntitySetup.enc)
        val sample = EntitySetup.sampleMutable()
        val values = mapper.encode(sample, DataAction.Create, EntitySetup.enc)
        values.forEachIndexed { ndx, v ->
            val expectedVal = expected.first { it.first == v.name }
            println(v.name + " = " + v.value)
            Assert.assertEquals(expectedVal.first, v.name)
            Assert.assertEquals(expectedVal.second, v.text)
        }
    }

    @Test
    fun can_handle_enum() {
        val prop = SampleEntityImmutable::test_enum
        val type = ModelUtils.fieldType(prop)
        Assert.assertEquals(DataType.DTEnum, type)
    }
}