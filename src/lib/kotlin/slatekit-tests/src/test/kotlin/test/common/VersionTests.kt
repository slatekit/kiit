package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.utils.Version


class VersionTests {

    private fun ensure(version: Version, major:Int, minor:Int, patch:Int, build:Int ){
        Assert.assertEquals(version.major, major)
        Assert.assertEquals(version.minor, minor)
        Assert.assertEquals(version.patch, patch)
        Assert.assertEquals(version.build, build)
    }


    @Test fun can_initialize() {
        val version1 = Version(1)
        ensure(version1, 1, 0, 0, 0)


        val version2 = Version(1, 2)
        ensure(version2, 1, 2, 0, 0)


        val version3 = Version(1, 2, 3)
        ensure(version3, 1, 2, 3, 0)


        val version4 = Version(1, 2, 3, 4)
        ensure(version4, 1, 2, 3, 4)
    }


    @Test fun can_compare_less_than() {
        val version1 = Version(1, 2, 3, 4)
        val version2 = Version(1, 2, 3, 5)
        Assert.assertEquals(version1.compareTo(version2), -1)
    }


    @Test fun can_compare_more_than() {
        val version1 = Version(1, 2, 3, 4)
        val version2 = Version(1, 2, 3, 3)
        Assert.assertEquals(version1.compareTo(version2), 1)
    }


    @Test fun can_compare_equal() {
        val version1 = Version(1, 2, 3, 4)
        val version2 = Version(1, 2, 3, 4)
        Assert.assertEquals(version1.compareTo(version2), 0)
    }


    @Test fun can_check_empty() {
        val version = Version(0, 0, 0, 0)
        Assert.assertTrue(version.isEmpty())
    }
}
