package com.gustavo.persistenciadb

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.gustavo.persistenciadb.db.DB
import com.gustavo.persistenciadb.domain.Pessoa
import com.gustavo.persistenciadb.ui.theme.PersistenciaDBTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val pessoas = mutableStateListOf<Pessoa>()

    private var mostrarDialog by mutableStateOf(false)

    private var pessoaSelecionada by mutableStateOf<Pessoa?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PersistenciaDBTheme {
                TelaPrincipal()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            listar()
        }
    }

    private suspend fun listar() {
        val db = DB.DatabaseProvider.getDatabase(this)
        val dao = db.pessoaDao()

        val resultado = dao.listarTodas()

        pessoas.clear()
        pessoas.addAll(resultado)
    }

    @Composable
    private fun TelaPrincipal() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),

            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(
                            this@MainActivity,
                            CadastroActivity::class.java
                        )

                        startActivity(intent)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar pessoa"
                    )
                }
            }
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(
                    items = pessoas,
                    key = { pessoa -> pessoa.id }
                ) { pessoa ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .combinedClickable(
                                onClick = {
                                    val intent = Intent(
                                        this@MainActivity,
                                        CadastroActivity::class.java
                                    ).apply {
                                        putExtra("pessoa", pessoa.id)
                                    }

                                    startActivity(intent)
                                },

                                onLongClick = {
                                    pessoaSelecionada = pessoa
                                    mostrarDialog = true
                                }
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = pessoa.nome,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = pessoa.email,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (mostrarDialog) {
                ConfirmarExclusao(
                    pessoa = pessoaSelecionada,

                    onConfirmar = {
                        excluirPessoaSelecionada()
                    },

                    onCancelar = {
                        pessoaSelecionada = null
                        mostrarDialog = false
                    }
                )
            }
        }
    }

    private fun excluirPessoaSelecionada() {
        val pessoa = pessoaSelecionada ?: return

        lifecycleScope.launch {
            val db = DB.DatabaseProvider.getDatabase(this@MainActivity)
            val dao = db.pessoaDao()

            dao.deletar(pessoa)

            listar()

            pessoaSelecionada = null
            mostrarDialog = false
        }
    }

    @Composable
    private fun ConfirmarExclusao(
        pessoa: Pessoa?,
        onConfirmar: () -> Unit,
        onCancelar: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onCancelar,

            title = {
                Text("Excluir pessoa")
            },

            text = {
                Text(
                    text = "Deseja realmente excluir ${pessoa?.nome.orEmpty()}?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = onConfirmar
                ) {
                    Text("Excluir")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = onCancelar
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
