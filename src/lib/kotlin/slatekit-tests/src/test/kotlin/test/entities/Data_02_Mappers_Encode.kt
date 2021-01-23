package test.entities

import org.junit.Assert
import org.junit.Test
import org.threeten.bp.LocalTime
import slatekit.common.data.DataAction
import slatekit.data.core.LongId
import slatekit.data.core.Meta
import slatekit.data.core.Table
import slatekit.entities.mapper.EntityEncoder
import slatekit.entities.mapper.EntitySettings
import slatekit.meta.models.ModelMapper
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalField


class Data_02_Mappers_Encode {
    val expected = listOf(
            "`id`"                  to "0",
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
        val model = ModelMapper.loadSchema(SampleEntityImmutable::class)
        val mapper = EntityEncoder<Long, SampleEntityImmutable>(model, EntityFixtures.meta, settings = EntitySettings(false), encryptor = EntityFixtures.enc)
        val sample = EntityFixtures.sampleImmutable()
        val values = mapper.encode(sample, DataAction.Create, EntityFixtures.enc)
        values.forEachIndexed { ndx, v ->
            println(v.name + " = " + v.value)
            Assert.assertEquals(expected[ndx].first, v.name)
            Assert.assertEquals(expected[ndx].second, v.value)
        }
    }


    @Test
    fun can_encode_model_mutable() {
        val model = ModelMapper.loadSchema(SampleEntityMutable::class)
        val meta = Meta<Long, SampleEntityMutable>(LongId { m -> m.id }, Table("sample1"))
        val mapper = EntityEncoder<Long, SampleEntityMutable>(model, meta, settings = EntitySettings(false), encryptor = EntityFixtures.enc)
        val sample = EntityFixtures.sampleMutable()
        val values = mapper.encode(sample, DataAction.Create, EntityFixtures.enc)
        values.forEachIndexed { ndx, v ->
            val expectedVal = expected.first { it.first == v.name }
            println(v.name + " = " + v.value)
            Assert.assertEquals(expectedVal.first, v.name)
            Assert.assertEquals(expectedVal.second, v.value)
        }
    }
}