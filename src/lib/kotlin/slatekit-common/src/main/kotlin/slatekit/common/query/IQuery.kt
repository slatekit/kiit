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


    /// <summary>
    /// Adds a WHERE clause to the query.
    /// </summary>
    /// <param name="exp">Expression to retrieve property name.</param>
    /// <returns>This instance.</returns>
    fun set(field: String, fieldValue: Any): IQuery


    /// <summary>
    /// Adds a WHERE clause to the query.
    /// </summary>
    /// <param name="exp">Expression to retrieve property name.</param>
    /// <returns>This instance.</returns>
    fun where(field: String, compare: String, fieldValue: Any): IQuery


    fun where(field: String, compare: Op, fieldValue: Any): IQuery


    fun where(field: kotlin.reflect.KProperty<*>, compare: kotlin.String, fieldValue: kotlin.Any): IQuery


    fun and(field: kotlin.reflect.KProperty<*>, compare: String, fieldValue: Any): IQuery


    /// <summary>
    /// Adds an AND condition to the query.
    /// </summary>
    /// <param name="exp">Expression to retrieve property name.</param>
    /// <returns>This instance.</returns>
    fun and(field: String, compare: String, fieldValue: Any): IQuery


    fun and(field: String, compare: Op, fieldValue: Any): IQuery


    /// <summary>
    /// Adds an OR condition to the query.
    /// </summary>
    /// <param name="exp">Expression to retrieve property name.</param>
    /// <returns>This instance.</returns>
    fun or(field: String, compare: String, fieldValue: Any): IQuery


    fun or(field: String, compare: Op, fieldValue: Any): IQuery

    fun orderBy(field: String): IQuery

    fun asc(): IQuery


    fun desc(): IQuery


    fun limit(max: Int): IQuery
}
