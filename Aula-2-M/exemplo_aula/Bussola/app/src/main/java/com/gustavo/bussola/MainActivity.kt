package com.gustavo.bussola

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gustavo.bussola.ui.theme.BussolaTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    var angulo by mutableStateOf(0.0f)

    var sensorManager: SensorManager? = null
    var sensor: Sensor? = null

    val TAG = "BUSSOLA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        setContent {
            BussolaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Bussola(angulo)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ROTATION_VECTOR) return


        val rotationMatrix = FloatArray(9)

        SensorManager.getRotationMatrixFromVector(
            rotationMatrix,
            event.values
        )

        val orientation = FloatArray(3)

        SensorManager.getOrientation(
            rotationMatrix,
            orientation
        )

        angulo =
            ((Math.toDegrees(orientation[0].toDouble()) + 360) % 360).toFloat()
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)

    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }
}


@Composable
fun Bussola(angulo: Float) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$angulo°",
        )
        Image(
            painter = painterResource(R.drawable.bussola),
            modifier = Modifier.rotate(angulo).fillMaxWidth(),
            contentDescription = null,
        )

    }

}
