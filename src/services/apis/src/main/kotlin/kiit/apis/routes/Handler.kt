package kiit.apis.routes

import kiit.apis.Verb

interface RouteHandler

class MethodExecutor(val call: Call) : RouteHandler

class RouteForwarder(val globalVersion: String, val verb: Verb, val area:Area, val api:Versioned, val action:Versioned) : RouteHandler
