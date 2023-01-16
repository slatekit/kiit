package kiit.migrations

sealed class MigrationType {
    object TableCreate : MigrationType()
    object TableUpdate : MigrationType()
    object TableDelete : MigrationType()
    object Custom : MigrationType()
}
