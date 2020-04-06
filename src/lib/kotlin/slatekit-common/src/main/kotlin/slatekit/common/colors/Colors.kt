package slatekit.common.colors

/**
 * Selectable colors ( e.g. for handling/changing themes )
 * @sample
 * private val items = listOf(
 *     ColorGroup("Ruby"     , 0xFFE74C3C.toInt(), MaterialColors.Red500.toInt(), MaterialColors.Red100.toInt(), MaterialColors.Red700.toInt()),
 *     ColorGroup("Sapphire" , 0xFF3498DB.toInt(), MaterialColors.Blue500.toInt(), MaterialColors.Blue100.toInt(), MaterialColors.Blue700.toInt()),
 *     ColorGroup("Midnight" , 0xFF34495E.toInt(), MaterialColors.BlueGray800.toInt(), MaterialColors.BlueGray400.toInt(), MaterialColors.BlueGray900.toInt())
 * )
 */
data class Colors(val items:List<ColorGroup>) {
    fun all():List<ColorGroup> = items
    fun size():Int = items.size
    fun get(ndx:Int) = items[ndx]
    fun get(name:String):ColorGroup? = items.firstOrNull { it.name == name }
    fun colors():List<Int> = items.map { it.color }
}