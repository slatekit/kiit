package test.common

import org.junit.Assert
import org.junit.Test
import kiit.common.ids.ULIDs
import kiit.common.ids.UPIDs
import kiit.common.ids.UUIDs


class UPIDTests {

    @Test
    fun upid_can_create(){
        val id = UPIDs.create("spc")
        Assert.assertTrue(id.value.startsWith("spc:"))
        Assert.assertEquals(id.value, id.toString())
    }

    @Test
    fun upid_can_create_empty(){
        val id = UPIDs.create()
        Assert.assertTrue(id.value.startsWith(":"))
        Assert.assertEquals(id.value, id.toString())
    }

    @Test
    fun upid_can_validate(){
        Assert.assertTrue(UPIDs.isValid("usr:${UUIDs.create().value}"))
        Assert.assertTrue(UPIDs.isValid(":${UUIDs.create().value}"))
        Assert.assertTrue(UPIDs.isValid("${UUIDs.create().value}"))
    }

    @Test
    fun upid_can_parse(){
        val id = UUIDs.create().value
        Assert.assertEquals("usr:$id", UPIDs.parse("usr:$id").value)
        Assert.assertEquals(":$id", UPIDs.parse(":$id").value)
        Assert.assertEquals(":$id", UPIDs.parse("$id").value)
    }
}



class UUIDTests {

    @Test
    fun upid_can_create(){
        val id = UUIDs.create()
        Assert.assertTrue(id.value == id.uuid.toString())
        Assert.assertTrue(id.value == id.toString())
    }

    @Test
    fun upid_can_create_empty(){
        val id = UUIDs.create("")
        Assert.assertTrue(id.value == id.uuid.toString())
        Assert.assertTrue(id.value == id.toString())
    }

    @Test
    fun upid_can_validate(){
        val id = UUIDs.create().value
        Assert.assertTrue(UUIDs.isValid(id))
    }

    @Test
    fun upid_can_parse(){
        val id = UUIDs.create().value
        Assert.assertEquals(id, UUIDs.parse(id).value)
    }
}



class ULIDTests {

    @Test
    fun upid_can_create(){
        val id = ULIDs.create()
        Assert.assertTrue(id.value == "${id.instant}${id.node}${id.random}${id.version}")
        Assert.assertEquals(id.value, id.toString())
    }

    @Test
    fun upid_can_create_empty(){
        val id = ULIDs.create("kprmac01")
        Assert.assertTrue(id.value == "${id.instant}kprmac01${id.random}${id.version}")
        Assert.assertEquals(id.value, id.toString())
    }

    @Test
    fun upid_can_validate(){
        // 175aa15decd62wym53449wvhyjmuw
        val id = ULIDs.create().value
        Assert.assertTrue(ULIDs.isValid(id))
    }

    @Test
    fun upid_can_parse(){
        val id = ULIDs.create().value
        Assert.assertEquals(id, ULIDs.parse(id).value)
    }
}
