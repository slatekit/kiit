package kiit.common.utils

import java.util.regex.Pattern

object StringSearch {

    fun phone(text: String): String? {
        if (text.isNullOrEmpty())
            return null

        val patterns = arrayOf("\\d{3}[-]?\\d{3}[-]?\\d{4}",
                "\\d{3}\\s*[-]?\\s*\\d{3}\\s*[-]?\\s*\\d{4}",
                "\\s*\\d{3}\\s*[-]?\\s*\\d{3}\\s*[-]?\\s*\\d{4}\\s*",
                ".\\(\\s*\\d{3}\\s*\\)\\s*[-]?\\s*\\d{3}\\s*[-]?\\s*\\d{4}.",
                "\\s*\\d{9}\\s*")
        var phone = ""
        for (ndx in patterns.indices) {
            val patternText = patterns[ndx]
            val pattern = Pattern.compile(patternText)
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                phone = matcher.group()
                break
            }
        }
        return phone
    }


    fun email(text: String): String? {
        if (text.isNullOrEmpty())
            return null

        var email: String? = null
        try {
            val regex = "[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}"
            email = null
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                email = matcher.group()
            }
        } catch (ex: Exception) {

        }

        return email
    }


    fun url(text: String): String? {
        if (text.isNullOrEmpty())
            return null

        var url: String? = null
        try {
            val regex = "http\\:|https\\:|www\\."
            url = null
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val start = matcher.start()
                val spacePos = text.indexOf(' ', start)
                if (spacePos > -1) {
                    url = text.substring(start, spacePos)
                } else
                    url = text.substring(start)
            }
        } catch (ex: Exception) {

        }

        return url
    }
}