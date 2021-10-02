package com.silentnuke.bits.wallet.data.source

import androidx.lifecycle.LiveData
import com.silentnuke.bits.wallet.data.Result
import com.silentnuke.bits.wallet.data.Card

interface CardsRepository {

    fun observeCards(): LiveData<Result<List<Card>>>

    suspend fun getCards(forceUpdate: Boolean = false): Result<List<Card>>

    suspend fun refreshCards()

    fun observeCard(cardId: String): LiveData<Result<Card>>

    suspend fun getCard(cardId: String, forceUpdate: Boolean = false): Result<Card>

    suspend fun refreshCard(cardId: String)

    suspend fun saveCard(card: Card)

    suspend fun deleteAllCards()

    suspend fun deleteCard(cardId: String)
}
