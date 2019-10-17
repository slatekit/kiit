package test.jobs

import org.junit.Assert
import org.junit.Test

import slatekit.common.queues.QueueSourceInMemory
import slatekit.common.queues.QueueStringConverter
import slatekit.jobs.Priority
import slatekit.jobs.Queue
import slatekit.jobs.support.Queues

class Queue_Tests {

    @Test
    fun can_load_queues_basic() {

        val queue = QueueSourceInMemory<String>(converter = QueueStringConverter())
        val infos = (0..2).map { it -> Queue(it.toString(), Priority.Low, queue) }
        val queues = Queues(infos)
        Assert.assertEquals(3, queues.size())
        Assert.assertEquals(queues[0]?.name, "0")
        Assert.assertEquals(queues[1]?.name, "1")
        Assert.assertEquals(queues[2]?.name, "2")
        Assert.assertEquals(queues["0"]?.name, "0")
        Assert.assertEquals(queues["1"]?.name, "1")
        Assert.assertEquals(queues["2"]?.name, "2")
    }

    @Test
    fun can_load_queues_prioritized() {

        val queue = QueueSourceInMemory<String>(converter = QueueStringConverter())
        val infos =
            (0..2).map { it -> Queue(it.toString(), Priority.convert(it + 1) as Priority, queue) }
        val queues = Queues(infos)
        Assert.assertEquals(3, queues.size())
        Assert.assertEquals(6, queues.prioritizedQueues.size)
        Assert.assertEquals(queues.prioritizedQueues[0].name, "0")
        Assert.assertEquals(queues.prioritizedQueues[1].name, "1")
        Assert.assertEquals(queues.prioritizedQueues[2].name, "1")
        Assert.assertEquals(queues.prioritizedQueues[3].name, "2")
        Assert.assertEquals(queues.prioritizedQueues[4].name, "2")
        Assert.assertEquals(queues.prioritizedQueues[5].name, "2")

        val pos = queues.next().name.toInt()
        Assert.assertTrue(pos >= 0 && pos < queues.size())
    }
}
