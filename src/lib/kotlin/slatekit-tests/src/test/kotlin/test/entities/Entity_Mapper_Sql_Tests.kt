/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.entities

import org.junit.Assert
import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.UniqueId
import slatekit.common.toUUId
import slatekit.entities.core.EntityMapper
import slatekit.entities.databases.statements.Insert
import slatekit.entities.databases.statements.Update
import slatekit.entities.databases.vendors.MySqlConverter
import slatekit.meta.models.ModelMapper
import test.setup.*

/**
 * Created by kishorereddy on 6/4/17.
 */

class Entity_Mapper_Sql_Tests {

    val sampleDate = DateTime.of(2018, 11, 1, 8, 30, 0)
    val sampleUser = AuthorR(0, sampleUUID1, sampleDate, 0, sampleDate, 0, "k@abc.com",
            true, 35, StatusEnum.Active, 123.45, sampleUUID1.toUUId(), UniqueId.fromString("us:" + sampleUUID1) )


    @Test fun can_map_sql_insert(){
        val model = ModelMapper.loadSchema(AuthorR::class, AuthorR::id.name)
        val mapper = EntityMapper(model, MySqlConverter)
        val actual = Insert().sql(sampleUser, model, mapper)
        val exepected = """insert into `AuthorR` (`uuid`,`createdAt`,`createdBy`,`updatedAt`,`updatedBy`,`email`,`isActive`,`age`,`status`,`salary`,`uid`,`shardId`)  VALUES ('67bdb72a-1d74-11e8-b467-0ed5f89f718b','2018-11-01 08:30:00',0,'2018-11-01 08:30:00',0,'k@abc.com',1,35,1,123.45,'67bdb72a-1d74-11e8-b467-0ed5f89f718b','us:67bdb72a-1d74-11e8-b467-0ed5f89f718b');"""
        Assert.assertEquals(exepected, actual)
    }


    @Test fun can_map_sql_update(){
        val model = ModelMapper.loadSchema(AuthorR::class, AuthorR::id.name)
        val mapper = EntityMapper(model, MySqlConverter)
        val actual = Update().sql(sampleUser.copy(id = 2), model, mapper)
        val exepected = """update `AuthorR` set  `uuid`='67bdb72a-1d74-11e8-b467-0ed5f89f718b',`createdAt`='2018-11-01 08:30:00',`createdBy`=0,`updatedAt`='2018-11-01 08:30:00',`updatedBy`=0,`email`='k@abc.com',`isActive`=1,`age`=35,`status`=1,`salary`=123.45,`uid`='67bdb72a-1d74-11e8-b467-0ed5f89f718b',`shardId`='us:67bdb72a-1d74-11e8-b467-0ed5f89f718b' where id = 2;"""
        Assert.assertEquals(exepected, actual)
    }



    companion object {
        val sampleUUID1 = "67bdb72a-1d74-11e8-b467-0ed5f89f718b"
        val sampleUUID2 = "67bdb72a-1d74-11e8-b467-0ed5f89f718c"
    }
}
