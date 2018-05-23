package test

import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.UniqueId
import slatekit.meta.models.Model
import slatekit.meta.models.ModelMapper
import test.setup.Address
import test.setup.AuthorR
import test.setup.UserWithAddress
import java.util.*
import kotlin.reflect.KClass


class ModelTests {


    @Test fun can_build_simple_model_from_reflection(){
        val model = ModelMapper.loadSchema(AuthorR::class, AuthorR::id.name)
        ensureAuthorModel(model)
    }


    @Test fun can_build_simple_model_from_schema(){
        val model = loadSchemaSpecification()
        ensureAuthorModel(model)
    }


    @Test fun can_build_complex_model_from_schema(){
        val model = ModelMapper.loadSchema(UserWithAddress::class, UserWithAddress::id.name)
        val addrProp = model.fields.find { it.name == "addr" }
        assert( addrProp != null)
        assert( addrProp!!.model != null)
        assert( addrProp!!.model?.dataType == Address::class)
    }


    fun ensureField(model: Model, name:String, required:Boolean, tpe: KClass<*>):Unit {
        val field = model.fields.find { it.name == name }
        assert(field != null)
        assert(field!!.isRequired == required)
        assert(field!!.dataType == tpe)
    }


    fun ensureAuthorModel(model: Model):Unit {

        assert(model.hasId)
        assert(model.any)
        ensureField(model, "id"        , true, Long::class     )
        ensureField(model, "createdAt" , true, DateTime::class )
        ensureField(model, "createdBy" , true, Long::class     )
        ensureField(model, "updatedAt" , true, DateTime::class )
        ensureField(model, "updatedBy" , true, Long::class     )
        ensureField(model, "uuid"      , true, String::class   )
        ensureField(model, "email"     , true, String::class   )
        ensureField(model, "isActive"  , true, Boolean::class  )
        ensureField(model, "age"       , true, Int::class      )
        ensureField(model, "salary"    , true, Double::class   )
        ensureField(model, "uid"       , true, UUID::class     )
        ensureField(model, "shardId"   , true, UniqueId::class )
    }


    fun loadSchemaSpecification(): Model {
        val model = Model(AuthorR::class)
                .addId( AuthorR::id, true)
                .add( AuthorR::createdAt )
                .add( AuthorR::createdBy )
                .add( AuthorR::updatedAt )
                .add( AuthorR::updatedBy )
                .add( AuthorR::uuid  )
                .add( AuthorR::email     )
                .add( AuthorR::isActive  )
                .add( AuthorR::age       )
                .add( AuthorR::salary    )
                .add( AuthorR::uid       )
                .add( AuthorR::shardId   )
        return model
    }
}