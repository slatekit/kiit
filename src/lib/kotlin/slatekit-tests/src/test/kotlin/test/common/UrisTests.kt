package test.common

import org.junit.Assert
import org.junit.Test
import kiit.common.io.Alias
import kiit.common.io.Files
import kiit.common.io.Uri
import kiit.common.io.Uris
import java.io.File


class UrisTests {

    val lookups = mapOf(
            "/" to "/",
            "~" to "/Users/batman",
            "." to "/Users/batman/slatekit"
    )

    fun ensure(raw: String, alias: Alias, child: String, full: String) {
        val uri = Uris.parse(raw, lookups)
        Assert.assertEquals(raw.trim(), uri.raw)
        Assert.assertEquals(alias, uri.root)
        Assert.assertEquals(child, uri.path)
        Assert.assertEquals(full, uri.full)
    }

    fun ensure(uri: Uri, alias: Alias, child: String, full: String) {
        Assert.assertEquals(alias, uri.root)
        Assert.assertEquals(child, uri.path)
        Assert.assertEquals(full, uri.full)
    }

    fun ensureCombine(uri: Uri, alias: Alias, base: String, other:String, full: String) {
        val uriFinal = uri.combine(other)
        val combined = File(base, other).toString()
        Assert.assertEquals(alias, uriFinal.root)
        Assert.assertEquals(combined, uriFinal.path)
        Assert.assertEquals(full, uriFinal.full)
    }

    @Test
    fun can_load_cfg_dir(){
        val cfg = Files.cfgDir
        val expect = "/Users/kishorereddy/git/slatekit/slatekit/src/lib/kotlin/slatekit-tests/conf"
        Assert.assertTrue(cfg.startsWith("/Users/"))
        Assert.assertTrue(cfg.endsWith("/src/lib/kotlin/slatekit-tests/conf"))
    }

    @Test
    fun can_parse_alias() {
        ensure("abs://dev/tmp/out.txt", Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure("usr://dev/tmp/out.txt", Alias.Usr, "dev/tmp/out.txt", "/Users/batman/dev/tmp/out.txt")
        ensure("cur://dev/tmp/out.txt", Alias.Cur, "dev/tmp/out.txt", "/Users/batman/slatekit/dev/tmp/out.txt")
        ensure("cfg://dev/tmp/out.txt", Alias.Cfg, "dev/tmp/out.txt", "/Users/batman/slatekit/conf/dev/tmp/out.txt")
        ensure("rel://dev/tmp/out.txt", Alias.Rel, "dev/tmp/out.txt", "/Users/batman/dev/tmp/out.txt")
    }

    @Test
    fun can_parse_variations() {
        ensure("abs://dev/tmp/out.txt", Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure("abs://dev\\tmp\\out.txt", Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure(" abs://dev/tmp/out.txt ", Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure("abs:///dev/tmp/out.txt", Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure("abs://\\dev/tmp/out.txt", Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure(" abs://dev\\tmp\\out.txt ", Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
    }

    @Test
    fun can_build_uri_as_file() {
        ensure(Uri.abs("dev/tmp/out.txt", lookups), Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure(Uri.usr("dev/tmp/out.txt", lookups), Alias.Usr, "dev/tmp/out.txt", "/Users/batman/dev/tmp/out.txt")
        ensure(Uri.cur("dev/tmp/out.txt", lookups), Alias.Cur, "dev/tmp/out.txt", "/Users/batman/slatekit/dev/tmp/out.txt")
        ensure(Uri.cfg("dev/tmp/out.txt", lookups), Alias.Cfg, "dev/tmp/out.txt", "/Users/batman/slatekit/conf/dev/tmp/out.txt")
        ensure(Uri.rel("dev/tmp/out.txt", lookups), Alias.Rel, "dev/tmp/out.txt", "/Users/batman/dev/tmp/out.txt")
    }

    @Test
    fun can_build_uri_as_dir() {
        ensure(Uri.abs("dev/tmp", lookups), Alias.Abs, "dev/tmp", "/dev/tmp")
        ensure(Uri.usr("dev/tmp", lookups), Alias.Usr, "dev/tmp", "/Users/batman/dev/tmp")
        ensure(Uri.cur("dev/tmp", lookups), Alias.Cur, "dev/tmp", "/Users/batman/slatekit/dev/tmp")
        ensure(Uri.cfg("dev/tmp", lookups), Alias.Cfg, "dev/tmp", "/Users/batman/slatekit/conf/dev/tmp")
        ensure(Uri.rel("dev/tmp", lookups), Alias.Rel, "dev/tmp", "/Users/batman/dev/tmp")
    }

    @Test
    fun can_build_uri_as_dir_combined() {
        ensureCombine(Uri.abs("dev/tmp", lookups), Alias.Abs, "dev/tmp", "cache", "/dev/tmp/cache")
        ensureCombine(Uri.usr("dev/tmp", lookups), Alias.Usr, "dev/tmp", "cache", "/Users/batman/dev/tmp/cache")
        ensureCombine(Uri.cur("dev/tmp", lookups), Alias.Cur, "dev/tmp", "cache", "/Users/batman/slatekit/dev/tmp/cache")
        ensureCombine(Uri.cfg("dev/tmp", lookups), Alias.Cfg, "dev/tmp", "cache", "/Users/batman/slatekit/conf/dev/tmp/cache")
        ensureCombine(Uri.rel("dev/tmp", lookups), Alias.Rel, "dev/tmp", "cache", "/Users/batman/dev/tmp/cache")
    }
}
