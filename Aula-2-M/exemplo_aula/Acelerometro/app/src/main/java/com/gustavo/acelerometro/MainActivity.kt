package com.gustavo.acelerometro

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gustavo.acelerometro.ui.theme.AcelerometroTheme
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {
    var sensorManager: SensorManager? = null
    var sensor: Sensor? = null
    var acelData by mutableStateOf(AcelData())
    val gravidade = FloatArray(3)
    val aceleracaoLinear = FloatArray(3)

    val TAG = "SENSOR_ACELEROMETRO"

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text("Leitura de Acelerômetro")
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },

                ) { innerPadding ->

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                ) {
                    AcelerometroTheme {
                        showSensorData(acelData)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        val alpha: Float = 0.8f

        // Isolate the force of gravity with the low-pass filter.
        gravidade[0] = alpha * gravidade[0] + (1 - alpha) * x
        gravidade[1] = alpha * gravidade[1] + (1 - alpha) * y
        gravidade[2] = alpha * gravidade[2] + (1 - alpha) * z

        // Remove the gravity contribution with the high-pass filter.
        aceleracaoLinear[0] = x - gravidade[0]
        aceleracaoLinear[1] = y - gravidade[1]
        aceleracaoLinear[2] = z - gravidade[2]

        acelData = AcelData(
            x = "%.2f".format(x),
            y = "%.2f".format(y),
            z = "%.2f".format(z),
            gravity = "%.2f".format(sqrt(gravidade[0].pow(2) + gravidade[1].pow(2) + gravidade[2].pow(2))),
            linearAcceleration = "%.2f".format(
                sqrt(aceleracaoLinear[0].pow(2) + aceleracaoLinear[1].pow(2) + aceleracaoLinear[2].pow(2)))
        )

        //Log.d(TAG, "valor %f".format(event?.values[0]))
    }
}

@Composable
fun showSensorData(data: AcelData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SensorText("Eixo x", data.x)
        SensorText("Eixo y", data.y)
        SensorText("Eixo z", data.z)
        Spacer(modifier = Modifier.height(24.dp))
        SensorText("Gravidade", data.gravity)
        SensorText("Aceleração no Dispositivo", data.linearAcceleration)
    }

}

@Composable
fun SensorText(label: String, value: String) {
    Text(
        text = "$label: $value m/s²".format(value),
        fontSize = 24.sp
    )
}

data class AcelData(
    val x: String = "",
    val y: String = "",
    val z: String = "",
    val gravity: String = "",
    val linearAcceleration: String = ""
)
