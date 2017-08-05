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
package test

import org.junit.Test
import slatekit.common.Random
import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.ListMap
import slatekit.common.Mapper
import slatekit.common.records.RecordMap
import slatekit.entities.core.EntityMapper
import slatekit.meta.models.Model
import slatekit.meta.models.ModelMapper
import kotlin.reflect.KClass

/**
 * Created by kishorereddy on 6/4/17.
 */

class MapperTests {

    fun loadSchemaViaReflection(): Model {
        val model = ModelMapper.loadSchema(AuthorR::class)
        return model
    }


    fun loadSchemaSpecification(): Model {
        val model = Model(AuthorR::class)
                    .addId( AuthorR::id, true)
                    .add( AuthorR::createdAt )
                    .add( AuthorR::createdBy )
                    .add( AuthorR::updatedAt )
                    .add( AuthorR::updatedBy )
                    .add( AuthorR::uniqueId  )
                    .add( AuthorR::email     )
                    .add( AuthorR::isActive  )
                    .add( AuthorR::age       )
                    .add( AuthorR::salary    )
        return model
    }


    fun ensureField(model:Model, name:String, required:Boolean, tpe: KClass<*>):Unit {
        val field = model.fields.find { it.name == name }
        assert(field != null)
        assert(field!!.isRequired == required)
        assert(field!!.dataType == tpe)
    }


    fun ensureAuthorModel(model:Model):Unit {

        assert(model.hasId)
        assert(model.any)
        ensureField(model, "id"        , true, Long::class     )
        ensureField(model, "createdAt" , true, DateTime::class )
        ensureField(model, "createdBy" , true, Long::class     )
        ensureField(model, "updatedAt" , true, DateTime::class )
        ensureField(model, "updatedBy" , true, Long::class     )
        ensureField(model, "uniqueId"  , true, String::class   )
        ensureField(model, "email"     , true, String::class   )
        ensureField(model, "isActive"  , true, Boolean::class  )
        ensureField(model, "age"       , true, Int::class      )
        ensureField(model, "salary"    , true, Double::class   )
    }


    @Test fun can_build_from_reflection(){
        val model = loadSchemaViaReflection()
        ensureAuthorModel(model)
    }


    @Test fun can_build_from_schema(){
        val model = loadSchemaSpecification()
        ensureAuthorModel(model)
    }


    @Test fun can_map_to_case_class(){
        val mod = AuthorR(email = "kishore@abc.com")
        val model = loadSchemaViaReflection()

        val mapper = EntityMapper(model)
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
        Pair("salary", 400.5)
        ))

        val source = RecordMap(data)
        val entity = mapper.mapFrom(source)!!as AuthorR

        assert( entity.id == 1L )
        assert( entity.uniqueId == "ABC" )
        assert( entity.email == "kishore@abc.com" )
        assert( entity.isActive )
        assert( entity.age == 35 )
        assert( entity.salary == 400.5 )
    }


    data class AuthorR(
            val id: Long             = 0,

            @property:Field(required = true)
            val createdAt: DateTime = DateTime.now(),

            @property:Field(required = true)
            val createdBy: Long = 0,

            @property:Field(required = true)
            val updatedAt: DateTime = DateTime.now(),

            @property:Field(required = true)
            val updatedBy: Long = 0,

            @property:Field(required = true)
            val uniqueId: String            = Random.stringGuid(),

            @property:Field(required = true)
            val email:String = "",

            @property:Field(required = true)
            val isActive:Boolean = false,

            @property:Field(required = true)
            val age:Int = 35,

            @property:Field(required = true)
            val salary:Double = 20.5
    )
}