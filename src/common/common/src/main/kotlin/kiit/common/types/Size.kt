package kiit.common.types

data class Size(
        @JvmField val width: Int,
        @JvmField val height: Int,
        @JvmField val density: Int = 1
) {
    constructor(width: Int, height: Int) : this(width, height, 1)


    constructor(width: Double, height: Double) : this(width.toInt(), height.toInt(), 1)
}