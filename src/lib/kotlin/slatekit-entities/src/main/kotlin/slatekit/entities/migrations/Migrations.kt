package slatekit.entities.migrations

import slatekit.common.data.IDb
import slatekit.common.log.LogSupport
import slatekit.common.log.Logger
import slatekit.results.Outcome
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Tries

data class SimpleMigration(
    override val desc: String,
    override val start: Int,
    override val end: Int,
    override val steps: List<MigrationStep>,
    override val logger: Logger?
) : Migration, LogSupport {

    override fun migrate(db: IDb): Outcome<Int> {
        info("Migration starting for : $id", null)
        val results = steps.map { Tries.of { it.run(db) } }
        val success = results.all { it.success }
        val message = if (success) "" else results.first { !it.success }.msg
        return when (success) {
            true -> {
                info("Migration success : $id. \n $message", null)
                Outcomes.success(steps.count())
            }
            false -> {
                info("Migration failure : $id. \n $message", null)
                Outcomes.errored(message)
            }
        }
    }
}
