package kiit.utils.display

/**
 * A color group that contains light/normal/dark color with name
 * @param name      : "Indigo"
 * @param color     : hex color
 * @param colorLight: hex for light version of color
 * @param colorDark : hex for dark  version of color
 */
data class ColorGroup(
    @JvmField val name: String,
    @JvmField val color: Int,
    @JvmField val colorHex: Int,
    @JvmField val colorLightHex: Int,
    @JvmField val colorDarkHex: Int
)
