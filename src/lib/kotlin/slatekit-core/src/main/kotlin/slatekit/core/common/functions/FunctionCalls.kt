package slatekit.core.common.functions


interface FunctionCalls<out Result> {

    /**
     * execute this function indicating triggered or forced mode
     *
     * @param
     * @return
     */
    fun trigger() {
        execute(arrayOf(), FunctionMode.Triggered)
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