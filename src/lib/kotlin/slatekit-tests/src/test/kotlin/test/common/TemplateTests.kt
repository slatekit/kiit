/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.common

import org.junit.Assert
import org.junit.Test
import kiit.utils.templates.TemplateConstants.TypeSub
import kiit.utils.templates.*
import kiit.utils.templates.TemplateConstants.TypeText
import kiit.common.utils.Random
import kiit.results.Success
import kiit.results.Try

/**
 * Created by kishorereddy on 6/3/17.
 */

class TemplateTests {


    @Test fun can_parse() {
        val p = TemplateParser("Hi @{user.api}.")
        val result = p.parse()
        Assert.assertTrue(result.success)
    }


    @Test fun can_sub_1_only() {
        checkResult ( subs().parse("@{user.home}"), listOf(
            Pair(TypeSub , "user.home"  )
        ))
    }


    @Test fun can_sub_1_at_start() {
        checkResult ( subs().parse("@{user.home}/slatekit/env.conf"), listOf(
            Pair(TypeSub , "user.home"  ),
            Pair(TypeText, "/slatekit/env.conf")
        ))
    }


    @Test fun can_sub_1_in_middle() {
        checkResult ( subs().parse("/slatekit/@{user.home}/env.conf/"), listOf(
            Pair(TypeText, "/slatekit/"),
            Pair(TypeSub , "user.home"  ),
            Pair(TypeText, "/env.conf/")
        ))
    }


    @Test fun can_sub_1_at_end() {
        checkResult ( subs().parse("/slatekit/env.conf/@{user.home}"), listOf(
            Pair(TypeText, "/slatekit/env.conf/"),
            Pair(TypeSub , "user.home"  )
        ))
    }


    @Test fun can_sub_2_at_start() {
        checkResult ( subs().parse("@{user.home}/@{company.id}/env.conf"), listOf(
            Pair(TypeSub  , "user.home"   ),
            Pair(TypeText , "/"           ),
            Pair(TypeSub  , "company.id"  ),
            Pair(TypeText , "/env.conf"   )
        ))
    }


    @Test fun can_sub_2_in_middle() {
        checkResult ( subs().parse("/c/@{user.api}/@{company.id}/env.conf/"), listOf(
            Pair(TypeText , "/c/"        ),
            Pair(TypeSub  , "user.api"  ),
            Pair(TypeText , "/"          ),
            Pair(TypeSub  , "company.id" ),
            Pair(TypeText , "/env.conf/" )
        ))
    }


    @Test fun can_sub_2_at_end() {
        checkResult ( subs().parse("/c:/users/@{user.api}/@{company.id}"), listOf(
            Pair(TypeText, "/c:/users/"),
            Pair(TypeSub  , "user.api"  ),
            Pair(TypeText , "/"  ),
            Pair(TypeSub  , "company.id" )
        ))
    }


    @Test fun can_sub_2_consecutive() {
        checkResult ( subs().parse("@{user.api}@{company.id}"), listOf(
            Pair(TypeSub  , "user.api"  ),
            Pair(TypeSub  , "company.id" )
        ))
    }


    @Test fun can_sub_many_in_template() {
        checkResult ( subs().parse(
                "hi @{user.api}, Welcome to @{company.id}, @{company.id} does abc. " +
                        "visit @{company.url} for more info. regards @{company.support}"
        ), listOf(
            Pair(TypeText , "hi "  ),
            Pair(TypeSub  , "user.api"  ),
            Pair(TypeText , ", Welcome to "  ),
            Pair(TypeSub  , "company.id"  ),
            Pair(TypeText , ", "  ),
            Pair(TypeSub  , "company.id"  ),
            Pair(TypeText , " does abc. visit "  ),
            Pair(TypeSub  , "company.url"  ),
            Pair(TypeText , " for more info. regards "  ),
            Pair(TypeSub  , "company.support" )
        ))
    }

    val TEST_TEMPLATE = "Hi @{user.api}, Welcome to @{company.api}."


    @Test fun can_substitute_basic_name() {
        val name = subs().resolve("@{user.home}/slatekit/env.conf")
        Assert.assertTrue ( name == "c:/users/johndoe/slatekit/env.conf")
    }


    @Test fun can_parse_on_demand_template_as_result() {
        val result = subs().parse(TEST_TEMPLATE)
        Assert.assertTrue( result.success )
        Assert.assertTrue( result.map { it.size == 5 }.success)
        checkResult(result, listOf(
            Pair(TypeText  , "Hi "           ),
            Pair(TypeSub   , "user.api"     ),
            Pair(TypeText  , ", Welcome to " ),
            Pair(TypeSub   , "company.api"  ),
            Pair(TypeText  , "."             )
        ))
    }


    @Test fun can_parse_demand_template_as_template() {
        val templates = Templates()
        val result = templates.parseTemplate("showWelcome", TEST_TEMPLATE)
        Assert.assertTrue( result.valid )
        Assert.assertTrue( result.name == "showWelcome")
        Assert.assertTrue( result.parsed)
        Assert.assertTrue( result.valid )
        Assert.assertTrue( result.content == TEST_TEMPLATE )
        Assert.assertTrue( result.parts!!.size == 5 )

        check(result.parts!!, listOf(
            Pair(TypeText  , "Hi "           ),
            Pair(TypeSub   , "user.api"     ),
            Pair(TypeText  , ", Welcome to " ),
            Pair(TypeSub   , "company.api"  ),
            Pair(TypeText  , "."             )
        ))
    }


