package kiit

import kiit.common.Identity
import kiit.jobs.Jobs
import kiit.jobs.Manager
import kiit.jobs.WResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel


class SampleJobs {

    suspend fun task(parent: String, name: String, delay: Long, error:Boolean = false): String  {
        println("starting parent=$parent, name=$name, thread=${Thread.currentThread().name}")
        // Simulate non-block call ( http, file )
        delay(delay)
        if(error) {
            throw Exception("Testing error from parent=$parent, name=$name, thread=${Thread.currentThread().name}")
        }
        println("----done parent=$parent, name=$name, thread=${Thread.currentThread().name}")
        return "Done $name"
    }



    fun runExampleCoroutines1() {
        runBlocking {
            launch {
                task("file", "read file 1", 2000)
                task("http", "load health", 1000)
            }
        }
        println("done")
    }


    suspend fun runExampleCoroutines2() {
        coroutineScope {
            task("file", "read file 1", 2000)
            task("http", "load health", 1000)
        }
        println("done")
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun runExampleDispatcher() {
        // Option 1: Use provided ones ( cpu + io - network )
        val scopeCPU = CoroutineScope(Dispatchers.Default)
        val scopeNIO = CoroutineScope(Dispatchers.IO)

        // Option 2: Build new scope using IO scope
        val scopeDB = CoroutineScope(Dispatchers.IO.limitedParallelism(parallelism = 6))

        // Option 3: Create your own
        val scopeCron = CoroutineScope(java.util.concurrent.Executors.newFixedThreadPool(2).asCoroutineDispatcher())

        // Shutdown Android page/Server application
        scopeCPU.cancel()
        scopeNIO.cancel()
        scopeCron.cancel()
        scopeDB.cancel()
    }


    fun runExampleScope() {
        val scopeCPU = CoroutineScope(Dispatchers.Default)
        val scopeNIO = CoroutineScope(Dispatchers.IO)

        runBlocking {
            launch {
                val pj1 = scopeCPU.launch {
                    println("parent 1 thread = ${Thread.currentThread().name}")
                    val c1 = launch { task("p1", "calculate_1", 4000) }
                    val c2 = launch { task("p1", "calculate_2", 3000) }
                    delay(2000)
                    c2.cancel()
                }
                val pj2 = scopeNIO.launch {
                    println("parent 2 thread = ${Thread.currentThread().name}")
                    val c3 = launch { task("p2", "api_call_3", 2000) }
                    val c4 = launch { task("p2", "api_call_4", 1000) }
                }
                delay(500)
                // Can cancel parent p1 coroutine
                // p1.cancel()

                // Only needed in example
                pj1.join()
                pj2.join()
            }
        }
        println("done")
    }

    fun runExampleBuilders() {
        runBlocking {
            launch {
                // These will run sequentially
                val c1 = task("p1", "c1", 4000)
                val c2 = task("p1", "c2", 3000)

                // These will run concurrently, but in a fire/forget
                // Handles c3, c4 are only to cancel ( not to get the result )
                val c3 = launch { task("p1", "c1", 4000) }
                val c4 = launch { task("p1", "c2", 3000) }

                // These will run concurrently (on 2 different threads)
                // Handles c5, c6 are Deferred<T> so we can wait for their results
                val c5 = async { task("p1", "c2", 4000) }
                val c6 = async { task("p1", "c3", 3000) }

                // Wait for both to complete
                val results = awaitAll(c5, c6)
                println(results)
            }
        }
    }


    fun runExampleRelationship() {
        val parent1 = CoroutineScope(Dispatchers.IO)
        val parent2 = CoroutineScope(Dispatchers.Default)

        runBlocking {
            launch {
                val pj1 = parent1.launch {
                    println("parent 1 thread = ${Thread.currentThread().name}")
                    val c1 = launch { task("p1", "calculate_1", 4000) }
                    val c2 = launch { task("p1", "calculate_2", 3000) }
                    delay(2000)
                    c2.cancel()
                }
                val pj2 = parent2.launch {
                    println("parent 2 thread = ${Thread.currentThread().name}")
                    val c3 = launch { task("p2", "api_call_3", 2000) }
                    val c4 = launch { task("p2", "api_call_4", 1000) }
                }
                delay(500)
                //pj1.cancel()
                pj1.join()
                pj2.join()
            }
            //delay(800)
        }
        println("done")
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun runExampleRelationships() {
        // CPU: Thread Pool - cpu-thread-1, cpu-thread-2
        val scopeCPU = CoroutineScope(Dispatchers.Default)

        // NIO: Thread Pool - nio-thread-3, nio-thread-4, nio-thread-5, nio-thread-6
        val scopeNIO = CoroutineScope(Dispatchers.IO)

        // CRON: Thread Pool - cron-thread-7
        val scopeCron = CoroutineScope(Dispatchers.IO.limitedParallelism(parallelism = 1))

        runBlocking {
            val j = coroutineScope {

                // CPU
                // ScopeCPU: Scope + Queue + 2 Threads to process queue
                //  - coroutine: "compute-1" -> processed by cpu-thread-2
                //  - coroutine: "compute-2" -> processed by cpu-thread-1
                //  - coroutine: "compute-2" -> processed by cpu-thread-2
                scopeCPU.launch {
                    task("p1", "compute-1", 1000)
                    task("p1", "compute-2", 1000)
                    task("p1", "compute-2", 1000)
                }

                // NIO
                // ScopeNIO: Scope + Queue + 4 Threads to process queue
                //  - coroutine: "api_call-1" -> processed by nio-thread-6
                //  - coroutine: "api_call-2" -> processed by nio-thread-3
                //  - coroutine: "api_call-3" -> processed by nio-thread-5
                //  - coroutine: "api_call-4" -> processed by nio-thread-4
                //  - coroutine: "api_call-5" -> processed by nio-thread-3
                //  - coroutine: "api_call-6" -> processed by nio-thread-6
                scopeNIO.launch {
                    task("p2", "api_call-1", 200)
                    task("p2", "api_call-2", 400)
                    task("p2", "api_call-3", 600)
                    task("p2", "api_call-4", 800)
                    task("p2", "api_call-5", 200)
                    task("p2", "api_call-6", 400)
                }

                // CRON
                // ScopeCRON: Scope + Queue + 1 Thread to process queue
                //  - coroutine: "cronjob-1" -> processed by cron-thread-7
                //  - coroutine: "cronjob-2" -> processed by cron-thread-7
                scopeCron.launch {
                    task("p3", "cronjob-1", 1000)
                    task("p3", "cronjob-2", 1000)
                }
            }
            // Needed for main/example
            j.join()
        }
    }


    fun runExampleErrorHandling() {
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("Global exception handler, $coroutineContext $throwable")
        }

        val scopeNIO1 = CoroutineScope(Dispatchers.IO)
        val scopeNIO2 = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val scopeNIO3 = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)

        // Option 1: Explicit handle
        runBlocking {
            // Option 1: No exception handler or supervisor job
            val j1 = coroutineScope {
                scopeNIO1.launch { task("p1", "calculate_1", 3000) }
                scopeNIO1.launch { task("p1", "calculate_2", 2000, true) }
                scopeNIO1.launch { task("p1", "calculate_3", 4000) }
            }
            // Option 2: Explicit exception handler ( prevents propagation )
            val j2 = coroutineScope {
                scopeNIO1.launch { handle { task("p2", "calculate_4", 3000) } }
                scopeNIO1.launch { handle { task("p2", "calculate_5", 2000, true) } }
                scopeNIO1.launch { handle { task("p2", "calculate_6", 4000) } }
            }
            // Option 3: Supervisor job but doesn't do anything with exception
            val j3 = coroutineScope {
                scopeNIO2.launch { task("p3", "calculate_7", 3000) }
                scopeNIO2.launch { task("p3", "calculate_8", 2000, true) }
                scopeNIO2.launch { task("p3", "calculate_9", 4000) }
            }
            // Option 4: Supervisor job but doesn't do anything with exception
            val j4 = coroutineScope {
                scopeNIO3.launch { task("p4", "calculate_10", 3000) }
                scopeNIO3.launch { task("p4", "calculate_11", 2000, true) }
                scopeNIO3.launch { task("p4", "calculate_12", 4000) }
            }
            j1.join()
            j2.join()
            j3.join()
            j4.join()
        }
        println("done")
    }

