package com.example.composephoto.data

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.TextBlock
import java.util.Locale
import java.util.TreeMap
import java.util.regex.Pattern


class AadhaarProcessing {
    fun processExtractedTextForFrontPic(
        text: Text,
        context: Context?
    ): HashMap<String, String>? {

        val blocks: List<TextBlock> = text.textBlocks
        if (blocks.isEmpty()) {
            Toast.makeText(context, "No Text :(", Toast.LENGTH_LONG).show()
            return null
        }
        val map = TreeMap<String, String>()
        for (block in text.textBlocks) {
            for (line in block.lines) {
                val rect: Rect? = line.boundingBox
                val y = rect?.exactCenterY().toString()
                val lineTxt: String = line.text
                map[y] = lineTxt
            }
        }
        Log.d("ContentBlock", map.toMap().toString())
        val orderedData: List<String> = ArrayList(map.values)
        val dataMap = HashMap<String, String>()
        var i = 0

        // aadhaar no
        val regex = "\\d\\d\\d\\d([,\\s])?\\d\\d\\d\\d.*"
        i = 0
        while (i < orderedData.size) {
            if (orderedData[i].matches(regex.toRegex(RegexOption.IGNORE_CASE))) {
                dataMap["aadhaar"] = orderedData[i]
                break
            }
            i++
        }
        // gender first
        i = 0
        while (i < orderedData.size) {
            if (orderedData[i].lowercase(Locale.ROOT).contains("female")) {
                dataMap["gender"] = "Female"
                break
            } else if (orderedData[i].lowercase(Locale.ROOT).contains("male")) {
                dataMap["gender"] = "Male"
                break
            }
            i++
        }

        //searching for Name
        i = 0
        val name = "^(?!.*\\bgovernment of india\\b)[A-Za-z\\s]+\$"
        while (i < orderedData.size) {
            if (orderedData[i].matches(name.toRegex(RegexOption.IGNORE_CASE))) {
                dataMap["name"] = orderedData[i]
                break
            }
            i++
        }

        i = 0
        while (i < orderedData.size) {
            val pattern = Pattern.compile("(?!DOB):?(\\d{2}/\\d{2}/\\d{4})")
            val matcher = pattern.matcher(text.text)
            if (matcher.find()) {
                dataMap["dob"] = matcher.group(1).toString()
                Log.d("ContentMap", dataMap["dob"].toString())
                break
            } else {
                Log.d("ContentMap", dataMap["dob"].toString())

            }
            i++
        }
        return dataMap
    }

    fun processExtractedTextForBackPic(
        value: String,
    ): HashMap<String, String>? {


        val dataMap = HashMap<String, String>()
        val data = value.replace("\n", " ")
        val pattern = Pattern.compile(
            "(c/o|d/o|s/o|w/o|co|do|so|wo|cio|dio|sio|wio):?.*?(\\d{6})",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(data)
        Log.d("data", data.lowercase(Locale.ROOT))

        if (matcher.find()) {
            val extractedString = matcher.group()
            Log.d("data", extractedString)

            // pincode
            val regex = Regex("\\b\\d{6}\\b", RegexOption.IGNORE_CASE)
            val pincodeResult = regex.find(extractedString)
            dataMap["pincode"] = pincodeResult?.value.toString()
            Log.d("pincode", dataMap["pincode"].toString())


            // state
            val pincode = dataMap["pincode"]
            val stateRegex =
                Pattern.compile(",([^,-]+)-?\\s*?-?\\s*?$pincode", Pattern.CASE_INSENSITIVE)
            val stateMatcher = stateRegex.matcher(extractedString)
            if (stateMatcher.find()) {
                dataMap["state"] = stateMatcher.group(1).toString().trim()
                Log.d("state", dataMap["state"].toString())
            }

            // city
            val state = dataMap["state"]
            val pinCode = dataMap["pincode"]
            val cityRegex = Pattern.compile(
                ".*?, ([^,]+), $state\\s*?-?\\s*?$pinCode",
                Pattern.CASE_INSENSITIVE
            )
            val cityMatcher = cityRegex.matcher(extractedString)

            if (cityMatcher.find()) {
                dataMap["city"] = cityMatcher.group(1)!!
                Log.d("city", dataMap["city"].toString())

            } else {
                Log.d("state", "city not found")
            }

            // relation
            val relationRegex = Regex(
                "(c/o|d/o|s/o|w/o|co|do|so|wo|cio|dio|sio|wio:??\\s)",
                RegexOption.IGNORE_CASE
            )
            val relationResult = relationRegex.find(extractedString)
            dataMap["relation"] = relationResult?.value.toString()
            Log.d("relation", dataMap["relation"].toString())

            // name
            val relation = dataMap["relation"].toString()
            val relativeRegex = Regex("$relation:? ?\\s(.*?),", RegexOption.IGNORE_CASE)
            val relativeResult = relativeRegex.find(extractedString)
            dataMap["name"] =
                relativeResult?.value.toString().replace(relationRegex, "").replace(":", "")
                    .replace(",", "").trim()
            Log.d("name", dataMap["name"].toString())

            // house no
            val houseRegex = Pattern.compile(" [^,]+, (.*?), [^,]+,", Pattern.CASE_INSENSITIVE)
            val houseMatcher = houseRegex.matcher(extractedString)
            if (houseMatcher.find()) {
                dataMap["houseNo"] = houseMatcher.group(1).toString()
                Log.d("houseNo", dataMap["houseNo"].toString())

            } else {
                Log.d("houseNo", "houseNo not found")

            }

            // street
            val house = dataMap["houseNo"]
            val city = dataMap["city"]
            val streetRegex = Pattern.compile("$house, (.*?), $city,", Pattern.CASE_INSENSITIVE)
            val streetMatcher = streetRegex.matcher(extractedString)
            if (streetMatcher.find()) {
                dataMap["street"] = streetMatcher.group(1).toString()
                Log.d("street", dataMap["street"].toString())

            }

        } else {
            Log.d("data", "Substring not found.")
        }

        return dataMap
    }
}