package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.utils.paged.Pager


class PagerTests {

    private fun <T> ensure(pager: Pager<T>, pos:Int, value:T ){

        Assert.assertEquals(value, pager.current())
        Assert.assertEquals(pos, pager.pos())
    }


    @Test fun can_initialize() {
        val pager = Pager(listOf("a", "b", "c"), true)
        Assert.assertTrue(pager.circular)
        Assert.assertEquals("a", pager.current())
        Assert.assertEquals(0, pager.pos())
        Assert.assertEquals(0, pager.start)
        Assert.assertEquals(2, pager.end)
        Assert.assertEquals(3, pager.size)
    }


    @Test fun can_navigate_via_move() {
        val pager = Pager(listOf("a", "b", "c"), false)
        pager.move(1)
        ensure(pager, 1, "b")
        pager.moveFirst()
        ensure(pager, 0, "a")
        pager.moveLast()
        ensure(pager, 2, "c")
    }


    @Test fun can_navigate_non_circular_via_next() {
        val pager = Pager(listOf("a", "b", "c"), false)
        Assert.assertEquals(pager.next(), "b")
        Assert.assertEquals(pager.next(), "c")
        Assert.assertEquals(pager.next(), "c")
    }


    @Test fun can_navigate_non_circular_via_back() {
        val pager = Pager(listOf("a", "b", "c"), false)
        pager.move(2)
        Assert.assertEquals(pager.back(), "b")
        Assert.assertEquals(pager.back(), "a")
        Assert.assertEquals(pager.back(), "a")
    }


    @Test fun can_navigate_circular_via_next() {
        val pager = Pager(listOf("a", "b", "c"), true)
        Assert.assertEquals(pager.next(), "b")
        Assert.assertEquals(pager.next(), "c")
        Assert.assertEquals(pager.next(), "a")
    }


    @Test fun can_navigate_circular_via_back() {
        val pager = Pager(listOf("a", "b", "c"), true)
        pager.move(2)
        Assert.assertEquals(pager.back(), "b")
        Assert.assertEquals(pager.back(), "a")
        Assert.assertEquals(pager.back(), "c")
    }


    @Test fun can_determine_moving_next_non_circular() {
        val pager = Pager(listOf("a", "b", "c"), false)
        Assert.assertTrue(pager.canMoveNext())
        pager.next()
        Assert.assertTrue(pager.canMoveNext())
        pager.next()
        Assert.assertFalse(pager.canMoveNext())
    }


    @Test fun can_determine_moving_back_non_circular() {
        val pager = Pager(listOf("a", "b", "c"), false)
        pager.move(2)
        Assert.assertTrue(pager.canMoveBack())
        pager.back()
        Assert.assertTrue(pager.canMoveBack())
        pager.back()
        Assert.assertFalse(pager.canMoveBack())
    }


    @Test fun can_determine_moving_next_circular() {
        val pager = Pager(listOf("a", "b", "c"), true)
        Assert.assertTrue(pager.canMoveNext())
        pager.next()
        Assert.assertTrue(pager.canMoveNext())
        pager.next()
        Assert.assertTrue(pager.canMoveNext())
    }


    @Test fun can_determine_moving_back_circular() {
        val pager = Pager(listOf("a", "b", "c"), true)
        pager.move(2)
        Assert.assertTrue(pager.canMoveBack())
        pager.back()
        Assert.assertTrue(pager.canMoveBack())
        pager.back()
        Assert.assertTrue(pager.canMoveBack())
    }
}
