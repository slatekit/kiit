package slatekit.common.functions


enum class FunctionMode constructor(val value: Int) {
    Normal(0),    // In code
    Triggered(1), // On-Demand
    Scheduled(2), // Scheduler
    Interacted(3), // CLI
    Evented(4) // CLI
}