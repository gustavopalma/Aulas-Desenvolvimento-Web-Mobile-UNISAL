package com.gustavo.persistenciadb.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pessoa")
data class Pessoa(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var nome: String,
    var idade: Int,
    var email: String
)