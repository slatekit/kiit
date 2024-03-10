package slatekit.status

expect class Sample() {
    fun checkMe(): Int
}

expect object Platform {
    val name: String
}

fun hello(): String = "Hello from ${Platform.name}"

/*
1. create
2. compile
3. test
4. gradle compile, test
5. repository setup
6. publishing
7. reuse
 */