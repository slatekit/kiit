package test.common

import org.junit.Test
import slatekit.common.queues.QueueSourceDefault

class QueueSourceTests {

    @Test
    fun can_add() {
        val queue = QueueSourceDefault()
        queue.send("1")
        queue.send("2")
        assert( queue.count() == 2)
    }


    @Test
    fun can_take() {
        val queue = QueueSourceDefault()
        queue.send("1")
        queue.send("2")
        queue.send("3")
        assert( queue.count() == 3)

        val item1 = queue.getMessageBody(queue.next())
        val item2 = queue.getMessageBody(queue.next())

        assert(queue.count() == 1)
        assert(item1 == "1")
        assert( item2 == "2")
    }


    @Test
    fun can_take_many() {
        val queue = QueueSourceDefault()
        queue.send("1")
        queue.send("2")
        queue.send("3")
        assert( queue.count() == 3)

        val items = queue.nextBatch(2)!!
        val item1 = queue.getMessageBody(items[0])
        val item2 = queue.getMessageBody(items[1])

        assert(queue.count() == 1)
        assert(item1 == "1")
        assert( item2 == "2")
    }


    @Test
    fun can_have_limit() {
        val queue = QueueSourceDefault(size = 3)
        queue.send("1")
        queue.send("2")
        val result3 = queue.send("3")
        val result4 = queue.send("4")
        assert( queue.count() == 3)
        assert( result3.success )
        assert( !result4.success )
    }
}