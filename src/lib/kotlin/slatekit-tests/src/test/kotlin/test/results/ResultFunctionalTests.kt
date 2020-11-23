package test.results

import org.junit.Assert
import org.junit.Test
import slatekit.results.*
import slatekit.results.Codes
import slatekit.results.builders.Outcomes
import slatekit.results.builders.Outcomes.success
import slatekit.results.builders.Outcomes.errored

/**
 * Tests Operations on the Result class which include:
 * 1. map
 * 2. flatMap
 * 3. onSuccess
 * 4. onFailure
 * 5. fold
 *
 * The tests check the operations above on both:
 * 1. Success branch
 * 2. Failure branch
 */
class ResultFunctionalTests {


    @Test
    fun can_get_or_else() {
        val result1 = success("peter parker")
        Assert.assertEquals("peter parker", result1.getOrElse { "" })
    }


    @Test
    fun can_get_or_null() {
        val result1 = errored<String>("name unknown")
        Assert.assertEquals(null, result1.getOrNull())
    }


    @Test
    fun can_get_inner() {
        val result1:Result<Result<String, Err>, Err> = Success(Success("peter parker"))
        val i1 = result1.inner()
        Assert.assertEquals("peter parker", i1.getOrNull())
    }


    @Test
    fun can_check_exists() {
        val result1 = success("peter parker")
        Assert.assertTrue(result1.exists { it == "peter parker" })
    }


    @Test
    fun can_check_contains() {
        val result1 = success("peter parker")
        Assert.assertEquals(true, result1.contains("peter parker"))
    }


    @Test
    fun can_map_branch_success() {
        val result1 = success("peter parker")
        val result2 = result1.map { name -> "$name : spider-man" }
        Assert.assertEquals("peter parker : spider-man", result2.getOrElse { "" })
    }


    @Test
    fun can_flatMap_branch_success() {
        val result1 = success("peter parker")
        val result2 = result1.flatMap { name -> success("$name : spider-man") }
        Assert.assertEquals("peter parker : spider-man", result2.getOrElse { "" })
    }


    @Test
    fun can_flatMap_branch_success_via_then() {
        val result1 = success("peter parker")
        val result2 = result1.then { name -> success("$name : spider-man") }
        Assert.assertEquals("peter parker : spider-man", result2.getOrElse { "" })
    }


    @Test
    fun can_handle_branch_success() {
        val result1 = success("peter parker")
        val result2 = result1.map { name -> "$name : spider-man" }
        result2.onSuccess {
            Assert.assertEquals("peter parker : spider-man", it)
        }
    }


    @Test
    fun can_map_branch_failure() {
        val result1 = errored<String>("name unknown")
        val result2 = result1.map { name -> "$name : spider-man" }
        Assert.assertEquals(false, result2.success)
        Assert.assertEquals(Codes.ERRORED, result2.status)
        Assert.assertEquals(Codes.ERRORED.code, result2.code)
        Assert.assertEquals(Codes.ERRORED.desc, result2.msg)
        Assert.assertEquals("??", result2.getOrElse { "??" })
    }


    @Test
    fun can_flatMap_branch_failure() {
        val result1 = errored<String>("name unknown")
        val result2 = result1.flatMap { name -> success("$name : spider-man") }
        Assert.assertEquals(false, result2.success)
        Assert.assertEquals(Codes.ERRORED, result2.status)
        Assert.assertEquals(Codes.ERRORED.code, result2.code)
        Assert.assertEquals(Codes.ERRORED.desc, result2.msg)
        Assert.assertEquals("??", result2.getOrElse { "??" })
    }


    @Test
    fun can_handle_branch_failure() {
        val result1 = errored<String>("name unknown")
        val result2 = result1.map { name -> "$name : spider-man" }
        Assert.assertEquals(false, result2.success)
        Assert.assertEquals(Codes.ERRORED, result2.status)
        Assert.assertEquals(Codes.ERRORED.code, result2.code)
        Assert.assertEquals(Codes.ERRORED.desc, result2.msg)
        Assert.assertEquals("??", result2.getOrElse { "??" })
    }


    @Test
    fun can_convert_error_via_map() {
        val result1 = errored<String>("name unknown")
        val result2 = result1.mapError { _ -> 0 }
        Assert.assertEquals(false, result2.success)
        Assert.assertEquals(Codes.ERRORED, result2.status)
        Assert.assertEquals(Codes.ERRORED.code, result2.code)
        Assert.assertEquals(Codes.ERRORED.desc, result2.msg)
        result2.onFailure {
            Assert.assertEquals(0, it)
        }
    }


    @Test
    fun can_convert_error_via_flatMap() {
        val result1 = errored<String>("name unknown")
        val result2 = result1.flatMapError { it -> Failure(0) }
        Assert.assertEquals(false, result2.success)
        Assert.assertEquals(Codes.ERRORED, result2.status)
        Assert.assertEquals(Codes.ERRORED.code, result2.code)
        Assert.assertEquals(Codes.ERRORED.desc, result2.msg)
        result2.onFailure {
            Assert.assertEquals(0, it)
        }
    }


    @Test
    fun can_transform() {
        val r1 = success("peter parker")

        // Default to spider-man
        val r2 = r1.transform(
            { name -> Success("$name : spider-man") },
            { err -> Failure("a marvel character") }
        )

        Assert.assertEquals(true, r2.success)
        Assert.assertEquals(Codes.SUCCESS, r2.status)
        Assert.assertEquals(Codes.SUCCESS.code, r2.code)
        Assert.assertEquals(Codes.SUCCESS.desc, r2.msg)
        r2.onSuccess {
            Assert.assertEquals("peter parker : spider-man", it )
        }
    }


    @Test
    fun can_transform_with_fold() {
        val result1 = success("peter parker")

        // Default to spider-man
        val name = result1.fold(
            { name -> "$name : spider-man" },
            { err -> "a marvel character" }
        )
        Assert.assertEquals("peter parker : spider-man", name)
    }


    @Test
    fun can_chain() {
        var successValue = ""

        val result1 = Outcomes.of { "1" }
        val result2 =
            result1.map { it.toInt() }
                .onSuccess { successValue = "converted to int: $it" }
                .flatMap { Success(it + 1) }

        Assert.assertEquals("converted to int: 1", successValue)
        Assert.assertTrue(result2.contains(2))

        val finalValue = result2.fold({ "final value: $it" }, { "error : $it" })
        Assert.assertEquals("final value: 2", finalValue)
    }
}