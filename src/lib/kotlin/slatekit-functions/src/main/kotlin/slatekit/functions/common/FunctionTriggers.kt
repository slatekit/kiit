package slatekit.functions.common

interface FunctionTriggers<out Result> {

    /**
     * execute this function in normal mode with empty args
     */
    fun call() {
        this.execute(arrayOf(), FunctionMode.Called)
    }

    /**
     * execute this function indicating triggered or forced mode
     *
     * @param
     * @return
     */
    fun force() {
        execute(arrayOf(), FunctionMode.Forced)
    }

    /**
     * execute this function indicating scheduled mode
     *
     * @param
     * @return
     */
    fun schedule() {
        execute(arrayOf(), FunctionMode.Scheduled)
    }

    /**
     * execute this function indicating interactive mode with the supplied args as a line
     *
     * @param line
     * @return
     */
    fun interact(line: String, mode: FunctionMode) {
        execute(arrayOf(line), FunctionMode.Interacted)
    }

    /**
     * execute this function with the supplied args
     *
     * @param args
     * @return
     */
    fun execute(args: Array<String>, mode: FunctionMode)
}
