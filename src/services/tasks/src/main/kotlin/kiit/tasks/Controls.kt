package kiit.tasks

import kiit.results.Outcome

/**
 * Convenience interface to control an @see[Action].
 */
interface Controls {
    /**
     * Fully qualified name of the action
     * @param fullName: "signup.registration.sendWelcome"
     */
    fun start(fullName:String): Outcome<Status> {
        return control(fullName, Command.Start)
    }


    /**
     * Fully qualified name of the action
     * @param fullName: "signup.registration.sendWelcome"
     */
    fun stop(fullName:String): Outcome<Status> {
        return control(fullName, Command.Stop)
    }


    /**
     * Fully qualified name of the action
     * @param fullName: "signup.registration.sendWelcome"
     * @param seconds : Number of seconds to pause ( not used yet )
     */
    fun pause(fullName:String, seconds:Long): Outcome<Status> {
        return control(fullName, Command.Pause)
    }


    /**
     * Fully qualified name of the action
     * @param fullName: "signup.registration.sendWelcome"
     */
    fun resume(fullName:String): Outcome<Status> {
        return control(fullName, Command.Resume)
    }


    /**
     * Perform a graceful controlled change to the action.
     * This is to start, stop, pause or resume the action.
     * @param fullName: "signup.registration.sendWelcome"
     */
    fun control(fullName:String, command:Command): Outcome<Status>
}