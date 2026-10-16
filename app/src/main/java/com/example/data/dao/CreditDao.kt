package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CreditTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditDao {
    @Query("SELECT * FROM credit_history ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<CreditTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CreditTransaction)

    @Query("SELECT * FROM credit_history ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestTransaction(): CreditTransaction?

    @Query("SELECT COUNT(*) FROM credit_history")
    suspend fun getTransactionCount(): Int
}
