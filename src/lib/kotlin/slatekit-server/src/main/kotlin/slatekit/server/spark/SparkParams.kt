/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.server.spark

import org.json.simple.JSONObject
import slatekit.apis.support.JsonSupport
import slatekit.common.*
import slatekit.common.encrypt.Encryptor
import spark.Request
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

/**
 * @param req : The raw request
 * @param enc : The encryptor
 * @param extraParams: Additional parameters from SlateKit.
 *                     These are useful for the middleware rewrite module
 *                     which can rewrite routes add parameters
 */
data class SparkParams(
    val req: Request,
    val enc: Encryptor?,
    val extraParams: MutableMap<String, Any> = mutableMapOf()
) : Inputs, InputsUpdateable, JsonSupport {

    val method = req.requestMethod().toLowerCase()
    val hasBody = SparkRequest.isBodyAllowed(method)
    val json = SparkRequest.loadJson(req, false)

    override fun toJson(): JSONObject = SparkRequest.loadJson(req, true)

    override fun get(key: String): Any? = getInternal(key)
    override fun getObject(key: String): Any? = getInternal(key)
    override fun size(): Int = req.headers().size

    override val raw: Any = json
    override fun getString(key: String): String = InputFuncs.decrypt(getInternalString(key).trim(), { it -> enc?.decrypt(it) ?: it })
    override fun getBool(key: String): Boolean = Conversions.toBool(getStringRaw(key))
    override fun getShort(key: String): Short = Conversions.toShort(getStringRaw(key))
    override fun getInt(key: String): Int = Conversions.toInt(getStringRaw(key))
    override fun getLong(key: String): Long = Conversions.toLong(getStringRaw(key))
    override fun getFloat(key: String): Float = Conversions.toFloat(getStringRaw(key))
    override fun getDouble(key: String): Double = Conversions.toDouble(getStringRaw(key))
    override fun getLocalDate(key: String): LocalDate = Conversions.toLocalDate(getStringRaw(key))
    override fun getLocalTime(key: String): LocalTime = Conversions.toLocalTime(getStringRaw(key))
    override fun getLocalDateTime(key: String): LocalDateTime = Conversions.toLocalDateTime(getStringRaw(key))
    override fun getZonedDateTime(key: String): ZonedDateTime = Conversions.toZonedDateTime(getStringRaw(key))
    override fun getDateTime(key: String): DateTime = Conversions.toDateTime(getStringRaw(key))

    override fun containsKey(key: String): Boolean {
        return if (extraParams.containsKey(key)) {
            true
        } else if (hasBody && json.containsKey(key)) {
            true
        } else if (!hasBody) {
            req.queryParams().contains(key)
        } else {
            false
        }
    }

    fun getInternal(key: String): Any? {
        val value = if (extraParams.containsKey(key)) {
            extraParams[key]
        } else if (hasBody && json.containsKey(key)) {
            json.get(key)
        } else if (!hasBody) {
            req.queryParams(key)
        } else {
            ""
        }
        return value
    }

    fun getInternalString(key: String): String {
        val value = if (extraParams.containsKey(key)) {
            extraParams[key].toString()
        } else if (hasBody && json.containsKey(key)) {
            json.get(key).toString()
        } else if (!hasBody) {
            req.queryParams(key)
        } else {
            ""
        }
        return value
    }

    /**
     * This is to support the rewrite middleware which can rewrite
     * requests to other requests ( e.g. routes and parameters )
     */
    override fun add(key: String, value: Any): Inputs {
        if (hasBody) {
            json.put(key, value)
        } else {
            extraParams.put(key, value)
        }
        return this
    }

    fun getStringRaw(key: String): String = getInternalString(key).trim()
}

/* FILE HANDLING
* post("/upload", "multipart/form-data", (request, response) -> {

String location = "image";          // the directory location where files will be stored
long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
     location, maxFileSize, maxRequestSize, fileSizeThreshold);
 request.raw().setAttribute("org.eclipse.jetty.multipartConfig",
     multipartConfigElement);

Collection<Part> parts = request.raw().getParts();
for (Part part : parts) {
   System.out.println("Name: " + part.getName());
   System.out.println("Size: " + part.getSize());
   System.out.println("Filename: " + part.getSubmittedFileName());
}

String fName = request.raw().getPart("file").getSubmittedFileName();
System.out.println("Title: " + request.raw().getParameter("title"));
System.out.println("File: " + fName);

Part uploadedFile = request.raw().getPart("file");
Path out = Paths.get("image/" + fName);
try (final InputStream in = uploadedFile.getInputStream()) {
   Files.copy(in, out);
   uploadedFile.delete();
}
// cleanup
multipartConfigElement = null;
parts = null;
uploadedFile = null;

return "OK";
});
* */
