package slatekit.generator

import kiit.common.EnumLike
import kiit.common.EnumSupport

/**
 * Represents how to generate/reference credentials in the config file
 * They are never stored as plain text.
 * 1. EnvVars: Generate credentials using references to environment variables
 * 2. Encrypt: Generate credentials using encrypted strings
 * 3. FileRefs: Generate credentials as references to files in user "~" directory
 */
enum class CredentialMode(override val value: Int) : EnumLike {
    EnvVars(0),
    Encrypted(1),
    FileRefs(2) ;

    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(EnvVars, Encrypted, FileRefs)
        }
    }
}
