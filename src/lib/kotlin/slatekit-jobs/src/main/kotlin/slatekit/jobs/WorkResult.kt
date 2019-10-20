package slatekit.jobs


sealed class WorkResult(val name:String) {
    object Unknown    : WorkResult( "Unknown" )
    object Done       : WorkResult( "Done"    )
    object More       : WorkResult( "More"    )
    data class Next(val offset:Long, val processed:Long, val reference:String) : WorkResult( "next"  )


    fun parse(name:String):WorkResult {
        return when(name) {
            Done.name    -> Done
            More.name    -> More
            else         -> {
                val tokens = name.split(".")
                val first = tokens[0]
                when(first){
                    "Next" -> Next(tokens[1].toLong(), tokens[2].toLong(), tokens[3])
                    else   -> Unknown
                }
            }
        }
    }
}



