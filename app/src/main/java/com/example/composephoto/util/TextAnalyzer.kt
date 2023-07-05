package com.example.composephoto.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.composephoto.data.AadhaarProcessing
import com.example.composephoto.data.PanProcessing
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.lang.Exception
import java.util.Locale

class TextAnalyzer(
    private val context: Context,
    private val imageUri: Uri,
    private val aadhaarProcessing: AadhaarProcessing?,
    private val panProcessing: PanProcessing?
) {


    var map = mutableMapOf<String, String>()
    private val detector = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    init {
        recognizeTextOnDevice(imageUri)
    }


    private fun getErrorMessage(exception: Exception): String? {
        val mlKitException = exception as? MlKitException ?: return exception.message
        return if (mlKitException.errorCode == MlKitException.UNAVAILABLE) {
            "Waiting for text recognition model to be downloaded"
        } else exception.message
    }

    companion object {
        private const val TAG = "TextAnalyzer"
    }

    private fun recognizeTextOnDevice(
        image: Uri
    ) {

        // Pass image to an ML Kit Vision API
        val inputImage = InputImage.fromFilePath(context, image)
        detector.process(inputImage)
            .addOnSuccessListener { visionText ->
                if (panProcessing != null && visionText.text.lowercase()
                        .contains("income tax department")
                ) {

                    map = panProcessing.processExtractedTextForFrontPic(visionText)
                    map["pan"] = "pan"
                    Log.d("ContentValuesw", visionText.text)

                }

                if (visionText.text.contains("Address")) {
                    Log.d("value", "back")
                    map = aadhaarProcessing?.processExtractedTextForBackPic(visionText.text)!!
                    map["back"] = "backData"
                    Log.d("ContentValue", map.toMap().toString())
                } else if (visionText.text.contains("DOB") || visionText.text.lowercase(Locale.ROOT)
                        .contains("year of birth")
                ) {
                    Log.d("value", "front")
                    map = aadhaarProcessing?.processExtractedTextForFrontPic(visionText, context)!!
                    map["front"] = "frontData"
                    Log.d("ContentValue", map.toMap().toString())


                }


            }
            .addOnFailureListener { exception ->
                // Task failed with an exception
                Log.e(TAG, "Text recognition error", exception)
                val message = getErrorMessage(exception)
                message?.let {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

    }


}



