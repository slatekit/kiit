package slatekit.functions.common


enum class FunctionMode constructor(val value: Int) {
    Called(0),     // In code
    Forced(1),     // On-Demand
    Scheduled(2),  // Scheduler
    Interacted(3), // CLI
    Evented(4);    // Events
}