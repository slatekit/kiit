package test.entities

import org.junit.Assert
import org.junit.Test
import slatekit.data.core.Types
import slatekit.data.syntax.Delete
import slatekit.data.syntax.Insert
import slatekit.data.syntax.Update
import slatekit.entities.mapper.EntityMapper
import slatekit.entities.mapper.EntitySettings
import slatekit.meta.models.Model
import slatekit.migrations.SqlBuilder
import test.setup.Group
import test.setup.Member
import test.setup.User5

class Data_03_Statement_Syntax {

    @Test
    fun can_build_insert() {
        val model = Model.loadSchema(SampleEntityImmutable::class)
        val mapper = EntityMapper<Long, SampleEntityImmutable>(model, EntitySetup.meta, Long::class, SampleEntityImmutable::class, EntitySettings(false))
        val stmt = Insert<Long, SampleEntityImmutable>(EntitySetup.meta, mapper)
        val sample = EntitySetup.sampleImmutable()
        val actual = stmt.stmt(sample)
        val expected = """insert into `sample1` (`test_string`,`test_string_enc`,`test_bool`,`test_short`,`test_int`,`test_long`,`test_float`,`test_double`,`test_enum`,`test_localdate`,`test_localtime`,`test_localdatetime`,`test_zoneddatetime`,`test_uuid`,`test_uniqueId`,`test_object_addr`,`test_object_city`,`test_object_state`,`test_object_country`,`test_object_zip`,`test_object_isPOBox`)  VALUES ('abc','abc123',0,1,2,3,4.5,5.5,1,'2021-01-20','13:30:45','2021-01-20 13:30:45','2021-01-20 13:30:45','497dea41-8658-4bb7-902c-361014799214','usa:314fef51-43a7-496c-be24-520e73758836','street 1','city 1','state 1',1,'12345',1);"""
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun can_build_update() {
        val model = Model.loadSchema(SampleEntityImmutable::class)
        val mapper = EntityMapper<Long, SampleEntityImmutable>(model, EntitySetup.meta, Long::class, SampleEntityImmutable::class, EntitySettings(false))
        val stmt = Update<Long, SampleEntityImmutable>(EntitySetup.meta, mapper)
        val sample = EntitySetup.sampleImmutable()
        val actual = stmt.stmt(sample)
        val expected = """update `sample1` SET `test_string` = 'abc',`test_string_enc` = 'abc123',`test_bool` = 0,`test_short` = 1,`test_int` = 2,`test_long` = 3,`test_float` = 4.5,`test_double` = 5.5,`test_enum` = 1,`test_localdate` = '2021-01-20',`test_localtime` = '13:30:45',`test_localdatetime` = '2021-01-20 13:30:45',`test_zoneddatetime` = '2021-01-20 13:30:45',`test_uuid` = '497dea41-8658-4bb7-902c-361014799214',`test_uniqueId` = 'usa:314fef51-43a7-496c-be24-520e73758836',`test_object_addr` = 'street 1',`test_object_city` = 'city 1',`test_object_state` = 'state 1',`test_object_country` = 1,`test_object_zip` = '12345',`test_object_isPOBox` = 1;"""
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun can_build_delete() {
        val model = Model.loadSchema(SampleEntityImmutable::class)
        val mapper = EntityMapper<Long, SampleEntityImmutable>(model, EntitySetup.meta, Long::class, SampleEntityImmutable::class, EntitySettings(false))
        val stmt = Delete<Long, SampleEntityImmutable>(EntitySetup.meta, mapper)
        val actual = stmt.stmt(2)
        val expected = """delete from `sample1` where `id` = 2;"""
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun can_build_ddl_1() {
        val model = Model.loadSchema(SampleEntityImmutable::class, table = "sample1")
        val builder = SqlBuilder(Types(), null)
        val actual = builder.createTable(model)
        val expected = """create table `sample1` ( 
`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,  
`test_string` NVARCHAR(30) NOT NULL,  
`test_string_enc` NVARCHAR(100) NOT NULL,  
`test_bool` BIT NOT NULL,  
`test_short` SMALLINT NOT NULL,  
`test_int` INTEGER NOT NULL,  
`test_long` BIGINT NOT NULL,  
`test_float` FLOAT NOT NULL,  
`test_double` DOUBLE NOT NULL,  
`test_enum` INTEGER NOT NULL,  
`test_localdate` DATE NOT NULL,  
`test_localtime` TIME NOT NULL,  
`test_localdatetime` DATETIME NOT NULL,  
`test_zoneddatetime` DATETIME NOT NULL,  
`test_uuid` NVARCHAR(50) NOT NULL,  
`test_uniqueid` NVARCHAR(50) NOT NULL,  
`test_object_addr` NVARCHAR(40) NOT NULL,  
`test_object_city` NVARCHAR(30) NOT NULL,  
`test_object_state` NVARCHAR(20) NOT NULL,  
`test_object_country` INTEGER NOT NULL,  
`test_object_zip` NVARCHAR(5) NOT NULL,  
`test_object_ispobox` BIT NOT NULL );"""
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun can_build_ddl_2() {
        val models = listOf(
                Model.loadSchema(User5::class ),
                Model.loadSchema(Member::class),
                Model.loadSchema(Group::class )
        )
        val builder = SqlBuilder(Types(), null)
        val ddls = models.map { builder.createTable(it) }
        println(ddls)
    }
}