package kiit.data.sql

import kiit.common.data.*
import kiit.data.Consts
import kiit.data.core.Meta
import kiit.data.Mapper

/**
 * Used to build the syntax for update statements for a model using a mapper
 * @param info: Meta info to know about the table (name, primary key ) and model id
 * @param mapper: Mapper that converts a model T into its values for a table
 */
open class Update<TId, T>(val dialect: Dialect, val meta: Meta<TId, T>, val mapper: Mapper<TId, T>)
    where TId : kotlin.Comparable<TId>, T : Any {

    /**
     * Builds the full SQL statement
     * e.g. "update `movies` set name = 'batman', category = 'action' where id = 1;"
     * sql = "update `movies` set name = 'batman', category = 'action' where id = ?;"
     * prep = "update `movies` set name = ?, `category` = ?
     *          listOf(
     *              Value("name", "batman"),
     *              Value("category", "action")
     *          )
     */
    open fun build(item: T, mode: BuildMode = BuildMode.Prep): Command {
        val start = prefix()
        val values = mapper.encode(item, DataAction.Update, null)
        val idName = meta.encode(meta.pkey.name)
        val idValue = meta.id.identity(item)
        return when(mode){
            BuildMode.Sql -> {
                val args = values.joinToString(",", transform = { "${it.name} = ${it.text ?: Consts.NULL}" } )
                val sql ="$start SET $args WHERE $idName = $idValue;"
                Command(sql, listOf(), listOf())
            }
            BuildMode.Prep -> {
                val args = values.joinToString(",", transform = { "${it.name} = ?" } )
                val sql = "$start SET $args WHERE $idName = ?;"
                val finalValues = values + Value(meta.pkey.name, meta.pkey.type, idValue)
                Command(sql, finalValues, listOf())
            }
        }
    }


    fun prefix(): String = "update " + dialect.encode(meta.name)
}
