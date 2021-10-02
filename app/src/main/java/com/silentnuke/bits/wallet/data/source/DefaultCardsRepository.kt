package com.silentnuke.bits.wallet.data.source

import androidx.lifecycle.LiveData
import com.silentnuke.bits.wallet.data.Result
import com.silentnuke.bits.wallet.data.Card
import com.silentnuke.bits.wallet.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultCardsRepository(
    private val cardsLocalDataSource: CardsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CardsRepository {

    override suspend fun getCards(forceUpdate: Boolean): Result<List<Card>> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                try {
                    updateTasksFromRemoteDataSource()
                } catch (ex: Exception) {
                    return Result.Error(ex)
                }
            }
            return cardsLocalDataSource.getCards()
        }
    }

    override suspend fun refreshCards() {
        updateTasksFromRemoteDataSource()
    }

    override fun observeCards(): LiveData<Result<List<Card>>> {
        return cardsLocalDataSource.observeCards()
    }

    override suspend fun refreshCard(cardId: String) {
        updateTaskFromRemoteDataSource(cardId)
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        // TODO: sync with back if needed
    }

    override fun observeCard(cardId: String): LiveData<Result<Card>> {
        return cardsLocalDataSource.observeCard(cardId)
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        // TODO: sync with back if need
    }

    /**
     * Relies on [getCards] to fetch data and picks the task with the same ID.
     */
    override suspend fun getCard(cardId: String, forceUpdate: Boolean): Result<Card> {
        // Set app as busy while this function executes.
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                updateTaskFromRemoteDataSource(cardId)
            }
            return cardsLocalDataSource.getCard(cardId)
        }
    }

    override suspend fun saveCard(card: Card) {
        coroutineScope {
            launch { cardsLocalDataSource.saveCard(card) }
        }
    }

    override suspend fun deleteAllCards() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { cardsLocalDataSource.deleteAllCards() }
            }
        }
    }

    override suspend fun deleteCard(cardId: String) {
        coroutineScope {
            launch { cardsLocalDataSource.deleteCard(cardId) }
        }
    }

}
