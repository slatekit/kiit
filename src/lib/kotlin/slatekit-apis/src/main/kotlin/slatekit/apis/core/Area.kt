package slatekit.apis.core


/**
 * The top most level qualifier in the Universal Routing Structure
 * Essentially the root of the Routing tree
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
class Area(val name:String, val apis:Lookup<Api>)