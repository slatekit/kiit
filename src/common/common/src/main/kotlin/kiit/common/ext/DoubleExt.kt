package kiit.common.ext

fun Double.format(digits: Int) = "%.${digits}f".format(this)