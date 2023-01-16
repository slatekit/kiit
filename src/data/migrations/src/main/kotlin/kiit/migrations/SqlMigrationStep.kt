package kiit.migrations

import slatekit.common.data.IDb
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes

data class SqlMigrationStep(
    override val type: MigrationType,
    val sql: String
) : MigrationStep {

    override fun run(db: IDb): Outcome<String> {
        return when (type) {
            MigrationType.Custom -> { db.execute(sql); Outcomes.success(sql) }
            else -> Outcomes.errored("Unexpected migration type")
        }
    }
}
