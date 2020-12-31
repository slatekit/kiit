package test.setup

import slatekit.common.conf.Confs
import slatekit.common.data.DbCon

interface TestSupport {

    fun getConnection(): DbCon {
        val path = "user://.slatekit/conf/db.conf"
        val con = Confs.readDbCon(path)
        return con ?: throw Exception("can not run unit-tests with missing connection at path :$path")
    }
}