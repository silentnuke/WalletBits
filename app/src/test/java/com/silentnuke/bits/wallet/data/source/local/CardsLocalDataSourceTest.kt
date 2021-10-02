package com.silentnuke.bits.wallet.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.silentnuke.bits.wallet.MainCoroutineRule
import com.silentnuke.bits.wallet.data.Card
import com.silentnuke.bits.wallet.data.Result.Success
import com.silentnuke.bits.wallet.data.source.TestData
import com.silentnuke.bits.wallet.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class CardsLocalDataSourceTest {

    private lateinit var localDataSource: CardsLocalDataSource
    private lateinit var database: WalletDatabase

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WalletDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource = CardsLocalDataSource(database.cardsDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveCard_retrievesCard() = runBlockingTest {
        // GIVEN - a new card saved in the database
        val testCard = TestData.card1
        localDataSource.saveCard(testCard)

        // WHEN  - Card retrieved by ID
        val result = localDataSource.getCard(testCard.id)

        // THEN - Same card is returned
        assertEquals(true, result.succeeded)
        result as Success
        assertEquals(testCard.holder, result.data.holder)
        assertEquals(testCard.number, result.data.number)
        assertEquals(testCard.expiryDate, result.data.expiryDate)
        assertEquals(testCard.cvv, result.data.cvv)
    }

    @Test
    fun deleteAllCards_emptyListOfRetrievedCard() = runBlockingTest {
        // Given a new card in the persistent repository and a mocked callback
        val newCard = TestData.card1

        localDataSource.saveCard(newCard)

        // When all cards are deleted
        localDataSource.deleteAllCards()

        // Then the retrieved cards is an empty list
        val result = localDataSource.getCards() as Success
        assertEquals(true, result.data.isEmpty())
    }

    @Test
    fun getCards_retrieveSavedCards() = runBlockingTest {
        // Given 2 new cards in the persistent repository
        val newCard1 = TestData.card1
        val newCard2 = TestData.card2

        localDataSource.saveCard(newCard1)
        localDataSource.saveCard(newCard2)
        // Then the cards can be retrieved from the persistent repository
        val results = localDataSource.getCards() as Success<List<Card>>
        val cards = results.data
        assertEquals(2, cards.size)
    }
}
