package slatekit.policy.middleware

/**
 * Base trait for the 3 different types of middle ware ( hooks, filters, controls )
 * 1. hooks   : for pre/post execution of api actions         ( can not modify the execution    )
 * 2. filters : for allowing/denying execution of api action  ( can only allow/deny execution   )
 * 3. controls: for controlling the execution of an api action( can handle the execution itself )
 */
interface Middleware
