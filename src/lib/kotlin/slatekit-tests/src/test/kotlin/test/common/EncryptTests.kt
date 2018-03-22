/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.common

import slatekit.common.encrypt.Encryptor

/**
 * Created by kishorereddy on 5/22/17.
 */

import org.junit.Test


class EncryptTests {


    data class Pair(val item1:String, val item2:String)



    fun encryptor(): Encryptor {

        val enc = Encryptor("wejklhviuxywehjk", "3214maslkdf03292")
        return enc
    }


    fun data(): List<Pair> {
        val pairs = listOf(
                Pair("1234567890", "xW6vZHVYvoqfJT7cNfeW8A"),
                Pair("abcdefghijklmnopqrstuvwxyz", "hoeqMGoGwHH2HVQDV2w2eiINfVJX/qX/u+06TmDesvg"),
                Pair("`~!@#$%^&*()-_=+", "rpPgvVt28fQVNjCZhh0y5v2/jn+aq9qZI757kuAszy8"),
                Pair("[]\\;',./{}|:\"<>?", "gbi+NTCgEvul2NtTkF+LOT2H7Di4UTZbNn7GtsUm2dY")
        )
        return pairs
    }


    @Test fun can_encrypt() {
        val enc = encryptor()
        val pairs = data()

        for(pair in pairs){
            val encrypted = enc.encrypt(pair.item1)
            assert( encrypted == pair.item2)
        }
    }


    @Test fun can_decrypt() {
        val enc = encryptor()
        val pairs = data()

        for(pair in pairs){
            val encrypted = enc.decrypt(pair.item2)
            assert( encrypted == pair.item1)
        }
    }
}