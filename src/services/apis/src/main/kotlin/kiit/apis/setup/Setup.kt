package kiit.apis.setup

import kiit.apis.SetupType
import kiit.apis.routes.*
import kotlin.reflect.KClass
import kiit.utils.naming.Namer


data class ApiSetup(
    val klass: KClass<*>,
    val singleton: Any? = null,
    val setup: SetupType = SetupType.Annotated,
    val declared: Boolean = true,
    val content:String = ""
)


data class GlobalVersion(val version:String, val apis:List<ApiSetup>)


fun routes(versions: List<GlobalVersion>, namer: Namer? = null) : List<VersionAreas> {
    val loader =  Loader(namer)
    val versionedRoutes = versions.map { loader.routes(it.version, it.apis) }
    return versionedRoutes
}


fun router(versions: List<GlobalVersion>, namer: Namer? = null) : DefaultRouter {
    val versionedRoutes = routes(versions, namer)
    return DefaultRouter(versionedRoutes, namer)
}

fun global(version:String, apis:List<ApiSetup>) : GlobalVersion = GlobalVersion(version, apis)

fun api(klass: KClass<*>, singleton: Any?, setup: SetupType = SetupType.Annotated, declared: Boolean = true, content:String = "")
    : ApiSetup = ApiSetup(klass, singleton, setup, declared, content)


