package test.entities

import org.junit.Assert
import org.junit.Test
import slatekit.data.syntax.Types
import slatekit.common.data.BuildMode
import slatekit.data.sql.Insert
import slatekit.data.sql.Update
import slatekit.data.sql.vendors.MySqlDialect
import slatekit.data.sql.vendors.MySqlProvider
import slatekit.entities.Schema
import slatekit.entities.mapper.EntityMapper
import slatekit.entities.mapper.EntitySettings
import slatekit.meta.models.Model
import slatekit.migrations.SqlBuilder
import slatekit.migrations.SqlBuilderDDL
import test.setup.Group
import test.setup.Member
import test.setup.User5

class Data_03_Statement_Syntax {

    @Test
    fun can_build_insert() {
        val model = Schema.load(SampleEntityImmutable::class)
        val mapper = EntityMapper<Long, SampleEntityImmutable>(model, EntitySetup.meta, Long::class, SampleEntityImmutable::class, EntitySettings(false))
        val stmt = Insert<Long, SampleEntityImmutable>(MySqlDialect, EntitySetup.meta, mapper)
        val sample = EntitySetup.sampleImmutable()
        val actual = stmt.build(sample, BuildMode.Sql).sql
        val expected = """insert into `sample1` (`test_string`,`test_string_enc`,`test_bool`,`test_short`,`test_int`,`test_long`,`test_float`,`test_double`,`test_enum`,`test_localdate`,`test_localtime`,`test_localdatetime`,`test_zoneddatetime`,`test_uuid`,`test_uniqueId`,`test_object_addr`,`test_object_city`,`test_object_state`,`test_object_country`,`test_object_zip`,`test_object_isPOBox`)  VALUES ('abc','abc123',0,1,2,3,4.5,5.5,1,'2021-01-20','13:30:45','2021-01-20 13:30:45','2021-01-20 13:30:45','497dea41-8658-4bb7-902c-361014799214','usa:314fef51-43a7-496c-be24-520e73758836','street 1','city 1','state 1',1,'12345',1);"""
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun can_build_update() {
        val model = Schema.load(SampleEntityImmutable::class)
        val mapper = EntityMapper<Long, SampleEntityImmutable>(model, EntitySetup.meta, Long::class, SampleEntityImmutable::class, EntitySettings(false))
        val stmt = Update<Long, SampleEntityImmutable>(MySqlDialect, EntitySetup.meta, mapper)
        val sample = EntitySetup.sampleImmutable()
        val actual = stmt.build(sample, BuildMode.Sql).sql
        val expected = """update `sample1` SET `test_string` = 'abc',`test_string_enc` = 'abc123',`test_bool` = 0,`test_short` = 1,`test_int` = 2,`test_long` = 3,`test_float` = 4.5,`test_double` = 5.5,`test_enum` = 1,`test_localdate` = '2021-01-20',`test_localtime` = '13:30:45',`test_localdatetime` = '2021-01-20 13:30:45',`test_zoneddatetime` = '2021-01-20 13:30:45',`test_uuid` = '497dea41-8658-4bb7-902c-361014799214',`test_uniqueId` = 'usa:314fef51-43a7-496c-be24-520e73758836',`test_object_addr` = 'street 1',`test_object_city` = 'city 1',`test_object_state` = 'state 1',`test_object_country` = 1,`test_object_zip` = '12345',`test_object_isPOBox` = 1 WHERE `id` = 0;"""
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun can_build_ddl_1() {
        val model = Schema.load(SampleEntityImmutable::class, table = "sample1")
        val builder = SqlBuilderDDL(MySqlDialect, null)
        val actual = builder.create(model)
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
`test_uniqueId` NVARCHAR(50) NOT NULL,  
`test_object_addr` NVARCHAR(40) NOT NULL,  
`test_object_city` NVARCHAR(30) NOT NULL,  
`test_object_state` NVARCHAR(20) NOT NULL,  
`test_object_country` INTEGER NOT NULL,  
`test_object_zip` NVARCHAR(5) NOT NULL,  
`test_object_isPOBox` BIT NOT NULL );"""
        Assert.assertEquals(expected, actual)
    }
}