    suspend fun handle(op: suspend () -> Any): Any? {
        return try {
            val result = op()
            result
        } catch(ex:Exception) {
            println("Error: ${ex.message}")
            null
        }
    }


    fun runExampleCooperate() {
        // CPU: Thread Pool - cpu-thread-1, cpu-thread-2
        val scopeCPU = CoroutineScope(Dispatchers.Default)

        // NIO: Thread Pool - nio-thread-3, nio-thread-4, nio-thread-5, nio-thread-6
        val scopeNIO = CoroutineScope(Dispatchers.IO)

        runBlocking {
            val j = coroutineScope {
                scopeCPU.launch {
                    repeat(2) {
                        println("worker-1: cpu task ${it}, thread=${Thread.currentThread().name}")
                        withContext(scopeNIO.coroutineContext) {
                            println("worker-1: nio task for ${it}, thread=${Thread.currentThread().name}")
                        }
                        //yield() // Allow other coroutines to run
                        //task("p1", "worker-1 : task-${it}", 200)
                    }
                }

                scopeNIO.launch {
                    repeat(2) {
                        println("worker-2: nio task ${it}, thread=${Thread.currentThread().name}")
                        withContext(scopeCPU.coroutineContext) {
                            println("worker-1: cpu task for ${it}, thread=${Thread.currentThread().name}")
                        }
                        //yield() // Allow other coroutines to run
                        //task("p1", "worker-2 : task-${it}", 200)
                    }
                }
            }
            j.join()
        }
    }
}


class Tester1 {
    fun run2() {
        val channel = Channel<Int>(capacity = 10) // Create a buffered channel

        // Producer coroutine
        GlobalScope.launch {
            for (i in 1..5) {
                channel.send(i) // Send data to the channel
                println("Produced: $i")
                delay(100)
            }
            channel.close() // Close the channel when done
        }

        // Consumer coroutine
        GlobalScope.launch {
            for (item in channel) { // Iterate over the channel
                println("Consumed: $item")
                delay(200)
            }
            println("Done consuming")
        }

    }

    fun run() {
        runBlocking {
            val id = Identity.job("kiit", "tests", "job1")
            val jobs = Jobs(listOf(), listOf(Manager(id, ::task1)))
            val j = Jobs.scope.launch {
                jobs.get(id.name)?.let { jb ->
                    jb.start()
                    jb.work()
                    jb.load()
                }
            }
            j.join()
        }
    }

    fun task1(): WResult {
        println("task 1")
        return WResult.More
    }
}


class SampleWorker()