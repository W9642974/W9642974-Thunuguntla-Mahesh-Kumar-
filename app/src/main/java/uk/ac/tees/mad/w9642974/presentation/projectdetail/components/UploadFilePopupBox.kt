package uk.ac.tees.mad.w9642974.presentation.projectdetail.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex


@Composable
fun UploadFilePopupBox(
    popupWidth: Float = 350f,
    showPopup: Boolean,
    fileUri: Uri?,
    onClickOutside: () -> Unit,
    onConfirm: (ByteArray, String, String) -> Unit,
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    var fileName by remember {
        mutableStateOf("")
    }
    var fileDescription by remember {
        mutableStateOf("")
    }
    if (showPopup) {
        // full screen background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(0.5f))
                .zIndex(10F),
            contentAlignment = Alignment.Center
        ) {
            // popup
            Popup(alignment = Alignment.Center, properties = PopupProperties(
                excludeFromSystemGesture = true,
                focusable = true
            ),
                // to dismiss on click outside
                onDismissRequest = { onClickOutside() }) {
                if (isLoading) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .width(popupWidth.dp)
                            .height(popupWidth.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .width(popupWidth.dp)
                            .background(Color.White), contentAlignment = Alignment.Center
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(40.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (fileUri != null) {
                                Text(
                                    text = "Enter file name",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Box(
                                    modifier = Modifier.size(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = null,
                                        modifier = Modifier.size(70.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                OutlinedTextField(
                                    value = fileName,
                                    onValueChange = { fileName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = {
                                        Text(text = "File name")
                                    },
                                    shape = RoundedCornerShape(30.dp)
                                )
                                OutlinedTextField(
                                    value = fileDescription,
                                    onValueChange = { fileDescription = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = {
                                        Text(text = "Short description")
                                    },
                                    shape = RoundedCornerShape(30.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(onClick = {
                                    val item = context.contentResolver.openInputStream(fileUri)
                                    val bytes = item?.readBytes()
                                    println(bytes)
                                    item?.close()
                                    if (bytes != null) {
                                        onConfirm(bytes, fileName, fileDescription)
                                    }
                                }) {
                                    Text(text = "Confirm")
                                }
                            } else {
                                Text(text = "Please choose file again.")
                            }
                        }
                    }
                }
            }
        }
    }

}