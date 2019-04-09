package slatekit.core.common.functions


enum class FunctionMode constructor(val value: Int) {
    Called(0),    // In code
    Triggered(1), // On-Demand
    Scheduled(2), // Scheduler
    Interacted(3), // CLI
    Evented(4) // CLI
}