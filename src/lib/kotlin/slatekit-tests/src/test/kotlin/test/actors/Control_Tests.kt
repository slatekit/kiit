package test.actors

import kotlinx.coroutines.channels.Channel
import org.junit.Test
import slatekit.core.actors.Control

class Control_Tests {


    @Test
    fun can_pull(){
        val actor = ""
        val channel = Channel<Int>(Channel.UNLIMITED)
        val control = Control(channel)
    }


    @Test
    fun can_poll(){

    }


    @Test
    fun can_wipe(){

    }
}