package test.workers

import org.junit.Test
import slatekit.common.results.ResultFuncs.success
import slatekit.common.status.*
import slatekit.core.workers.*

// https://stackoverflow.com/questions/2233561/producer-consumer-work-queues
// http://www.vogella.com/tutorials/JavaConcurrency/article.html





class Worker_Group_Tests {

    fun buildSampleGroup():Group {
        val group = Group("default", System())
        group.add("emailer", Worker<String>( "account emailer", "sends registration email", callback = { success("sent email") } ))
        group.add("inviter", Worker<String>( "invite sender", "sends invitations to app", callback = { success("stored invite request") } ))
        return group
    }


    @Test
    fun can_setup_groups(){
        val group = buildSampleGroup()
        assert(group.size == 2)
        assert(group.contains("emailer"))
        assert(group.contains("inviter"))
        assert(group[0]!!.metadata.about.name == "account emailer")
        assert(group[0]!!.metadata.about.desc == "sends registration email")
    }


    @Test
    fun can_start_group(){
        val group = buildSampleGroup()

        group.start()
        assertStatus(group, RunStateIdle)
    }


    @Test
    fun can_pause_group(){
        val group = buildSampleGroup()

        group.pause()
        assertStatus(group, RunStatePaused)
    }


    @Test
    fun can_stop_group(){
        val group = buildSampleGroup()

        group.stop()
        assertStatus(group, RunStateStopped)
    }


    @Test
    fun can_resume_group(){
        val group = buildSampleGroup()

        group.resume()
        assertStatus(group, RunStateIdle)
    }


    fun assertStatus(group:Group, state:RunState){
        group.all.forEach { it.state().value == state.value }
    }
}