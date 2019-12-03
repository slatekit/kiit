package entities

import slatekit.common.Record
import slatekit.common.data.IDb
import java.sql.ResultSet

class SqliteDb : IDb {
    override val onError: (Exception) -> Unit
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun open(): IDb {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun execute(sql: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> getScalarOrNull(sql: String, typ: Class<*>, inputs: List<Any>?): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(sql: String, inputs: List<Any>?): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insertGetId(sql: String, inputs: List<Any>?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(sql: String, inputs: List<Any>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> query(sql: String, callback: (ResultSet) -> T?, moveNext: Boolean, inputs: List<Any>?): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> mapOne(sql: String, inputs: List<Any>?, mapper: (Record) -> T?): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> mapAll(sql: String, inputs: List<Any>?, mapper: (Record) -> T?): List<T>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> callQuery(procName: String, callback: (ResultSet) -> T?, moveNext: Boolean, inputs: List<Any>?): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> callQueryMapped(procName: String, mapper: (Record) -> T?, inputs: List<Any>?): List<T>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun callUpdate(procName: String, inputs: List<Any>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun errorHandler(ex: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}