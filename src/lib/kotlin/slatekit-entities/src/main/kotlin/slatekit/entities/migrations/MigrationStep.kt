package slatekit.entities.migrations

import slatekit.common.db.IDb
import slatekit.results.Outcome

interface MigrationStep {
    val type:MigrationType

    fun run(db: IDb):Outcome<String>
}


