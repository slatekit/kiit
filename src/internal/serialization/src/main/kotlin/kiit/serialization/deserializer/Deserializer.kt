/**
 <kiit_header>
url: www.kiit.dev
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package kiit.serialization.deserializer

import kiit.requests.Request
import kotlin.reflect.KParameter

/**
 * Deserializer interface
 *
 * DESIGN:
 * This custom serializer exists because the use cases are not (At the moment) supported by others such as
 * 1. GSON
 * 2. Jackson
 * 3. Moshi
 *
 * USE-CASES:
 * 1. A context object ( in this case the Request - API, CLI ) is supplied to the deserializers
 * 2. Deserialize a list of specific parameters not a single value to deserialize
 * 3. Providing custom deserializers that use the context object separate from the source (e.g. JSON )
 */
interface Deserializer<TSource> {
    /**
     * converts the source object data ( JSON ) into the instances of the parameter types
     * @param parameters: The parameter info to convert
     * @param source : The json object containing the data
     */
    fun deserialize(parameters: List<KParameter>, source: TSource): Array<Any?>

    /**
     * converts the source object data ( JSON ) into the instances of the parameter types
     * @param parameter: The parameter info to convert
     * @param source : The json object containing the data
     */
    fun deserialize(parameter: KParameter, source: TSource): Any?

    /**
     * converts the source object data ( JSON ) into the instances of the parameter types
     * @param parameters: The parameter info to convert
     * @param req : The kiit request contain data
     */
    fun deserialize(parameters: List<KParameter>, req:Request): Array<Any?>
}


