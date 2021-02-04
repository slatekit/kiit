/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.query

import slatekit.common.data.Value


/**
 * Created by kreddy on 12/24/2015.
 */
interface IQuery : Criteria<IQuery> {
    fun toUpdates(): List<Set>

    fun toUpdatesText(): String

    fun toFilter(): String

    fun hasOrderBy(): Boolean

    fun getOrderBy(): String

    fun set(field: String, fieldValue: Any?): IQuery

    fun set(vararg pairs: Value): IQuery

    fun set(pairs: List<Value>): IQuery

    fun set(vararg pairs: Pair<String, Any>): IQuery

    fun orderBy(field: String, mode: String): IQuery

    fun limit(max: Int): IQuery

    fun join(model: String, modelField: String, refField: String): IQuery
}
