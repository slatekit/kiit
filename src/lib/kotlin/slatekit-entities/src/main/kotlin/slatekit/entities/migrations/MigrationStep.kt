package slatekit.entities.migrations

import slatekit.common.data.IDb
import slatekit.results.Outcome

interface MigrationStep {
    val type: MigrationType

    fun run(db: IDb): Outcome<String>
}
