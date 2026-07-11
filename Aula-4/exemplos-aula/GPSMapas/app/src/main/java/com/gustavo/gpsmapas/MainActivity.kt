package com.gustavo.gpsmapas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.gustavo.gpsmapas.ui.theme.GPSMapasTheme

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val TAG = "GPS"

    //fase 2
    var posicao by mutableStateOf(LatLng(0.0, 0.0))

    //fase 2.5
    val posicoes = mutableStateListOf<LatLng>()

    //remover para fase2.5
   // var markerState by mutableStateOf(MarkerState(posicao))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //precisa para funcionar o GPS
        //fase 1
        permissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //fase1.5
        val request =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L
            )
                .setMinUpdateDistanceMeters(1f)
                .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        //fase1.5
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
        setContent {
            GPSMapasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    //fase1.5
    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {
            onLocationChange(result)
        }

    }

    //fase1.5
    fun onLocationChange(result: LocationResult) {
        val msg = "latitude %.2f longitude %.2f".format(
            result.lastLocation?.latitude,
            result.lastLocation?.longitude
        )
        Log.d(TAG, msg)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

        val lat = result.lastLocation?.latitude ?: 0.0
        val lng = result.lastLocation?.longitude ?: 0.0

        //fase 2
        posicao = LatLng(lat,lng)

        //fase 2.5
        posicoes.add( LatLng(lat,lng))
    }

    //fase 1
    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            var permission =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            permission = permission &&
                    (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false)
            if (permission) {
                lerGps()
            }
        }

    //fase 1
    private fun lerGps() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                //fase 1
                if (location != null) {
                    val msg =
                        "latitude %.2f longitude %.2f".format(location.latitude, location.longitude)
                    Log.d(TAG, msg)
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    posicao = LatLng(location.latitude, location.longitude)
                    /*gpsData = GpsData(
                        latitude = location.latitude.toString(),
                        longitude = location.longitude.toString(),
                        accuracy = location.accuracy.toString()
                    )*/
                }

            }
    }


    fun limparMarcadores(){
        posicoes.clear()
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {


        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                posicao,
                16f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp,
                    top = 60.dp
                )
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /* fase 2
            LaunchedEffect(posicao) {
                markerState.position = posicao
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(posicao, 16f)
            }*/
            //fase 2.5

            val p = posicoes.lastOrNull() ?: LatLng(0.0, 0.0)
                LaunchedEffect(p) {
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(p, 16f)
                }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                cameraPositionState = cameraPositionState
            ) {
                //fase 2.5
                posicoes.forEach { local ->
                    Marker(
                        state = MarkerState(position = local),
                    )

                }

                /* fase 2
                Marker(
                    state = markerState,
                    title = "Minha posição",
                    snippet = "Localização atual"
                )*/
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(

                    onClick = {
                        lerGps()
                    }) { Text("Ler Gps") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(

                    onClick = {
                        limparMarcadores()
                    }) { Text("Limpar Marcadores") }

            }
        }
    }
}