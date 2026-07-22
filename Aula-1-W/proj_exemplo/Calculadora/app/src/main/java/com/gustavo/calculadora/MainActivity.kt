package com.gustavo.calculadora

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gustavo.calculadora.ui.theme.CalculadoraTheme


class MainActivity : ComponentActivity() {

    val TAG = "Calculadora"
    var display by mutableStateOf("0")
    val pilhaOperador = mutableListOf<String>()
    val pilhaOperando= mutableListOf<String>()
    var aguardandoOperando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            CalculadoraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    makeCalc(display = display)
                }
            }
        }
    }


    enum class ButtonOp {
        ZERO,
        UM,
        DOIS,
        TRES,
        QUATRO,
        CINCO,
        SEIS,
        SETE,
        OITO,
        NOVE,
        VIRGULA,
        PERCENTUAL,
        DIVISAO,
        MULTIPLICACAO,
        SOMA,
        SUBTRACAO,
        IGUALDADE,
        LIMPAR
    }

    fun numClick(event: ButtonOp) {
        if (aguardandoOperando) {
            display = "0"
            aguardandoOperando = false
        }

        if ((display == "0") && (event.name == ButtonOp.ZERO.name)) {
            return
        }
        if (display.length == 1 && display == "0") {
            display = event.ordinal.toString();
        } else {
            display += event.ordinal.toString()
        }
    }

    fun opClick(event: ButtonOp) {
        if(event == ButtonOp.LIMPAR) {
            pilhaOperador.clear()
            pilhaOperando.clear()
            aguardandoOperando = false
            display = "0"
        }

        if (aguardandoOperando) {
            // usuário apertou outro operador sem digitar número
            // então apenas troca o operador
            if (pilhaOperador.isNotEmpty()) {
                pilhaOperador[pilhaOperador.lastIndex] = event.name
            }
            return
        }

        if (pilhaOperador.isEmpty()) {
            pilhaOperador.add(event.name)
            pilhaOperando.add(display)
        } else {
            eqClick(ButtonOp.IGUALDADE)
            pilhaOperador.add(event.name)
            pilhaOperando.add(display)
        }

        aguardandoOperando = true

    }

    fun eqClick(event: ButtonOp){
        if (pilhaOperador.isEmpty() || pilhaOperando.isEmpty()) {
            return
        }

        val operador = pilhaOperador.removeAt(pilhaOperador.lastIndex)
        val operando = pilhaOperando.removeAt(pilhaOperando.lastIndex).toFloat()
        val aux = display.toFloat()

        if (operador == ButtonOp.SOMA.name) {
            display = (operando + aux).toString()
        }

        aguardandoOperando = true
    }

    @Composable
    fun makeCalc(display: String) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                fontSize = 32.sp,
                text = display

            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                botaoPequeno("%", ButtonOp.PERCENTUAL)
                botaoPequeno("/", ButtonOp.DIVISAO)
                botaoPequeno("X", ButtonOp.MULTIPLICACAO)
                botaoPequeno("-", ButtonOp.SUBTRACAO)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                botaoPequeno("Pi", ButtonOp.PERCENTUAL)
                botaoPequeno("Sqrt", ButtonOp.DIVISAO)
                botaoPequeno("^", ButtonOp.MULTIPLICACAO)
                botaoPequeno("-/+", ButtonOp.SUBTRACAO)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        botaoPequeno("7", ButtonOp.SETE)
                        botaoPequeno("8", ButtonOp.OITO)
                        botaoPequeno("9", ButtonOp.NOVE)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        botaoPequeno("4", ButtonOp.QUATRO)
                        botaoPequeno("5", ButtonOp.CINCO)
                        botaoPequeno("6", ButtonOp.SEIS)
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    botaoGrande("+", ButtonOp.SOMA)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        botaoPequeno("1", ButtonOp.UM)
                        botaoPequeno("2", ButtonOp.DOIS)
                        botaoPequeno("3", ButtonOp.TRES)
                        botaoPequeno("=", ButtonOp.IGUALDADE)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        botaoDuplo("0", ButtonOp.ZERO)
                        botaoPequeno(".", ButtonOp.VIRGULA)
                        botaoPequeno("C", ButtonOp.LIMPAR)
                    }
                }
            }
        }


    }

    @Composable
    fun botaoPequeno(texto: String, event: ButtonOp) {

        Button(
            modifier = Modifier
                .width(80.dp)
                .height(50.dp),
            onClick = {
                if (event.ordinal <= ButtonOp.NOVE.ordinal || event.name == ButtonOp.VIRGULA.name){
                    numClick(event)
                } else {
                    opClick(event)
                }

            }
        ) {
            Text(texto)
        }
    }

    @Composable
    fun botaoGrande(texto: String, event: ButtonOp) {
        Button(
            modifier = Modifier
                .width(80.dp)
                .height(108.dp),

            onClick = {
                if ( event == ButtonOp.SOMA) {
                    opClick(event)
                } else {
                    eqClick(event)
                }
            },
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(texto)
        }
    }

    @Composable
    fun botaoDuplo(texto: String, event: ButtonOp) {
        Button(
            modifier = Modifier
                .width(168.dp)
                .height(50.dp),

            onClick = {
                numClick(event)
            },
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(texto)
        }
    }

}