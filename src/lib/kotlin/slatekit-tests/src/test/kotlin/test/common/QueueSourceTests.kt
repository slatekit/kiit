package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.core.queues.QueueSourceInMemory

class QueueSourceTests {

    @Test
    fun can_add() {
        val queue = QueueSourceInMemory.stringQueue()
        queue.send("1")
        queue.send("2")
        Assert.assertTrue( queue.count() == 2)
    }


    @Test
    fun can_take() {
        val queue = QueueSourceInMemory.stringQueue()
        queue.send("1")
        queue.send("2")
        queue.send("3")
        Assert.assertTrue( queue.count() == 3)

        val item1 = queue.next()?.getValue()
        val item2 = queue.next()?.getValue()

        Assert.assertTrue(queue.count() == 1)
        Assert.assertTrue(item1 == "1")
        Assert.assertTrue( item2 == "2")
    }


    @Test
    fun can_take_many() {
        val queue = QueueSourceInMemory.stringQueue()
        queue.send("1")
        queue.send("2")
        queue.send("3")
        Assert.assertTrue( queue.count() == 3)

        val items = queue.next(2)!!
        val item1 = items[0].getValue()
        val item2 = items[1].getValue()

        Assert.assertTrue(queue.count() == 1)
        Assert.assertTrue(item1 == "1")
        Assert.assertTrue( item2 == "2")
    }


    @Test
    fun can_have_limit() {
        val queue = QueueSourceInMemory.stringQueue(3)
        queue.send("1")
        queue.send("2")
        val result3 = queue.send("3")
        val result4 = queue.send("4")
        Assert.assertTrue( queue.count() == 3)
        Assert.assertTrue( result3.success )
        Assert.assertTrue( !result4.success )
    }
}