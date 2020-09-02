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
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DbCon
import slatekit.common.utils.RecordMap
import slatekit.common.ids.UniqueId
import slatekit.common.utils.B64Java8
import slatekit.common.utils.ListMap
import slatekit.db.Db
import slatekit.meta.models.ModelMapper
import slatekit.orm.OrmMapper
import slatekit.orm.databases.vendors.MySqlConverter
import test.setup.*
import java.sql.Timestamp
import java.util.*

/**
 * Created by kishorereddy on 6/4/17.
 */

class Entity_Mapper_ResultSet_Tests {


    @Test fun can_map_to_immutable_class(){
        val model = ModelMapper.loadSchema(AuthorR::class, AuthorR::id.name)

        val mapper = OrmMapper(model, Db(DbCon.empty), MySqlConverter<Long, AuthorR>(), Long::class, AuthorR::class)
        val data = buildSampleDataForAuthor()
        val source = RecordMap(data)
        val entity = mapper.decode(source, null)!!

        Assert.assertTrue( entity.id == 1L )
        Assert.assertTrue( entity.uuid == "ABC" )
        Assert.assertTrue( entity.email == "kishore@abc.com" )
        Assert.assertTrue( entity.isActive )
        Assert.assertTrue( entity.age == 35 )
        Assert.assertTrue( entity.salary == 400.5 )
        Assert.assertTrue( entity.status == StatusEnum.Active )
        Assert.assertTrue( entity.uid == UUID.fromString(sampleUUID1) )
        Assert.assertTrue( entity.shardId == UniqueId.parse(sampleUUID2) )
    }


    @Test fun can_map_to_writable_class(){
        val model = ModelMapper.loadSchema(AuthorW::class, AuthorW::id.name)

        val mapper = OrmMapper(model, Db(DbCon.empty), MySqlConverter<Long, AuthorW>(), Long::class, AuthorW::class)
        val data = buildSampleDataForAuthor()
        val source = RecordMap(data)
        val entity = mapper.decode(source, null)!!

        Assert.assertTrue( entity.id == 1L )
        Assert.assertTrue( entity.uuid == "ABC" )
        Assert.assertTrue( entity.email == "kishore@abc.com" )
        Assert.assertTrue( entity.isActive )
        Assert.assertTrue( entity.age == 35 )
        Assert.assertTrue( entity.status == StatusEnum.Active )
        Assert.assertTrue( entity.salary == 400.5 )
        Assert.assertTrue( entity.uid == UUID.fromString(sampleUUID1) )
        Assert.assertTrue( entity.shardId == UniqueId.parse(sampleUUID2) )
    }


    @Test fun can_map_to_immutable_class_with_embedded_object(){
        val model = ModelMapper.loadSchema(UserWithAddress::class, UserWithAddress::id.name)

        val mapper = OrmMapper(model, Db(DbCon.empty), MySqlConverter<Long, UserWithAddress>(), Long::class, UserWithAddress::class)
        val data = buildSampleDataForEmbeddedObject()
        val source = RecordMap(data)
        val entity = mapper.decode(source, null)!!

        Assert.assertTrue( entity.id == 1L )
        Assert.assertTrue( entity.email == "kishore@abc.com" )
        Assert.assertTrue( entity.isActive )
        Assert.assertTrue( entity.age == 35 )
        Assert.assertTrue( entity.salary == 400.5 )
        Assert.assertTrue( entity.addr == Address("street 1", "city 1", "state 1", 1,"12345", true))
        Assert.assertTrue( entity.uid == UUID.fromString(sampleUUID1) )
        Assert.assertTrue( entity.shardId == UniqueId.parse(sampleUUID2) )
    }


