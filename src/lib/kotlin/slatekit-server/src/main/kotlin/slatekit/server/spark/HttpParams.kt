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

import slatekit.common.DateTime
import slatekit.common.InputFuncs
import slatekit.common.Inputs
import slatekit.common.encrypt.Encryptor
import spark.Request


data class HttpParams(val req: Request, val enc: Encryptor?) : Inputs {

    val method = req.requestMethod().toLowerCase()
    val hasBody = method == "put" || method == "post"
    val json = HttpRequest.loadJson(req)


    override fun get(key: String): Any? = getInternal(key)
    override fun getObject(key: String): Any? = getInternal(key)
    override fun size(): Int = req.headers().size


    override fun getString(key: String): String = InputFuncs.decrypt(getInternalString(key).trim(), { it -> enc?.decrypt(it) ?: it })
    override fun getDate(key: String): DateTime = InputFuncs.convertDate(getInternalString(key).trim())
    override fun getBool(key: String): Boolean = getInternalString(key).trim().toBoolean()
    override fun getShort(key: String): Short = getInternalString(key).trim().toShort()
    override fun getInt(key: String): Int = getInternalString(key).trim().toInt()
    override fun getLong(key: String): Long = getInternalString(key).trim().toLong()
    override fun getDouble(key: String): Double = getInternalString(key).trim().toDouble()
    override fun getFloat(key: String): Float = getInternalString(key).trim().toFloat()


    override fun containsKey(key: String): Boolean {
        return if (hasBody && json.containsKey(key)) {
            true
        }
        else if (!hasBody) {
            req.queryParams().contains(key)
        }
        else {
            false
        }
    }


    fun getInternal(key: String): Any? {
        val value = if (hasBody && json.containsKey(key)) {
            json.get(key)
        }
        else if (!hasBody) {
            req.queryParams(key)
        }
        else {
            ""
        }
        return value
    }


    fun getInternalString(key: String): String {
        val value = if (hasBody && json.containsKey(key)) {
            json.get(key).toString()
        }
        else if (!hasBody) {
            req.queryParams(key)
        }
        else {
            ""
        }
        return value
    }
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