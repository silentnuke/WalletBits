package com.silentnuke.bits.wallet.feature.carddetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.silentnuke.bits.wallet.MainCoroutineRule
import com.silentnuke.bits.wallet.data.source.FakeRepository
import com.silentnuke.bits.wallet.data.source.TestData
import com.silentnuke.bits.wallet.getOrAwaitValue
import com.silentnuke.bits.wallet.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [CardDetailsViewModel]
 */
@ExperimentalCoroutinesApi
class CardDetailsViewModelTest {

    private lateinit var cardDetailsViewModel: CardDetailsViewModel

    private lateinit var cardsRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val card = TestData.card1

    @Before
    fun setupViewModel() {
        cardsRepository = FakeRepository()
        cardsRepository.addCards(card)

        cardDetailsViewModel = CardDetailsViewModel(cardsRepository)
    }

    @Test
    fun getCardFromRepositoryAndLoadIntoView() {
        cardDetailsViewModel.start(card.id)

        // Then verify that the view was notified
        assertEquals(card.holder, cardDetailsViewModel.card.getOrAwaitValue()?.holder)
        assertEquals(card.number, cardDetailsViewModel.card.getOrAwaitValue()?.number)
        assertEquals(card.expiryDate, cardDetailsViewModel.card.getOrAwaitValue()?.expiryDate)
        assertEquals(card.cvv, cardDetailsViewModel.card.getOrAwaitValue()?.cvv)
    }

    @Test
    fun cardDetailViewModel_repositoryError() {
        // Given a repository that returns errors
        cardsRepository.setReturnError(true)

        // Given an initialized ViewModel with a card
        cardDetailsViewModel.start(card.id)
        // Get the computed LiveData value
        cardDetailsViewModel.card.observeForTesting {
            // Then verify that data is not available
            assertEquals(false, cardDetailsViewModel.isDataAvailable.getOrAwaitValue())
        }
    }

    @Test
    fun loadCard_loading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Load the card in the viewmodel
        cardDetailsViewModel.start(card.id)
        // Start observing to compute transformations
        cardDetailsViewModel.card.observeForTesting {
            // Force a refresh to show the loading indicator
            cardDetailsViewModel.refresh()

            // Then progress indicator is shown
            assertEquals(true, cardDetailsViewModel.dataLoading.getOrAwaitValue())

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // Then progress indicator is hidden
            assertEquals(false, cardDetailsViewModel.dataLoading.getOrAwaitValue())
        }
    }
}
