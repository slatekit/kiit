package slatekit.actors

interface Issuer {

    /**
     * Sends a request message
     * This represents a request to get payload internally ( say from a queue ) and process it
     */
    suspend fun request()


    /**
     * Sends a request message associated with the supplied target
     * This represents a request to get payload internally ( say from a queue ) and process it
     * @param reference  : Full message
     */
    suspend fun request(reference: String)
}
