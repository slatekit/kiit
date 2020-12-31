package test.setup

import slatekit.common.conf.Confs
import slatekit.common.data.DbCon
import test.TestApp

interface TestSupport {

    val app:Class<*> get() { return TestApp::class.java }

    fun getConnection(): DbCon {
        val path = "user://.slatekit/conf/db.conf"
        val con = Confs.readDbCon(app, path)
        return con ?: throw Exception("can not run unit-tests with missing connection at path :$path")
    }
}