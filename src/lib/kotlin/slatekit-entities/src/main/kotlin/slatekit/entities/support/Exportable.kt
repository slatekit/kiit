package slatekit.entities.support

import slatekit.common.utils.Export
import slatekit.entities.Entity
import slatekit.entities.EntityServices
import slatekit.meta.kClass
import slatekit.meta.models.Model
import slatekit.results.Try
import slatekit.results.builders.Tries

/**
 * Created by kishorereddy on 6/3/18.
 */

interface Exportable<TId, T> where TId : Comparable<TId>, T : Entity<TId> {

    val schema: Int
    val model: Model
    val service: EntityServices<TId, T>

    /**
     * Exports all items
     *
     * @param version
     * @return
     */
    fun exportAll(version: String): Try<Export<List<T>>> {
        return exportItems(version, service.getAll())
    }

    /**
     * Exports a single item ( returned as an array ) using the id.
     *
     * @param version
     * @param id
     * @return
     * @throws Exception
     */
    fun exportById(version: String, id: TId): Try<Export<T>> {
        return exportItem(version, service.get(id))
    }

    /**
     * Exports a single item as an array
     *
     * @param version
     * @param item
     * @return
     */
    fun exportItem(version: String, item: T?): Try<Export<T>> {
        if (item == null) {
            return Tries.invalid("Not supplied")
        }

        val path = "get/" + item.identity()
        val result = try {
            val data = mapToContent(version, schema, listOf(item))
            val export = Export(version, item.kClass.simpleName ?: "", path, "app", "", "json", 1, data, item)
            Tries.success(export, "")
        } catch (ex: Exception) {
            val message = "Error exporting item : " + item?.identity() + ". " + ex.message
            Tries.errored<Export<T>>(Exception(message, ex))
        }
        return result
    }

    /**
     * Exports all the items supplied.
     *
     * @param version
     * @param items
     * @return
     */
    fun exportItems(version: String, items: List<T>?): Try<Export<List<T>>> {

        // Available ?
        if (items == null || items.isEmpty()) {
            return Tries.errored("No items")
        }
        val item = items[0] as Entity<TId>
        val result = try {
            val path = if (items.size == 1) "get/" + item.identity() else "getAll"
            val data = mapToContent(version, schema, items)
            val export = Export(version, item.javaClass.name, path, "app", "", "json", items.size, data, items)
            Tries.success(export, "")
        } catch (ex: Exception) {
            val message = "Error exporting item : " + item.identity() + ". " + ex.message
            Tries.errored<Export<List<T>>>(ex)
        }
        return result
    }

    /** Loads items using the string content supplied.
     * Needs to implemented in derived repository classes.
     * @param items: The items to convert into a string.
     * @throws Exception
     */
    abstract fun mapToContent(version: String, schema: Int, items: List<T>?): String
}
