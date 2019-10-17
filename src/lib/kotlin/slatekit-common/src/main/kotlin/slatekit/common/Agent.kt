package slatekit.common



/**
 * Represents a type of service where code can run
 */
sealed class Agent(override val value:Int, override val name:String) : EnumLike {
    object App      : Agent(0, "App" ) // Console application
    object CLI      : Agent(1, "CLI" ) // Command Line Interface
    object Web      : Agent(2, "Web" ) // Web application UI
    object API      : Agent(3, "API" ) // Web/REST/RPC APIs
    object Bot      : Agent(4, "Bot" ) // Chat Bot/Bot
    object Job      : Agent(5, "Job" ) // Background job
    object Cmd      : Agent(6, "Cmd" ) // Function/Command/Task
    object Svc      : Agent(7, "Svc" ) // Generic Service ( e.g. for anything else )
    object Test     : Agent(8, "Test") // Unit/Integration/System tests
    class  Other(name:String): Agent(9, name) // Other type of agent


    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(App, CLI, Web, API, Bot, Job, Cmd, Svc, Test)
        }

        override fun isUnknownSupported(): Boolean {
            return true
        }

        override fun unknown(name: String): EnumLike {
            return Other(name)
        }

        override fun unknown(value: Int): EnumLike {
            return Other("unknown")
        }
    }
}
