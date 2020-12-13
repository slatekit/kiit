package slatekit.jobs

sealed class Target(val name:String) {
    object Job : Target("job")
    object Wrk : Target("wrk")
}
