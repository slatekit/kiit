package slatekit.jobs


sealed class WorkState(val name:String) {
    object Done       : WorkState( "Done"    )
    object More       : WorkState( "More"    )
    object Unknown    : WorkState( "Unknown" )
    data class Next(val offset:Long, val processed:Long, val reference:String) : WorkState( "next"  )


    fun parse(name:String):WorkState {
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