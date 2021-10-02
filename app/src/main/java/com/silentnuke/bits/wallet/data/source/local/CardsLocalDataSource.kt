package com.silentnuke.bits.wallet.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.silentnuke.bits.wallet.data.Result
import com.silentnuke.bits.wallet.data.Result.Error
import com.silentnuke.bits.wallet.data.Result.Success
import com.silentnuke.bits.wallet.data.Card
import com.silentnuke.bits.wallet.data.source.CardsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardsLocalDataSource internal constructor(
    private val cardsDao: CardsDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CardsDataSource {

    override fun observeCards(): LiveData<Result<List<Card>>> {
        return cardsDao.observeCards().map {
            Success(it)
        }
    }

    override fun observeCard(cardId: String): LiveData<Result<Card>> {
        return cardsDao.observeCardById(cardId).map {
            Success(it)
        }
    }

    override suspend fun getCards(): Result<List<Card>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(cardsDao.getCards())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getCard(cardId: String): Result<Card> = withContext(ioDispatcher) {
        try {
            val card = cardsDao.getCardById(cardId)
            if (card != null) {
                return@withContext Success(card)
            } else {
                return@withContext Error(Exception("Card not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun saveCard(card: Card) = withContext(ioDispatcher) {
        cardsDao.insertCard(card)
    }

    override suspend fun deleteAllCards() = withContext(ioDispatcher) {
        cardsDao.deleteCards()
    }

    override suspend fun deleteCard(cardId: String) = withContext<Unit>(ioDispatcher) {
        cardsDao.deleteCardById(cardId)
    }
}
