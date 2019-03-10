package test.common

import org.junit.Test
import slatekit.common.queues.QueueSourceInMemory

class QueueSourceTests {

    @Test
    fun can_add() {
        val queue = QueueSourceInMemory.stringQueue()
        queue.send("1")
        queue.send("2")
        assert( queue.count() == 2)
    }


    @Test
    fun can_take() {
        val queue = QueueSourceInMemory.stringQueue()
        queue.send("1")
        queue.send("2")
        queue.send("3")
        assert( queue.count() == 3)

        val item1 = queue.next()?.getValue()
        val item2 = queue.next()?.getValue()

        assert(queue.count() == 1)
        assert(item1 == "1")
        assert( item2 == "2")
    }


    @Test
    fun can_take_many() {
        val queue = QueueSourceInMemory.stringQueue()
        queue.send("1")
        queue.send("2")
        queue.send("3")
        assert( queue.count() == 3)

        val items = queue.next(2)!!
        val item1 = items[0].getValue()
        val item2 = items[1].getValue()

        assert(queue.count() == 1)
        assert(item1 == "1")
        assert( item2 == "2")
    }


    @Test
    fun can_have_limit() {
        val queue = QueueSourceInMemory.stringQueue(3)
        queue.send("1")
        queue.send("2")
        val result3 = queue.send("3")
        val result4 = queue.send("4")
        assert( queue.count() == 3)
        assert( result3.success )
        assert( !result4.success )
    }
}