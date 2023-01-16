package kiit.policy.throttle



class Throttles(val all:List<Throttle>) {

    fun named(name:String): Throttle? = all.first{ it.name == name }


    fun current(value:Int): Throttle? = all.first{ it.span.isWithin(value) }


    companion object {
        /**
         * Default throttle settings for peak hours of the day
         * @return
         */
        fun traffic():List<Throttle> {
            return listOf(
                    Throttle("a", Span("hhmm", 0, 700), Rate.High, NoSettings),
                    Throttle("b", Span("hhmm", 700, 800), Rate.Low, NoSettings),
                    Throttle("c", Span("hhmm", 800, 1000), Rate.Zero, NoSettings),
                    Throttle("d", Span("hhmm", 1000, 1200), Rate.Mid, NoSettings),
                    Throttle("e", Span("hhmm", 1200, 1400), Rate.Zero, NoSettings),
                    Throttle("f", Span("hhmm", 1400, 1800), Rate.Mid, NoSettings),
                    Throttle("g", Span("hhmm", 1800, 2000), Rate.Zero, NoSettings),
                    Throttle("h", Span("hhmm", 2000, 2359), Rate.High, NoSettings)
            )
        }
    }
}
