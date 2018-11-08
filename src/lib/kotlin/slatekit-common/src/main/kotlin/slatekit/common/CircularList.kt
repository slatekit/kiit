package slatekit.common

class CircularList<T>(val list:List<T>) {
    private var index = 0

    val size:Int = list.size


    fun next():T {
        index = if(index + 1 >= size) 0 else index + 1
        return list[index]
    }


    fun previous():T {
        index = if(index - 1 < 0 ) size - 1 else index - 1
        return list[index]
    }


    fun current():T = list[index]
}