package test.meta

import org.junit.Assert
import org.junit.Test
import kiit.common.DateTime
import kiit.common.data.DataType
import kiit.common.ids.UPID
import kiit.meta.Reflector
import kiit.meta.models.FieldCategory
import kiit.meta.models.Model
import test.setup.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createType


class ModelTests {


    @Test fun can_build_simple_model_from_reflection(){
        val model = Model.load(AuthorR::class, AuthorR::id.name)
        ensureAuthorModel(model)
    }


    @Test fun can_build_simple_model_from_schema(){
        val model = loadSchemaSpecification()
        ensureAuthorModel(model)
    }


    @Test fun can_build_complex_model_from_schema(){
        val model = Model.load(UserWithAddress::class, UserWithAddress::id.name)
        val addrProp = model.fields.find { it.name == "addr" }
        Assert.assertTrue( addrProp != null)
        Assert.assertTrue( addrProp!!.model != null)
        Assert.assertTrue( addrProp!!.model?.dataType == Address::class)
    }


    @Test fun can_build_simple_model_with_nullable(){
        val model = Model.load(AuthorRNull::class, AuthorRNull::id.name)
        Assert.assertTrue(model.hasId)
        Assert.assertTrue(model.any)
        ensureField(model, "id"        , false, Long::class       )
        ensureField(model, "email"     , false, String::class     )
        ensureField(model, "isActive"  , false, Boolean::class    )
        ensureField(model, "age"       , false, Int::class        )
        ensureField(model, "status"    , false, StatusEnum::class )
        ensureField(model, "salary"    , false, Double::class     )
        ensureField(model, "createdAt" , false, DateTime::class   )
    }


    @Test fun can_build_simple_with_sub_objects(){
        val model = Model.load(SampleEntityImmutable::class, SampleEntityImmutable::id.name)
        Assert.assertTrue(model.hasId)
        Assert.assertTrue(model.any)
        fun ensure(name:String, storedAs:String, type:DataType, model: Model){
            Assert.assertTrue(model.lookup[name] != null)
            Assert.assertEquals(storedAs, model.lookup[name]?.storedName)
            Assert.assertEquals(type, model.lookup[name]?.dataTpe)
        }
        ensure("test_object", "test_object", DataType.DTObject, model)
        ensure("test_object_state", "test_object_state", DataType.DTString, model)
        ensure("test_object_country", "test_object_country", DataType.DTInt, model)
        ensure("test_object_isPOBox", "test_object_isPOBox", DataType.DTBool, model)
    }


    @Test fun can_load_nullable(){
        data class Test(val str1:String, val str2:String? = null)

        val a1Type  = Test::str1.returnType
        val a1Cls   = Test::str1.returnType.classifier as KClass<*>
        val a1Type2 = Test::str1.returnType
        val a1Type3 =(Test::str1.returnType.classifier as KClass<*>).createType()


        val a2Type  = Test::str2.returnType
        val a2Cls   = Test::str2.returnType.classifier as KClass<*>
        val a2Type2 = Test::str2.returnType
        val a2Type3 =(Test::str2.returnType.classifier as KClass<*>).createType()

        val props = Reflector.getProperties(Test::class)

        val prop1 = props.get(0)
        val prop1Type = prop1.returnType

        val prop2 = props.get(1)
        val prop2Type = prop2.returnType

        Assert.assertEquals(a1Type, a1Type2)
        Assert.assertEquals(a1Type, a1Type3)
        Assert.assertEquals(a1Type, prop1Type)
        Assert.assertEquals(a1Type.isMarkedNullable, a1Type3.isMarkedNullable)
        Assert.assertEquals(a1Type.isMarkedNullable, prop1Type.isMarkedNullable)

        Assert.assertEquals(a2Type, a2Type2)
        Assert.assertEquals(a2Type, prop2Type)
        //Assert.assertEquals(a2Type, a2Type3)
        Assert.assertEquals(a2Type.isMarkedNullable, prop2Type.isMarkedNullable)
    }


    fun ensureField(model: Model, name:String, required:Boolean, tpe: KClass<*>):Unit {
        val field = model.fields.find { it.name == name }
        Assert.assertTrue(field != null)
        Assert.assertTrue(field!!.isRequired == required)
        Assert.assertTrue(field!!.dataCls == tpe)
    }


    private fun ensureAuthorModel(model: Model) {

        Assert.assertTrue(model.hasId)
        Assert.assertTrue(model.any)
        Assert.assertTrue(model.idField!!.name == "id")
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
        ensureField(model, "shardId"   , true, UPID::class )
    }


    private fun loadSchemaSpecification(): Model {
        val model = Model.of<Long, AuthorR>(Long::class, AuthorR::class) {
            field(AuthorR::id, category = FieldCategory.Id)
            field(AuthorR::createdAt)
            field(AuthorR::createdBy)
            field(AuthorR::updatedAt)
            field(AuthorR::updatedBy)
            field(AuthorR::uuid)
            field(AuthorR::email)
            field(AuthorR::isActive)
            field(AuthorR::age)
            field(AuthorR::salary)
            field(AuthorR::uid)
            field(AuthorR::shardId)
        }
        return model
    }
}
