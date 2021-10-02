package com.silentnuke.bits.wallet.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.silentnuke.bits.wallet.MainCoroutineRule
import com.silentnuke.bits.wallet.data.Card
import com.silentnuke.bits.wallet.data.source.TestData
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
@SmallTest
class CardsDaoTest {

    private lateinit var database: WalletDatabase

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            WalletDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertCardAndGetById() = runBlockingTest {
        // GIVEN - insert a card
        val card = TestData.card1
        database.cardsDao().insertCard(card)

        // WHEN - Get the card by id from the database
        val loaded = database.cardsDao().getCardById(card.id)

        // THEN - The loaded data contains the expected values
        assertEquals(card.id, loaded?.id)
        assertEquals(card.holder, loaded?.holder)
        assertEquals(card.number, loaded?.number)
        assertEquals(card.cvv, loaded?.cvv)
    }

    @Test
    fun insertCardReplacesOnConflict() = runBlockingTest {
        // Given that a card is inserted
        val card = TestData.card1
        database.cardsDao().insertCard(card)

        // When a card with the same id is inserted
        val newCard = Card("test", "1234", "1025", "123", card.id)
        database.cardsDao().insertCard(newCard)

        // THEN - The loaded data contains the expected values
        val loaded = database.cardsDao().getCardById(card.id)
        assertEquals(card.id, loaded?.id)
        assertEquals("test", loaded?.holder)
        assertEquals("1234", loaded?.number)
        assertEquals("1025", loaded?.expiryDate)
        assertEquals("123", loaded?.cvv)
    }

    @Test
    fun insertCardAndGetCards() = runBlockingTest {
        // GIVEN - insert a card
        val card = TestData.card1
        database.cardsDao().insertCard(card)

        // WHEN - Get cards from the database
        val cards = database.cardsDao().getCards()

        // THEN - There is only 1 card in the database, and contains the expected values
        assertEquals(1, cards.size)
        assertEquals(card.id, cards[0].id)
        assertEquals(card.holder, cards[0].holder)
        assertEquals(card.number, cards[0].number)
        assertEquals(card.expiryDate, cards[0].expiryDate)
        assertEquals(card.cvv, cards[0].cvv)
    }

    @Test
    fun deleteCardByIdAndGettingCards() = runBlockingTest {
        // Given a card inserted
        val card = TestData.card1
        database.cardsDao().insertCard(card)

        // When deleting a card by id
        database.cardsDao().deleteCardById(card.id)

        // THEN - The list is empty
        val cards = database.cardsDao().getCards()
        assertEquals(true, cards.isEmpty())
    }

    @Test
    fun deleteCardsAndGettingCards() = runBlockingTest {
        // Given a card inserted
        database.cardsDao().insertCard(TestData.card1)

        // When deleting all cards
        database.cardsDao().deleteCards()

        // THEN - The list is empty
        val cards = database.cardsDao().getCards()
        assertEquals(true, cards.isEmpty())
    }

}