    @Test fun can_map_with_custom_mapper_auto_with_encryption(){
        val model = ModelMapper.loadSchema(AuthorEnc::class, AuthorEnc::id.name)


        val encA = Encryptor("aejklhviuxywehjk", "3214a99lkdf03292", B64Java8)
        val encB = Encryptor("bejklhviuxywehjk", "3214b99lkdf03292", B64Java8)

        val mapper = CustomMapper1<AuthorEnc>(model, Db(DbCon.empty), AuthorEnc::class, AuthorEnc::encmode.name, mapOf("a" to encA, "b" to encB))
        val data = buildSampleDataForAuthor(encA)
        val source = RecordMap(data)
        val entity = mapper.decode(source, null)!!

        Assert.assertTrue( entity.id == 1L )
        Assert.assertTrue( entity.uuid == "ABC" )
        Assert.assertTrue( entity.email == "kishore@abc.com" )
        Assert.assertTrue( entity.isActive )
        Assert.assertTrue( entity.age == 35 )
        Assert.assertTrue( entity.salary == 400.5 )
        Assert.assertTrue( entity.status == StatusEnum.Active )
        Assert.assertTrue( entity.uid == UUID.fromString(sampleUUID1) )
        Assert.assertTrue( entity.shardId == UniqueId.parse(sampleUUID2) )
    }




    @Test fun can_map_with_custom_mapper_manual_with_encryption(){
        val model = ModelMapper.loadSchema(AuthorEnc::class, AuthorEnc::id.name)


        val encA = Encryptor("aejklhviuxywehjk", "3214a99lkdf03292", B64Java8)
        val encB = Encryptor("bejklhviuxywehjk", "3214b99lkdf03292", B64Java8)

        val mapper = CustomMapper2(model, Db(DbCon.empty), AuthorEnc::class, AuthorEnc::encmode.name, mapOf("a" to encA, "b" to encB))
        val data = buildSampleDataForAuthor(encA)
        val source = RecordMap(data)
        val entity = mapper.decode(source, null)!!

        Assert.assertTrue( entity.id == 1L )
        Assert.assertTrue( entity.uuid == "ABC" )
        Assert.assertTrue( entity.email == "kishore@abc.com" )
        Assert.assertTrue( entity.isActive )
        Assert.assertTrue( entity.age == 35 )
        Assert.assertTrue( entity.salary == 400.5 )
        Assert.assertTrue( entity.status == StatusEnum.Active )
        Assert.assertTrue( entity.uid == UUID.fromString(sampleUUID1) )
        Assert.assertTrue( entity.shardId == UniqueId.parse(sampleUUID2) )
    }


    fun buildSampleDataForAuthor(enc:Encryptor? = null): ListMap<String, Any> {
        val email = when(enc){
            null -> "kishore@abc.com"
            else -> enc.encrypt("kishore@abc.com")
        }
        val data = ListMap(
            listOf(
                Pair("id", 1L),
                Pair("uuid", "ABC"),
                Pair("createdAt", Timestamp(2017, 1, 1, 12, 0, 0, 0)),
                Pair("createdBy", 100L),
                Pair("updatedAt", Timestamp(2017, 1, 2, 12, 0, 0, 0)),
                Pair("updatedBy", 101L),
                Pair("email", email),
                Pair("isActive", true),
                Pair("age", 35),
                Pair("status", StatusEnum.Active.value),
                Pair("salary", 400.5),
                Pair("uid", UUID.fromString(sampleUUID1)),
                Pair("shardId", UniqueId.parse(sampleUUID2)),
                Pair("encmode", "a")
            )
        )
        return data
    }


    fun buildSampleDataForEmbeddedObject(): ListMap<String, Any> {

        val data = ListMap(
            listOf(
                Pair("id", 1L),
                Pair("email", "kishore@abc.com"),
                Pair("isActive", true),
                Pair("age", 35),
                Pair("salary", 400.5),
                Pair("addr_addr", "street 1"),
                Pair("addr_city", "city 1"),
                Pair("addr_state", "state 1"),
                Pair("addr_country", 1),
                Pair("addr_zip", "12345"),
                Pair("addr_isPOBox", true),
                Pair("uid", UUID.fromString(sampleUUID1)),
                Pair("shardId", UniqueId.parse(sampleUUID2))
            )
        )
        return data
    }



    companion object {
        val sampleUUID1 = "67bdb72a-1d74-11e8-b467-0ed5f89f718b"
        val sampleUUID2 = "67bdb72a-1d74-11e8-b467-0ed5f89f718c"
    }
}
