package test.entities

import org.junit.Assert
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import slatekit.common.DateTimes
import slatekit.common.data.*
import slatekit.common.ids.ULIDs
import slatekit.db.Db
import test.setup.TestSupport
import java.util.*

/**
 * DONE:
 * 1. connect : Db.of() // config, con, etc
 * 2. prepare : db.update("sql..", listOf(...) )
 * 3. methodX : db....
 *
 *
 * 4. ddl     : db.createTable, createColumn, createKey, createIndex ( just for models )
 * 4. Sqlite  : SqliteProvider ....
 * 5. Postgres: PostgresProvider ....
 */
class Data_04_Database_Procs : TestSupport {


    private val table = "sample_entity"

    @Test
    fun can_execute_proc() {
        val db = db()
        val result = db.callQuery("dbtests_get_max_id", { rs -> rs.getLong(1) })
        Assert.assertTrue(result!! > 0L)
    }


    @Test
    fun can_execute_proc_update() {
        val db = db()
        val result = db.callUpdate("dbtests_update_by_id", listOf(Value("", DataType.DTInt, 6)))
        Assert.assertTrue(result!! >= 1)
    }


    fun db(): IDb {
        return EntitySetup.db()
    }
}
