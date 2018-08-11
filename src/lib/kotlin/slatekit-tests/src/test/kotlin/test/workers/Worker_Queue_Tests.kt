package test.workers

import org.junit.Assert
import org.junit.Test

import slatekit.apis.ApiContainer
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.common.*
import slatekit.common.queues.QueueSourceDefault
import slatekit.core.common.AppContext
import slatekit.core.workers.*
import slatekit.core.workers.core.Priority
import slatekit.core.workers.core.QueueInfo
import slatekit.integration.workers.WorkerWithQueuesApi
import test.setup.SampleTypes2Api
import test.setup.WorkerSampleApi

class Worker_Queue_Tests {


    @Test
    fun can_load_queues_basic(){

        val infos = (0..2).map { it -> QueueInfo( it.toString(), Priority.Low,  QueueSourceDefault() ) }
        val queues = Queues(infos)
        Assert.assertEquals(3, queues.size())
        Assert.assertEquals(queues.get(0)?.name, "0")
        Assert.assertEquals(queues.get(1)?.name, "1")
        Assert.assertEquals(queues.get(2)?.name, "2")
        Assert.assertEquals(queues.get("0")?.name, "0")
        Assert.assertEquals(queues.get("1")?.name, "1")
        Assert.assertEquals(queues.get("2")?.name, "2")
    }


    @Test
    fun can_load_queues_prioritized(){

        val infos = (0..2).map { it -> QueueInfo( it.toString(), Priority.convert(it + 1) as Priority,  QueueSourceDefault() ) }
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
        Assert.assertTrue( pos >= 0 && pos < queues.size())
    }
}
