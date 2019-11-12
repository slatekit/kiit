package slatekit.common.io

import slatekit.common.hasScheme
import slatekit.common.subStringPair

/**
 * Represents a simple form of a Uniform Resource Identifier ( URI ) specifically for a File reference.
 * NOTES:
 * 1. This is more flexible and lax compared to java.net.URI
 * 2. This is intended to be a light-weight alternative also to java.net.URI for files
 * 3. This supports empty Paths. e.g. could simply be "usr://" or "jar://"
 * @param scheme: The scheme           e.g. "usr | tmp | jar | cfg | file | http | https"
 * @param path  : The path to file     e.g. "usr://company/app1/conf/env.conf
 */
data class FileSource internal constructor(val raw:String,
                                           val scheme: Scheme,
                                           val path:String?) {

    companion object {
        fun of(path:String?):FileSource? {
            return when(path) {
                null -> null
                else -> when(path.hasScheme()){
                    true  -> parse(path)
                    false -> FileSource(path, Scheme.Path, path)
                }
            }
        }


        /**
         * Parses the rawPath. Examples:
         * 1. usr://app1/conf/env.props      -> ~/app1/conf/env.props
         * 2. tmp://app1/conf/env.props      -> $TMPDIR/app1/conf/env.props
         * 3. jar://env.props                -> app.jar/resources/env.props
         * 4. cfg://env.props                -> ./conf/env.props
         * 5. curr://env.props               -> ./env.props
         * 5. path:///users/batman/env.props -> /users/batman/env.props
         * 5. path://env.props               -> users/batman/env.props
         * 5. path://users/batman/env.props  -> /users/batman/env.props
         */
        fun parse(raw:String):FileSource {
            val pathParts = raw.subStringPair("://")
            val source = pathParts?.let { parts ->
                val source = Scheme.parse(parts.first)
                val path = parts.second
                FileSource(raw, source, path)
            } ?: FileSource(raw, Scheme.Path, raw)
            return source
        }
    }
}