package test.entities

import org.junit.Assert
import org.junit.Test
import slatekit.data.syntax.Delete
import slatekit.data.syntax.Insert
import slatekit.data.syntax.Update
import slatekit.entities.mapper.EntityMapper
import slatekit.entities.mapper.EntitySettings
import slatekit.meta.models.ModelMapper

class Data_03_Syntax {

    @Test
    fun can_build_insert() {
        val model = ModelMapper.loadSchema(SampleEntityImmutable::class)
        val mapper = EntityMapper<Long, SampleEntityImmutable>(model, EntityFixtures.meta, Long::class, SampleEntityImmutable::class, EntitySettings(false))
        val stmt = Insert<Long, SampleEntityImmutable>(EntityFixtures.meta, mapper)
        val sample = EntityFixtures.sampleImmutable()
        val actual = stmt.stmt(sample)
        val expected = """insert into `sample1` (`id`,`test_string`,`test_string_enc`,`test_bool`,`test_short`,`test_int`,`test_long`,`test_float`,`test_double`,`test_enum`,`test_localdate`,`test_localtime`,`test_localdatetime`,`test_zoneddatetime`,`test_uuid`,`test_uniqueId`,`test_object_addr`,`test_object_city`,`test_object_state`,`test_object_country`,`test_object_zip`,`test_object_isPOBox`)  VALUES (0,'abc','abc123',0,1,2,3,4.5,5.5,1,'2021-01-20','13:30:45','2021-01-20 13:30:45','2021-01-20 13:30:45','497dea41-8658-4bb7-902c-361014799214','usa:314fef51-43a7-496c-be24-520e73758836','street 1','city 1','state 1',1,'12345',1);"""
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun can_build_update() {
        val model = ModelMapper.loadSchema(SampleEntityImmutable::class)
        val mapper = EntityMapper<Long, SampleEntityImmutable>(model, EntityFixtures.meta, Long::class, SampleEntityImmutable::class, EntitySettings(false))
        val stmt = Update<Long, SampleEntityImmutable>(EntityFixtures.meta, mapper)
        val sample = EntityFixtures.sampleImmutable()
        val actual = stmt.stmt(sample)
        val expected = """update `sample1` SET `test_string` = `test_string`='abc',`test_string_enc` = `test_string_enc`='abc123',`test_bool` = `test_bool`=0,`test_short` = `test_short`=1,`test_int` = `test_int`=2,`test_long` = `test_long`=3,`test_float` = `test_float`=4.5,`test_double` = `test_double`=5.5,`test_enum` = `test_enum`=1,`test_localdate` = `test_localdate`='2021-01-20',`test_localtime` = `test_localtime`='13:30:45',`test_localdatetime` = `test_localdatetime`='2021-01-20 13:30:45',`test_zoneddatetime` = `test_zoneddatetime`='2021-01-20 13:30:45',`test_uuid` = `test_uuid`='497dea41-8658-4bb7-902c-361014799214',`test_uniqueId` = `test_uniqueId`='usa:314fef51-43a7-496c-be24-520e73758836',`test_object_addr` = `test_object_addr`='street 1',`test_object_city` = `test_object_city`='city 1',`test_object_state` = `test_object_state`='state 1',`test_object_country` = `test_object_country`=1,`test_object_zip` = `test_object_zip`='12345',`test_object_isPOBox` = `test_object_isPOBox`=1;"""
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun can_build_delete() {
        val model = ModelMapper.loadSchema(SampleEntityImmutable::class)
        val mapper = EntityMapper<Long, SampleEntityImmutable>(model, EntityFixtures.meta, Long::class, SampleEntityImmutable::class, EntitySettings(false))
        val stmt = Delete<Long, SampleEntityImmutable>(EntityFixtures.meta, mapper)
        val actual = stmt.stmt(2)
        val expected = """delete from `sample1` where `id` = 2;"""
        Assert.assertEquals(expected, actual)
    }
}