    @Test fun can_resolve_on_demand_template_with_saved_vars() {
        val templates = Templates(variables =
                listOf(
            Pair("company.api" , { s  -> "CodeHelix"                       }),
            Pair("app.api"     , { s  -> "slatekit.sampleapp"              }),
            Pair("user.api"    , { s  -> "john.doe"                        }),
            Pair("code"         , { s  -> Random.alpha6()                   })
        ))
        val result = templates.resolve(TEST_TEMPLATE)
        Assert.assertTrue( result != null )
        Assert.assertTrue(result!! == "Hi john.doe, Welcome to CodeHelix.")
    }


    @Test fun can_resolve_on_demand_template_with_custom_vars() {
        val templates = Templates()
        val subs = Templates.subs( listOf(
            Pair("company.api" , { s -> "CodeHelix"                       }),
            Pair("app.api"     , { s -> "slatekit.sampleapp"              }),
            Pair("user.api"    , { s -> "john.doe"                        }),
            Pair("code"         , { s -> Random.alpha6()                   })
        ))

        val result = templates.resolve(TEST_TEMPLATE, subs)
        Assert.assertTrue(result != null)
        Assert.assertTrue(result!! == "Hi john.doe, Welcome to CodeHelix.")
    }


    @Test fun can_resolve_saved_template_with_saved_vars() {
        val templates = Templates.build(
                templates = listOf(
                        Template("showWelcome", "Hi @{user.api}, Welcome to @{company.api}."),
                        Template("confirm", "Your confirmation code for @{app.api} is @{code}.")
                ),
                subs = listOf(
                        Pair("company.api" , { s -> "CodeHelix"                       }),
                        Pair("app.api"     , { s -> "slatekit.sampleapp"              }),
                        Pair("user.api"    , { s -> "john.doe"                        }),
                        Pair("code"         , { s -> Random.alpha6()                   })
                ))

        val result = templates.resolveTemplate("showWelcome", null)
        Assert.assertTrue(result != null )
        Assert.assertTrue(result!! == "Hi john.doe, Welcome to CodeHelix.")
    }


    @Test fun can_resolve_saved_template_with_custom_vars() {
        val templates = Templates.build(
                templates = listOf(
                        Template("showWelcome", "Hi @{user.api}, Welcome to @{company.api}."),
                        Template("confirm", "Your confirmation code for @{app.api} is @{code}.")
        ))

        val subs = Templates.subs(listOf(
                Pair("company.api" , { s -> "CodeHelix"                       }),
                Pair("app.api"     , { s -> "slatekit.sampleapp"              }),
                Pair("user.api"    , { s -> "john.doe"                        }),
                Pair("code"         , { s -> Random.alpha6()                   })
        ))
        val result = templates.resolveTemplate("showWelcome", subs)
        Assert.assertTrue(result != null )
        Assert.assertTrue(result!! == "Hi john.doe, Welcome to CodeHelix.")
    }


    @Test fun can_resolve_saved_template_with_dictionary_variables_with_global_overrides() {
        val templates = Templates.build(
                templates = listOf(
                        Template("showWelcome", "Hi @{user.api}, Welcome to @{company.api}."),
                        Template("confirm", "Your confirmation code for @{app.api} is @{code}.")
        ),
        subs = listOf(
                Pair("company.api" , { s -> "CodeHelix"                       }),
                Pair("app.api"     , { s -> "slatekit.sampleapp2"             }),
                Pair("user.api"    , { s -> "john.doe"                        }),
                Pair("code"         , { s -> Random.alpha6()                   })
        ))

        val subs = mapOf(
                "company.api" to  "CodeHelix",
                "user.api"    to  "john.doe" ,
                "code"         to  "abc123"
        )
        val result = templates.resolveTemplateWithVars("confirm", subs)
        Assert.assertTrue(result != null)
        Assert.assertTrue(result!! == "Your confirmation code for slatekit.sampleapp2 is abc123.")
    }


    fun checkResult(subsResult: Try<List<TemplatePart>>, expected:List<Pair<Short,String>>)  {
        Assert.assertTrue(subsResult.success)
        val subs = (subsResult as Success).value
        check(subs, expected)
    }


    fun check(subs:List<TemplatePart>, expected:List<Pair<Short,String>>)  {
        Assert.assertTrue( subs.size == expected.size)
        for( ndx in 0 .. expected.size -1 ) {
            val actual = subs[ndx]
            val expect = expected[ndx]
            Assert.assertTrue( actual.subType == expect.first)
            Assert.assertTrue( actual.text == expect.second)
        }
    }


    fun subs(): Templates {
        //<doc:setup>
        val templates = Templates.build(

                listOf(
                        Template("showWelcome", "Hi @{user.api}, Welcome to @{company.api}."),
                        Template("confirm", "Your confirmation code for @{app.api} is @{code}.")
                ),
                listOf<Pair<String, ((TemplatePart) -> String)>>(
                        Pair("user.home", { s -> "c:/users/johndoe" }),
                        Pair("company.id", { s -> "slatekit" }),
                        Pair("company.api", { s -> "CodeHelix" }),
                        Pair("company.url", { s -> "http://www.slatekit.com" }),
                        Pair("company.dir", { s -> "@{user.home}/@{company.id}" }),
                        Pair("company.confs", { s -> "@{user.home}/@{company.id}/confs" }),
                        Pair("app.id", { s -> "slatekit.tests" }),
                        Pair("app.api", { s -> "slatekit.sampleapp" }),
                        Pair("app.dir", { s -> "@{company.dir}/@{app.id}" }),
                        Pair("app.confs", { s -> "@{app.dir}/confs" }),
                        Pair("user.api", { s -> "john.doe" }),
                        Pair("code", { s -> Random.alpha6() })
                )
        )
        return templates
    }
}