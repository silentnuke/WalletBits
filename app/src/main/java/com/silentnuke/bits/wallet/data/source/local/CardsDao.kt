package com.silentnuke.bits.wallet.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.silentnuke.bits.wallet.data.Card

@Dao
interface CardsDao {

    @Query("SELECT * FROM Cards")
    fun observeCards(): LiveData<List<Card>>

    @Query("SELECT * FROM Cards WHERE entryid = :cardId")
    fun observeCardById(cardId: String): LiveData<Card>

    @Query("SELECT * FROM Cards")
    suspend fun getCards(): List<Card>

    @Query("SELECT * FROM Cards WHERE entryid = :cardId")
    suspend fun getCardById(cardId: String): Card?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card)

    @Query("DELETE FROM Cards WHERE entryid = :cardId")
    suspend fun deleteCardById(cardId: String): Int

    @Query("DELETE FROM Cards")
    suspend fun deleteCards()

}
