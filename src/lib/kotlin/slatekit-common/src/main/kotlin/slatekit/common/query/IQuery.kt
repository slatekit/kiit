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

package slatekit.common.query

/**
 * Created by kreddy on 12/24/2015.
 */
interface IQuery {
    fun toUpdates(): List<FieldValue>

    fun toUpdatesText(): String

    fun toFilter(): String

    fun set(field: String, fieldValue: Any): IQuery

    fun set(vararg pairs:Pair<String, Any>): IQuery

    fun where(field: String, compare: String, fieldValue: Any): IQuery

    fun where(field: String, compare: Op, fieldValue: Any): IQuery

    fun and(field: String, compare: String, fieldValue: Any): IQuery

    fun and(field: String, compare: Op, fieldValue: Any): IQuery

    fun or(field: String, compare: String, fieldValue: Any): IQuery

    fun or(field: String, compare: Op, fieldValue: Any): IQuery

    fun orderBy(field: String, mode: String): IQuery

    fun limit(max: Int): IQuery

    fun join(model: String, modelField: String, refField: String): IQuery
}
