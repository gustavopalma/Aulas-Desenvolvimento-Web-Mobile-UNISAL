package com.gustavo.persistenciadb

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.gustavo.persistenciadb.dao.PessoaDao
import com.gustavo.persistenciadb.db.DB
import com.gustavo.persistenciadb.domain.Pessoa
import com.gustavo.persistenciadb.ui.theme.PersistenciaDBTheme
import kotlinx.coroutines.launch

class CadastroActivity : ComponentActivity() {
    var pessoa by mutableStateOf(
        Pessoa(
            id = 0,
            nome = "",
            idade = 0,
            email = ""
        )
    )

    var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val idPessoa = intent.getLongExtra("pessoa",0)
        if (idPessoa != 0L) {
            edit = true
            lifecycleScope.launch {
                carregarDaados(idPessoa)
            }
        }

        setContent {
            PersistenciaDBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    criaCampos(
                        innerPadding
                    )
                }
            }
        }
    }

    suspend fun salvar() {
        val db = DB.DatabaseProvider.getDatabase(this@CadastroActivity)
        val dao = db.pessoaDao()

        dao.inserir(
            pessoa = pessoa
        )
    }

    suspend fun carregarDaados(id: Long?) {
        val db = DB.DatabaseProvider.getDatabase(this@CadastroActivity)
        val dao = db.pessoaDao()

        val p = dao.buscarPorId(id ?: 0);
        if (p != null) {
            pessoa = p
        }
    }

    suspend fun editar() {
        val db = DB.DatabaseProvider.getDatabase(this@CadastroActivity)
        val dao = db.pessoaDao()

        dao.atualizar(pessoa)

    }

    @Composable
    fun criaCampos(paddingValues: PaddingValues) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = pessoa.nome,
                onValueChange = { pessoa = pessoa.copy(nome = it) },
                label = {
                    Text("Nome")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = pessoa.idade.toString(),
                onValueChange = { pessoa = pessoa.copy(idade = it.toIntOrNull() ?: 0) },
                label = {
                    Text("Idade")
                },
                //importante para usabilidade e para ajudar na validação
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = pessoa.email,
                onValueChange = { pessoa = pessoa.copy(email = it) },
                label = {
                    Text("E-Mail")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(

            ) {
                Button(onClick = {
                    lifecycleScope.launch {
                        if (edit) {
                            editar()
                        } else {
                            salvar()
                        }
                    }
                }) {
                    if (edit) {
                        Text("Editar")
                    } else {
                        Text("Salvar")
                    }
                }
            }
        }
    }

}