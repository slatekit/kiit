package slatekit.apis.routes

/**
 * The top most level qualifier in the Universal Routing Structure
 * Essentially the logical root of APIs
 * e.g.
 *
 * Format:  {area}.{api}.{action}
 * Tree  :
 *          { area_1 }
 *
 *              - { api_1 }
 *
 *                  - { action_a }
 *                  - { action_b }
 *
 *              - { api_2 }
 *
 *                  - { action_c }
 *                  - { action_d }
 *
 *              - { api_3 }
 *
 *                  - { action_d }
 *                  - { action_e }
 */
class Area(val name: String, val apis: Lookup<Api>)
