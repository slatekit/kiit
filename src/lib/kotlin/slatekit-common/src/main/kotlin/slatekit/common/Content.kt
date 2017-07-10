package slatekit.common


/**
 * represents a container for content and the conetnt type.
 * e.g.
 *
 * 1. string, json
 * 2. string, csv
 *
 * @param text
 * @param format
 */
data class Content(val text:String, val format:String, val ext:String) {

    /**
     * whether this content is empty
     * @return
     */
    val isEmpty: Boolean = text.isNullOrEmpty()


    /**
     * whether this content is present
     * @return
     */
    val isDefined: Boolean = !isEmpty


    /**
     * the length of the content
     * @return
     */
    val size : Int = text.length


    companion object {

        fun  csv(text:String):Content =  Content(text, "text/csv", "csv")


        fun  html(text:String):Content = Content(text, "text/html", "html")


        fun  json(text:String):Content =  Content(text, "application/json", "json")


        fun  text(text:String):Content =  Content(text, "text/plain", "text")


        fun  xml(text:String):Content = Content(text, "application/xml", "xml")
    }
}