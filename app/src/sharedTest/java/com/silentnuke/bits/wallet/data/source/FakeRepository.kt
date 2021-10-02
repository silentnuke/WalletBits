package com.silentnuke.bits.wallet.data.source

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.silentnuke.bits.wallet.data.Result
import com.silentnuke.bits.wallet.data.Result.Error
import com.silentnuke.bits.wallet.data.Result.Success
import com.silentnuke.bits.wallet.data.Card
import kotlinx.coroutines.runBlocking
import java.util.LinkedHashMap
import javax.inject.Inject

class FakeRepository @Inject constructor() : CardsRepository {

    var cardsServiceData: LinkedHashMap<String, Card> = LinkedHashMap()

    private var shouldReturnError = false

    private val observableCards = MutableLiveData<Result<List<Card>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun refreshCards() {
        observableCards.value = getCards()
    }

    override suspend fun refreshCard(cardId: String) {
        refreshCards()
    }

    override fun observeCards(): LiveData<Result<List<Card>>> {
        runBlocking { refreshCards() }
        return observableCards
    }

    override fun observeCard(cardId: String): LiveData<Result<Card>> {
        runBlocking { refreshCards() }
        return observableCards.map { cards ->
            when (cards) {
                is Result.Loading -> Result.Loading
                is Error -> Error(cards.exception)
                is Success -> {
                    val card = cards.data.firstOrNull() { it.id == cardId }
                        ?: return@map Error(Exception("Not found"))
                    Success(card)
                }
            }
        }
    }

    override suspend fun getCard(cardId: String, forceUpdate: Boolean): Result<Card> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        cardsServiceData[cardId]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find card"))
    }

    override suspend fun getCards(forceUpdate: Boolean): Result<List<Card>> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        return Success(cardsServiceData.values.toList())
    }

    override suspend fun saveCard(card: Card) {
        cardsServiceData[card.id] = card
    }

    override suspend fun deleteCard(cardId: String) {
        cardsServiceData.remove(cardId)
        refreshCards()
    }

    override suspend fun deleteAllCards() {
        cardsServiceData.clear()
        refreshCards()
    }

    @VisibleForTesting
    fun addCards(vararg cards: Card) {
        for (card in cards) {
            cardsServiceData[card.id] = card
        }
        runBlocking { refreshCards() }
    }
}
