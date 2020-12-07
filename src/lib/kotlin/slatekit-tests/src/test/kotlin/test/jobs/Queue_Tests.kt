package test.jobs

import org.junit.Assert
import org.junit.Test

import slatekit.core.queues.InMemoryQueue
import slatekit.core.queues.QueueStringConverter
import slatekit.core.queues.WrappedAsyncQueue
import slatekit.integration.jobs.JobQueue
import slatekit.jobs.Priority
import slatekit.jobs.support.Queues

class Queue_Tests {

    @Test
    fun can_load_queues_basic() {

        val queue = InMemoryQueue<String>(converter = QueueStringConverter())
        val infos = (0..2).map { it -> JobQueue(it.toString(), Priority.Low, WrappedAsyncQueue(queue)) }
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

        val queue = InMemoryQueue<String>(converter = QueueStringConverter())
        val infos =
            (0..2).map { it -> JobQueue(it.toString(), Priority.convert(it + 1) as Priority, WrappedAsyncQueue(queue)) }
        val queues = Queues(infos)
        Assert.assertEquals(3, queues.size())
        Assert.assertEquals(6, queues.prioritized.size)
        Assert.assertEquals(queues.prioritized[0].name, "0")
        Assert.assertEquals(queues.prioritized[1].name, "1")
        Assert.assertEquals(queues.prioritized[2].name, "1")
        Assert.assertEquals(queues.prioritized[3].name, "2")
        Assert.assertEquals(queues.prioritized[4].name, "2")
        Assert.assertEquals(queues.prioritized[5].name, "2")

        val pos = queues.next().name.toInt()
        Assert.assertTrue(pos >= 0 && pos < queues.size())
    }
}
