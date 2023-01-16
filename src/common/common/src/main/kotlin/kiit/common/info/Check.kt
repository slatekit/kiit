package kiit.common.info

/**
 * Used for build & timestamp info for a health-check / ping endpoint
 * @param version : Version number of application
 * @param commit  : Git commit sha
 * @param branch  : Git branch origin
 * @param date    : Date of the build
 * @param time    : Current timestamp
 *  {
 *      "version": "2.1.0.622",
 *      "commit": "bdcdc8fbab",
 *      "branch": "master",
 *      "date": "2020-04-05T13-30-00",
 *      "time": "2020-04-06T14:11:35Z"
 *  },
 */
data class Check(
        @JvmField val version: String,
        @JvmField val commit: String,
        @JvmField val branch: String,
        @JvmField val date: String,
        @JvmField val time: String
)