package kiit.migrations

import kiit.common.data.IDb
import kiit.results.Outcome

interface Migration {

    /**
     * Id of the migration
     */
    val id: String get() = start.toString() + "_" + end.toString()

    /**
     * Description of migration
     */
    val desc: String

    /**
     * The start version
     */
    val start: Int

    /**
     * The end version
     */
    val end: Int

    /**
     * The migration steps
     */
    val steps: List<MigrationStep>

    /**
     * Performs the migration
     */
    fun migrate(db: IDb): Outcome<Int>
}
