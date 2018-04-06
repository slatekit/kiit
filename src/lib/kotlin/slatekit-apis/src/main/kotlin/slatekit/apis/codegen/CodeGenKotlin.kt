package slatekit.apis.codegen

import slatekit.apis.ApiContainer
import slatekit.meta.KTypes
import kotlin.reflect.KClass
import kotlin.reflect.KType


class CodeGenKotlin(container: ApiContainer,
                    val pathToTemplates:String,
                    val nameOfTemplateClass:String,
                    val nameOfTemplateMethod:String,
                    val nameOfTemplateModel:String) : CodeGenBase(container) {


    override fun templateClass():String {
        val content = getContent(pathToTemplates, nameOfTemplateClass)
        return content
    }


    override fun templateModel():String = getContent(pathToTemplates, nameOfTemplateModel)


    override fun templateMethod():String = getContent(pathToTemplates, nameOfTemplateMethod)

}