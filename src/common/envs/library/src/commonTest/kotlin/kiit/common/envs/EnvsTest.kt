package kiit.common.envs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EnvsTest {

    @Test
    fun testEnvModeParse() {
        assertEquals(EnvMode.Dev, EnvMode.parse("dev"))
        assertEquals(EnvMode.Pro, EnvMode.parse("pro"))
        assertEquals(EnvMode.Qat, EnvMode.parse("qat"))
        assertEquals(EnvMode.Uat, EnvMode.parse("uat"))
        assertEquals(EnvMode.Dis, EnvMode.parse("dis"))
    }

    @Test
    fun testEnvModeParseOther() {
        val other = EnvMode.parse("custom")
        assertTrue(other is EnvMode.Other)
        assertEquals("custom", other.name)
    }

    @Test
    fun testEnvParse() {
        val env = Env.parse("qa1:qat")
        assertEquals("qa1", env.name)
        assertEquals(EnvMode.Qat, env.mode)
        assertEquals("qa1:qat", env.key)
    }

    @Test
    fun testEnvParseNameOnly() {
        val env = Env.parse("dev")
        assertEquals("dev", env.name)
        assertEquals(EnvMode.Dev, env.mode)
    }

    @Test
    fun testEnvSupport() {
        val devEnv = Env("dev", EnvMode.Dev)
        assertTrue(devEnv.isDev)
        assertTrue(!devEnv.isPro)

        val proEnv = Env("pro", EnvMode.Pro)
        assertTrue(proEnv.isPro)
        assertTrue(!proEnv.isDev)
    }

    @Test
    fun testEnvsDefaults() {
        val envs = Envs.defaults()
        assertNotNull(envs)
        assertTrue(envs.all.isNotEmpty())
        assertEquals(5, envs.all.size)
    }

    @Test
    fun testEnvsSelect() {
        val envs = Envs.defaults().select("pro")
        assertEquals("pro", envs.current.name)
        assertTrue(envs.isPro)
    }

    @Test
    fun testEnvsIsValid() {
        val envs = Envs.defaults()
        assertTrue(envs.isValid("dev"))
        assertTrue(envs.isValid("pro"))
        assertTrue(!envs.isValid("nonexistent"))
    }

    @Test
    fun testEnvsGet() {
        val envs = Envs.defaults()
        assertNotNull(envs.get("dev"))
        assertNull(envs.get("nonexistent"))
    }
}
