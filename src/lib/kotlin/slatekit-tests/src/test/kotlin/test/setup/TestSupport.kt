package test.setup

import slatekit.common.conf.ConfFuncs
import slatekit.common.data.DbCon

interface TestSupport {

    fun getConnection(): DbCon {
        val path = "user://.slatekit/conf/db.conf"
        val con = ConfFuncs.readDbCon(path)
        return con ?: throw Exception("can not run unit-tests with missing connection at path :$path")
    }
}