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

import org.junit.Test
import slatekit.common.*
import slatekit.common.records.RecordMap
import slatekit.entities.core.EntityMapper
import slatekit.meta.models.ModelMapper
import test.setup.Address
import test.setup.AuthorR
import test.setup.AuthorW
import test.setup.UserWithAddress
import java.util.*

/**
 * Created by kishorereddy on 6/4/17.
 */

class MapperTests {


    @Test fun can_map_to_immutable_class(){
        val model = ModelMapper.loadSchema(AuthorR::class, AuthorR::id.name)

        val mapper = EntityMapper(model)
        val data = buildSampleDataForAuthor()
        val source = RecordMap(data)
        val entity = mapper.mapFrom(source)!!as AuthorR

        assert( entity.id == 1L )
        assert( entity.uniqueId == "ABC" )
        assert( entity.email == "kishore@abc.com" )
        assert( entity.isActive )
        assert( entity.age == 35 )
        assert( entity.salary == 400.5 )
        assert( entity.uid == UUID.fromString(sampleUUID1) )
        assert( entity.shardId == UniqueId.fromString(sampleUUID2) )
    }


    @Test fun can_map_to_writable_class(){
        val model = ModelMapper.loadSchema(AuthorW::class, AuthorW::id.name)

        val mapper = EntityMapper(model)
        val data = buildSampleDataForAuthor()
        val source = RecordMap(data)
        val entity = mapper.mapFrom(source)!!as AuthorW

        assert( entity.id == 1L )
        assert( entity.uniqueId == "ABC" )
        assert( entity.email == "kishore@abc.com" )
        assert( entity.isActive )
        assert( entity.age == 35 )
        assert( entity.salary == 400.5 )
        assert( entity.uid == UUID.fromString(sampleUUID1) )
        assert( entity.shardId == UniqueId.fromString(sampleUUID2) )
    }


    @Test fun can_map_to_immutable_class_with_embedded_object(){
        val model = ModelMapper.loadSchema(UserWithAddress::class, UserWithAddress::id.name)

        val mapper = EntityMapper(model)
        val data = buildSampleDataForEmbeddedObject()
        val source = RecordMap(data)
        val entity = mapper.mapFrom(source)!!as UserWithAddress

        assert( entity.id == 1L )
        assert( entity.email == "kishore@abc.com" )
        assert( entity.isActive )
        assert( entity.age == 35 )
        assert( entity.salary == 400.5 )
        assert( entity.addr == Address("street 1", "city 1", "state 1", 1,"12345", true))
        assert( entity.uid == UUID.fromString(sampleUUID1) )
        assert( entity.shardId == UniqueId.fromString(sampleUUID2) )
    }


    fun buildSampleDataForAuthor(): ListMap<String,Any> {

        val data = ListMap(listOf(
                Pair("id", 1L),
                Pair("createdAt", java.sql.Timestamp(2017, 1, 1, 12,0,0,0)),
                Pair("createdBy", 100L),
                Pair("updatedAt", java.sql.Timestamp(2017, 1, 2, 12,0,0,0)),
                Pair("updatedBy", 101L),
                Pair("uniqueId", "ABC"),
                Pair("email", "kishore@abc.com"),
                Pair("isActive", true),
                Pair("age", 35),
                Pair("salary", 400.5),
                Pair("uid", UUID.fromString(sampleUUID1)),
                Pair("shardId", UniqueId.fromString(sampleUUID2))
        ))
        return data
    }


    fun buildSampleDataForEmbeddedObject(): ListMap<String,Any> {

        val data = ListMap(listOf(
                Pair("id", 1L),
                Pair("email", "kishore@abc.com"),
                Pair("isActive", true),
                Pair("age", 35),
                Pair("salary", 400.5),
                Pair("addr_addr"    , "street 1"),
                Pair("addr_city"    , "city 1"),
                Pair("addr_state"   , "state 1"),
                Pair("addr_country" , 1),
                Pair("addr_zip"     , "12345"),
                Pair("addr_isPOBox" , true),
                Pair("uid", UUID.fromString(sampleUUID1)),
                Pair("shardId", UniqueId.fromString(sampleUUID2))
        ))
        return data
    }



    companion object {
        val sampleUUID1 = "67bdb72a-1d74-11e8-b467-0ed5f89f718b"
        val sampleUUID2 = "67bdb72a-1d74-11e8-b467-0ed5f89f718c"
    }
}