package kiit.core.eventing

object EventUtils {

    /**
     * Cleans up the topic name for standardization.
     * Rules:
     * 1. No spaces
     * 2. No dashes ( only underscores )
     * @param raw
     * @return
     */
    fun clean(raw:String, standardize:Boolean = true):String {
        val cleaned = raw.replace(" ", "_")
        return if(standardize) cleaned.replace("-", "_") else cleaned
    }

    /**
     * Builds the Event topic info from raw name ( enforces standardization )
     * @param raw :  {area}.{name}.{env}
     * @return
     */
    fun topic(raw:String, standardize:Boolean = true):EventTopic {
        val parts = raw.split('-')
        val area = parts[0]
        val name = parts[1]
        val env = parts[2]
        val finalName = if(standardize) clean(name) else name
        return EventTopic(area, finalName, env)
    }
}