package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "credit_history")
data class CreditTransaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val action: String,
    val creditsChange: Int,
    val balanceAfter: Int,
    val note: String,
    val createdAt: Long = System.currentTimeMillis()
)
