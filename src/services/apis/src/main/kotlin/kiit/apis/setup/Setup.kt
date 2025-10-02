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


fun routes(all:List<ApiSetup>, namer: Namer? = null) : Routes {
    val loader =  Loader(namer)
    val routes = loader.routes(all)
    return routes
}


fun router(all:List<ApiSetup>, namer: Namer? = null) : DefaultRouter {
    val routes = routes(all, namer)
    return DefaultRouter(routes, namer)
}


fun api(klass: KClass<*>, singleton: Any?, setup: SetupType = SetupType.Annotated, declared: Boolean = true, content:String = "")
    : ApiSetup = ApiSetup(klass, singleton, setup, declared, content)


