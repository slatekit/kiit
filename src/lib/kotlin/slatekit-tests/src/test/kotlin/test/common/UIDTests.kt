package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.ids.ULIDs
import slatekit.common.ids.UPIDs


class UIDTests {

    @Test
    fun can_create_upid(){
        val id = UPIDs.create("spc")
        Assert.assertTrue(id.value.startsWith("spc:"))
    }


    @Test
    fun can_create_ulid(){
        val upids = ULIDs()
        val id = upids.create("kprmac01")
        Assert.assertTrue(id.value.contains("kprmac01"))
    }
}
