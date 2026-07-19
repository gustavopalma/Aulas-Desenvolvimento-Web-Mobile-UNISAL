package com.gustavo.persistenciadb.dao

import android.content.Context
import androidx.room.*
import com.gustavo.persistenciadb.db.DB
import com.gustavo.persistenciadb.domain.Pessoa

@Dao
interface PessoaDao {

    @Insert
    suspend fun inserir(pessoa: Pessoa)

    @Update
    suspend fun atualizar(pessoa: Pessoa)

    @Delete
    suspend fun deletar(pessoa: Pessoa)

    @Query("SELECT * FROM pessoa")
    suspend fun listarTodas(): List<Pessoa>

    @Query("SELECT * FROM pessoa WHERE id = :id")
    suspend fun buscarPorId(id: Long): Pessoa?
}