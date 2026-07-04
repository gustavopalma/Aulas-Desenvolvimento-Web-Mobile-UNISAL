package com.gustavo.cameraintent

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.gustavo.cameraintent.ui.theme.CameraIntentTheme

class MainActivity : ComponentActivity() {

    var fotoUri by mutableStateOf<Uri?>(null)
    private var uriTemporaria: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CameraIntentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CameraIntent(
                        fotoUri = fotoUri,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { sucesso ->
            if (sucesso) {
                fotoUri = uriTemporaria
            } else {
                fotoUri = null
            }
        }

    private fun criarUriImagem(): Uri {
        val nomeArquivo = "foto_${System.currentTimeMillis()}.jpg"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nomeArquivo)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AulaIntents")
        }

        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ) ?: throw RuntimeException("Não foi possível criar a imagem")
    }

    private fun abrirCamera() {
        /* para o ínicio
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivity(intent)*/
        uriTemporaria = criarUriImagem()
        cameraLauncher.launch(uriTemporaria!!)
    }


    @Composable
    fun CameraIntent(fotoUri : Uri?, modifier: Modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (fotoUri == null) {
                Image(
                    painter = painterResource(R.drawable.sem_foto),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(text = "Foto Capturada!", fontSize = 24.sp)
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Foto capturada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Button(onClick = {
                //abrir o browser
               /* val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://developer.android.com")
                )*/

                //abrir o discador
                /*val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:11999999999")
                )*/

                abrirCamera()
            }) {
                Text("Tirar Foto!")
            }

            Button(onClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )

                startActivity(intent)
            }) {
                    Text("Abrir Galeria")
            }
        }

    }
}