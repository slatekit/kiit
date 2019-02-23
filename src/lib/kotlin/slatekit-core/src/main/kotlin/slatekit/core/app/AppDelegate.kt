package slatekit.core.app

import slatekit.common.flatten
import slatekit.results.*


class AppDelegate {

    /**
     * Run the app using the workflow init -> execute -> end
     */
    fun run(app:App): Try<Any> = init(app).map { execute(app) }.map { end(app) }


    /**
     * Initialize the app
     */
    fun init(app:App):Try<Any> {
        // Wrap App.init() call for safety
        // This will produce a nested Try<Try<Boolean>>
        val rawResult = Try.attempt { app.init() }

        // Flatten the nested Try<Try<Boolean>> into a simple Try<Boolean>
        val result = rawResult.flatten()

        // Finally flatMap it to ensure creation of directories for the app.
        return result.flatMap {
            Try.attempt {
                app.ctx.dirs?.create()
                it
            }.onFailure {
                println("Error while creating directories for application in user.home directory")
            }
        }
    }


    /**
     * Execute the app
     */
    fun execute(app:App):Try<Any> {

        if (app.options.printSummaryBeforeExec) {
            app.info()
        }

        // Wrap App.init() call for safety
        // This will produce a nested Try<Try<Boolean>>
        val rawResult = Try.attempt { app.execute() }

        // Flatten the nested Try<Try<Boolean>> into a simple Try<Boolean>
        val result = rawResult.flatten()

        // Finally convert the error
        return result.mapError {
            Exception("Unexpected error : " + it.message, it)
        }
    }


    /**
     * Shutdown / end the app
     */
    fun end(app:App): Try<Any> {
        // Wrap App.init() call for safety
        // This will produce a nested Try<Try<Boolean>>
        val rawResult = Try.attempt { app.end() }

        // Flatten the nested Try<Try<Boolean>> into a simple Try<Boolean>
        val result = rawResult.flatten()

        // Finally convert the error
        return result.mapError {
            Exception("error while shutting down app : " + it.message, it)
        }
    }
}