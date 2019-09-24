package slatekit.tests


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import slatekit.common.Random
import java.util.concurrent.atomic.AtomicInteger

class Manager {

    suspend fun run(workerCount: Int) {
        val channel = Channel<Int>(Channel.RENDEZVOUS)
        GlobalScope.launch {
            (1..10).forEach {
                channel.send(it)
                println("sent : $it")
            }
        }
        val workers = (0..workerCount).map { Worker(it.toString(), channel) }
        workers.forEach {
            GlobalScope.launch {
                it.process()
            }
        }
        Thread.sleep(20000)
        println("done")
    }
}


class Worker<T>(val name: String, val channel: ReceiveChannel<T>) {

    suspend fun process() {
        for (item in channel) {
            val millis = Random.digitsN(4)
            delay(millis)
            println("Worker: $name - processing $item after $millis")
        }
    }
}


data class Job(val id:String, val task:String)
data class JobMap<T>(val name:String, val channel:Channel<T>, val worker:JobWorker<T>)

class JobManager {
    val requests = Channel<String>()
    val counter = AtomicInteger(0)
    lateinit var lookup:Map<String, JobMap<Job>>

    suspend fun run(workerCount: Int) {
        val mappings = (1..workerCount).map {
            val name = it.toString()
            val channel = Channel<Job>(Channel.UNLIMITED)
            val worker = JobWorker<Job>(name) { requests.send(name) }
            JobMap(name, channel, worker)
        }
        lookup = mappings.map { it.name to it }.toMap()

        // Listen to requests
        GlobalScope.launch {
            listen()
        }

        // Send off 1st items
        mappings.forEachIndexed { index, item -> process(item) }
        Thread.sleep(30000)
    }


    /**
     * Listens to incoming requests ( name of worker )
     */
    suspend fun listen(){
        for(name in requests){
            val item = lookup[name]
            item?.let {
                process(it)
            }
        }
    }


    val LIMIT = 20
    suspend fun process(jobMap:JobMap<Job>){
        val current = counter.get()
        if(current < LIMIT ) {
            val job = getJob()
            GlobalScope.launch {
                send(job, jobMap.channel, jobMap.worker)
                println("Done sending job to worker ${jobMap.name}")
            }
            GlobalScope.launch {
                jobMap.worker.process(job)
                println("Done worker request ${jobMap.name}")
            }
        }
    }


    suspend fun send(job:Job, channel: SendChannel<Job>, worker:JobWorker<Job>){
        channel.send(job)
    }


    fun getJob():Job {
        val id = counter.incrementAndGet()
        val job = Job(id.toString(), Random.alpha6())
        return job
    }
}


class JobWorker<T>(val name: String, val request:suspend () -> Unit) {

    suspend fun process(job:Job) {
        //val job = responses.receive()
        val millis = Random.digitsN(3)
        println("Worker: $name - processing $job for $millis")
        delay(millis)
        println("Worker: $name - completed job, requesting more")
        request()
    }
}