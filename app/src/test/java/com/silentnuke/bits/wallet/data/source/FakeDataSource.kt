package com.silentnuke.bits.wallet.data.source

import androidx.lifecycle.LiveData
import com.silentnuke.bits.wallet.data.Result
import com.silentnuke.bits.wallet.data.Result.Error
import com.silentnuke.bits.wallet.data.Result.Success
import com.silentnuke.bits.wallet.data.Card

class FakeDataSource(var cards: MutableList<Card>? = mutableListOf()) : CardsDataSource {
    override suspend fun getCards(): Result<List<Card>> {
        cards?.let { return Success(ArrayList(it)) }
        return Error(
            Exception("Tasks not found")
        )
    }

    override suspend fun getCard(cardId: String): Result<Card> {
        cards?.firstOrNull { it.id == cardId }?.let { return Success(it) }
        return Error(
            Exception("Task not found")
        )
    }

    override suspend fun saveCard(card: Card) {
        cards?.add(card)
    }

    override suspend fun deleteAllCards() {
        cards?.clear()
    }

    override suspend fun deleteCard(cardId: String) {
        cards?.removeIf { it.id == cardId }
    }

    override fun observeCards(): LiveData<Result<List<Card>>> {
        TODO("not implemented")
    }

    override fun observeCard(cardId: String): LiveData<Result<Card>> {
        TODO("not implemented")
    }

}
