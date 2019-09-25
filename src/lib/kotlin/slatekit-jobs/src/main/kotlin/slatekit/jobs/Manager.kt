package slatekit.jobs

import kotlinx.coroutines.channels.Channel


class Manager(val worker: Worker<Task>, val scheduler:Scheduler) {

    /**
     * For communicating requests on the worker
     */
    private val channel =  Channel<WorkAction>()


    suspend fun request(action: WorkAction){
        when (action){
            is WorkAction.NA     -> { }
            is WorkAction.Start  -> channel.send(WorkAction.Start )
            is WorkAction.Pause  -> channel.send(WorkAction.Pause )
            is WorkAction.Stop   -> channel.send(WorkAction.Stop  )
            is WorkAction.Resume -> channel.send(WorkAction.Resume)
            else                 -> error(action)
        }
    }


    private fun error(action: WorkAction){

    }


    private fun recieve(action: WorkAction){
        when (action){
            is WorkAction.NA     -> { }
            is WorkAction.Start  -> start ()
            is WorkAction.Pause  -> pause ()
            is WorkAction.Stop   -> stop  ()
            is WorkAction.Resume -> resume()
            else                 -> error(action)
        }
    }


    private fun start(){
        info("Starting worker")
    }


    private fun pause(){
        info("Pausing worker")
    }


    private fun stop(){
        info("Stopping worker")
    }


    private fun resume(){
        info("Resuming worker")
    }


    private fun info(msg:String){

    }
}