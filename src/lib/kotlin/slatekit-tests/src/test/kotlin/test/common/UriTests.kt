package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.io.Alias
import slatekit.common.io.Uri
import slatekit.common.io.Uris
import slatekit.common.naming.*


class UriTests {

    val lookups = mapOf(
            "/" to "/",
            "~" to "/Users/batman",
            "." to "/Users/batman/slatekit"
    )


    fun ensure(raw:String, alias:Alias, child:String, full:String){
        val uri = Uris.parse(raw, lookups)
        Assert.assertEquals(raw.trim(), uri.raw)
        Assert.assertEquals(alias, uri.root)
        Assert.assertEquals(child, uri.path)
        Assert.assertEquals(full , uri.full)
    }

    fun ensure(uri:Uri, alias:Alias, child:String, full:String){
        Assert.assertEquals(alias, uri.root)
        Assert.assertEquals(child, uri.path)
        Assert.assertEquals(full , uri.full)
    }


    @Test fun can_parse_alias() {
        ensure("abs://dev/tmp/out.txt", Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure("usr://dev/tmp/out.txt", Alias.Usr, "dev/tmp/out.txt", "/Users/batman/dev/tmp/out.txt")
        ensure("cur://dev/tmp/out.txt", Alias.Cur, "dev/tmp/out.txt", "/Users/batman/slatekit/dev/tmp/out.txt")
        ensure("cfg://dev/tmp/out.txt", Alias.Cfg, "dev/tmp/out.txt", "/Users/batman/slatekit/conf/dev/tmp/out.txt")
        ensure("rel://dev/tmp/out.txt", Alias.Rel, "dev/tmp/out.txt", "/Users/batman/dev/tmp/out.txt")
    }

    @Test fun can_parse_variations() {
        ensure("abs://dev/tmp/out.txt", Alias.Abs    , "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure("abs://dev\\tmp\\out.txt", Alias.Abs  , "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure(" abs://dev/tmp/out.txt ", Alias.Abs  , "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure("abs:///dev/tmp/out.txt", Alias.Abs   , "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure("abs://\\dev/tmp/out.txt", Alias.Abs  , "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure(" abs://dev\\tmp\\out.txt ", Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
    }

    @Test fun can_build_uri() {
        ensure(Uri.abs("dev/tmp/out.txt", lookups), Alias.Abs, "dev/tmp/out.txt", "/dev/tmp/out.txt")
        ensure(Uri.usr("dev/tmp/out.txt", lookups), Alias.Usr, "dev/tmp/out.txt", "/Users/batman/dev/tmp/out.txt")
        ensure(Uri.cur("dev/tmp/out.txt", lookups), Alias.Cur, "dev/tmp/out.txt", "/Users/batman/slatekit/dev/tmp/out.txt")
        ensure(Uri.cfg("dev/tmp/out.txt", lookups), Alias.Cfg, "dev/tmp/out.txt", "/Users/batman/slatekit/conf/dev/tmp/out.txt")
        ensure(Uri.rel("dev/tmp/out.txt", lookups), Alias.Rel, "dev/tmp/out.txt", "/Users/batman/dev/tmp/out.txt")
    }
}
