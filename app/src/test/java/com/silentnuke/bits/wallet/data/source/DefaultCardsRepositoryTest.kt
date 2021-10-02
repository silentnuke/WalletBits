package com.silentnuke.bits.wallet.data.source

import com.silentnuke.bits.wallet.MainCoroutineRule
import com.silentnuke.bits.wallet.data.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
@ExperimentalCoroutinesApi
class DefaultCardsRepositoryTest {

    private val card1 = TestData.card1
    private val card2 = TestData.card2
    private val localCards = listOf(card2).sortedBy { it.id }
    private lateinit var cardsLocalDataSource: FakeDataSource

    private lateinit var cardsRepository: DefaultCardsRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        cardsLocalDataSource = FakeDataSource(localCards.toMutableList())
        cardsRepository = DefaultCardsRepository(cardsLocalDataSource, Dispatchers.Main)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getCards_emptyRepositoryAndUninitializedCache() = mainCoroutineRule.runBlockingTest {
        val emptySource = FakeDataSource()
        val cardsRepository = DefaultCardsRepository(emptySource, Dispatchers.Main)

        assertEquals(true, cardsRepository.getCards() is Success)
    }

    @Test
    fun saveCard_savesToLocal() = mainCoroutineRule.runBlockingTest {
        // Make sure newCard is not in the local datasource
        assertEquals(false, cardsLocalDataSource.cards?.contains(card1))

        // When a card is saved to the cards repository
        cardsRepository.saveCard(card1)

        // Then the local source contains new card
        assertEquals(true, cardsLocalDataSource.cards?.contains(card1))
    }

    @Test
    fun deleteAllCards() = mainCoroutineRule.runBlockingTest {
        val initialCards = (cardsRepository.getCards() as? Success)?.data

        // Delete all cards
        cardsRepository.deleteAllCards()

        // Fetch data again
        val afterDeleteCards = (cardsRepository.getCards() as? Success)?.data

        // Verify cards are empty now
        assertEquals(false, initialCards?.isEmpty())
        assertEquals(true, afterDeleteCards?.isEmpty())
    }

    @Test
    fun deleteSingleCard() = mainCoroutineRule.runBlockingTest {
        cardsRepository.saveCard(card1)
        val initialCards = (cardsRepository.getCards(true) as? Success)?.data

        // Delete first card
        cardsRepository.deleteCard(card2.id)

        // Fetch data again
        val afterDeleteCards = (cardsRepository.getCards(true) as? Success)?.data

        // Verify only one card was deleted
        assertEquals(initialCards!!.size - 1, afterDeleteCards?.size)
        assertEquals(false, afterDeleteCards?.contains(card2))
        assertEquals(true, afterDeleteCards?.contains(card1))
    }
}
