package slatekit.apis



/**
 * Used to indicate the protocol(s) allowed for the ApiContainer
 * @param name
 */
abstract class Protocol(val name: String)


/**
 * Used to setup an ApiContainer to allow any protocol
 */
object AllProtocols : Protocol(slatekit.apis.ApiConstants.SourceAny)


/**
 * Used to setup an ApiContainer to allow only the CLI ( command line interface ) protocol
 */
object CliProtocol : Protocol(slatekit.apis.ApiConstants.SourceCLI)


/**
 * Used to setup an ApiContainer to allow only web/http protocol
 */
object WebProtocol : Protocol(slatekit.apis.ApiConstants.SourceWeb)
