package kiit.migrations

import kiit.common.data.IDb
import kiit.results.Outcome

interface MigrationStep {
    val type: MigrationType

    fun run(db: IDb): Outcome<String>
}
