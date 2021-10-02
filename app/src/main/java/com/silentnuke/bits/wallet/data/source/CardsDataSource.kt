package com.silentnuke.bits.wallet.data.source

import androidx.lifecycle.LiveData
import com.silentnuke.bits.wallet.data.Card
import com.silentnuke.bits.wallet.data.Result

interface CardsDataSource {

    fun observeCards(): LiveData<Result<List<Card>>>

    suspend fun getCards(): Result<List<Card>>

    fun observeCard(cardId: String): LiveData<Result<Card>>

    suspend fun getCard(cardId: String): Result<Card>

    suspend fun saveCard(card: Card)

    suspend fun deleteAllCards()

    suspend fun deleteCard(cardId: String)
}
