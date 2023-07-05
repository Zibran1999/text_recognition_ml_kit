package com.example.composephoto.data

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.widget.Toast
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.TextBlock
import java.util.Locale
import java.util.TreeMap
import java.util.regex.Pattern


class PanProcessing {

    fun processExtractedTextForFrontPic(
        text: Text): MutableMap<String, String> {
        val regexMap = mapOf(
            "name" to """(?<=Name\n)(.*?)(?:\n|$)""".toRegex(),
            "fatherName" to """(?<=Father's Name\n)(.*?)(?:\n|${'$'})""".toRegex(),
            "dob" to """(?<=Date of Birth\n)(\d{2}/\d{2}/\d{4})""".toRegex(),
            "panNumber" to """(?<=Permanent Account Number Card\n)([A-Z0-9]+)""".toRegex()
        )
        val extractedData = mutableMapOf<String, String>()

        regexMap.forEach { (key, regex) ->
            val matchResult = regex.find(text.text)
            if (matchResult != null) {
                extractedData[key] = matchResult.groupValues[1].trim()
                Log.d("data", extractedData.toString())
            }
        }
        return extractedData
    }

}