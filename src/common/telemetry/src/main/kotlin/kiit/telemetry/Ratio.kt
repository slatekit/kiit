package kiit.telemetry

data class Ratio(val name:String, val count:Long, val total:Long) {
    val value : Double = if(total == 0L) 0.0 else count / total.toDouble()

    operator fun compareTo(other:Double) = this.value.compareTo(other)

    operator fun compareTo(other:Ratio) = this.value.compareTo(other.value)
}
