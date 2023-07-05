package com.example.composephoto

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.composephoto.camera.CameraCapture
import com.example.composephoto.gallery.GallerySelect
import com.example.composephoto.ui.theme.ComposePhotoIntegrationTheme
import com.example.composephoto.util.TextAnalyzer
import com.example.composephoto.data.AadhaarProcessing
import com.example.composephoto.data.PanProcessing
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoilApi
@ExperimentalCoroutinesApi
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {
    lateinit var textAnalyzer: TextAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePhotoIntegrationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainContent(Modifier.fillMaxSize())
                }
            }
        }
    }


    @ExperimentalCoilApi
    @ExperimentalCoroutinesApi
    @ExperimentalPermissionsApi
    @Composable
    fun MainContent(modifier: Modifier = Modifier) {
        var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
        var dataExtracted by remember { mutableStateOf(false) }
        var btnClicked by remember { mutableStateOf("") }
        var map: MutableMap<String, String>
        // aadhaar front Data
        var name by remember { mutableStateOf("") }
        var dob by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf("") }
        var aadhaar by remember { mutableStateOf("") }
        var pan by remember { mutableStateOf("") }

        // aadhaar back Data

        var pincode by remember { mutableStateOf("") }
        var street by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var state by remember { mutableStateOf("") }
        var relativeName by remember { mutableStateOf("") }
        var house by remember { mutableStateOf("") }
        var relation by remember { mutableStateOf("") }

        // aadhaar side
        var aadhaarSide by remember { mutableStateOf("") }

        if (imageUri != EMPTY_IMAGE_URI) {

            textAnalyzer = TextAnalyzer(
                context = this@MainActivity,
                imageUri = imageUri,
                aadhaarProcessing = AadhaarProcessing(),
                panProcessing = PanProcessing()
            )
            Column(modifier = modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    painter = rememberImagePainter(imageUri),
                    contentDescription = "Captured image"
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (dataExtracted && btnClicked == "aadhaar" && (aadhaar.isNotBlank()|| street.isNotBlank())) {
                    Text(
                        text = "Front Aadhaar Record: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(text = "Name: $name")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Dob: $dob")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Gender: $gender")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Aadhaar: $aadhaar")
                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Back Aadhaar Record: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Relation: $relation")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Relative: $relativeName")
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(text = "House No: $house")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Street: $street")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "City: $city")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "State: $state")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Pincode: $pincode")
                    Spacer(modifier = Modifier.height(5.dp))

                } else if (dataExtracted && btnClicked == "pan" && pan.isNotEmpty()) {
                    Text(
                        text = "Pan Record: ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(text = "Name: $name")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Dob: $dob")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "FatherName: $relativeName")
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Pan No: $pan")
                    Spacer(modifier = Modifier.height(5.dp))

                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Aadhaar Record",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Button(
                            onClick = {
                                name = ""
                                dob = ""
                                relativeName = ""
                                pan=""
                                textAnalyzer.map.clear()
                                imageUri = EMPTY_IMAGE_URI
                            }
                        ) {
                            Text(if (aadhaarSide.isEmpty()) "Remove Img" else "$aadhaarSide Img")
                        }
                        Button(
                            onClick = {
                                btnClicked = "aadhaar"
                                map = textAnalyzer.map
                                if (!map["front"].isNullOrEmpty()) {
                                    aadhaarSide = "Back"
                                    name = map["name"].toString()
                                    dob = map["dob"].toString()
                                    gender = map["gender"].toString()
                                    aadhaar = map["aadhaar"].toString()
                                    Log.d("ContentMain", textAnalyzer.map.toMap().toString())
                                } else if (!map["back"].isNullOrEmpty()) {
                                    aadhaarSide = "Front"
                                    relation = map["relation"].toString()
                                    relativeName = map["name"].toString()
                                    house = map["houseNo"].toString()
                                    street = map["street"].toString()
                                    city = map["city"].toString()
                                    state = map["state"].toString()
                                    pincode = map["pincode"].toString()

                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Please Select Aadhaar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                Log.d("data", map.size.toString())
                                dataExtracted = true
                            }
                        ) {
                            Text("Extract Data")
                        }

                        Button(
                            onClick = {
                                // front record
                                name = ""
                                dob = ""
                                gender = ""
                                aadhaar = ""

                                // back record
                                house = ""
                                street = ""
                                relativeName = ""
                                city = ""
                                state = ""
                                pincode = ""
                            }
                        ) {
                            Text("Clear")
                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Pan Record",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Button(
                            onClick = {
                                name = ""
                                dob = ""
                                relativeName = ""
                                pan=""
                                textAnalyzer.map.clear()
                                imageUri = EMPTY_IMAGE_URI
                            }
                        ){
                            Text(text =  "Remove Img" )
                        }
                        Button(
                            onClick = {
                                btnClicked = "pan"
                                map = textAnalyzer.map
                                if (!map["pan"].isNullOrEmpty()){
                                    name = map["name"].toString()
                                    dob = map["dob"].toString()
                                    relativeName = map["fatherName"].toString()
                                    pan = map["panNumber"].toString()
                                }else{
                                    Toast.makeText(this@MainActivity, "Please Select Pan Card", Toast.LENGTH_SHORT).show()
                                }
                                dataExtracted = true
                            }
                        ) {
                            Text("Extract Data")
                        }

                        Button(
                            onClick = {
                                // front record
                                name = ""
                                dob = ""
                                relativeName = ""
                                pan=""
                            }
                        ) {
                            Text("Clear")
                        }

                    }

                }
            }
        } else {
            var showGallerySelect by remember { mutableStateOf(false) }
            if (showGallerySelect) {
                GallerySelect(
                    modifier = modifier,
                    onImageUri = { uri ->
                        showGallerySelect = false
                        imageUri = uri
                    }
                )
            } else {
                Box(modifier = modifier) {
                    CameraCapture(
                        modifier = modifier,
                        onImageFile = { file ->
                            imageUri = file.toUri()
                        }
                    )
                    Image(painter = painterResource(id = R.drawable.gallery),
                        contentDescription = "GalleryImage",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(50.dp)
                            .padding(4.dp)
                            .clickable {
                                showGallerySelect = true
                            }
                    )
                }
            }
        }
    }

}

val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")
