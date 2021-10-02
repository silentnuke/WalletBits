package com.silentnuke.bits.wallet.feature.cardslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.silentnuke.bits.wallet.*
import com.silentnuke.bits.wallet.data.Card
import com.silentnuke.bits.wallet.data.source.FakeRepository
import com.silentnuke.bits.wallet.data.source.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [CardsListViewModel]
 */
@ExperimentalCoroutinesApi
class CardsListViewModelTest {

    private lateinit var cardsListViewModel: CardsListViewModel

    private lateinit var cardsRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        cardsRepository = FakeRepository()
        cardsRepository.addCards(TestData.card1, TestData.card2)
        cardsListViewModel = CardsListViewModel(cardsRepository)
    }

    @Test
    fun loadAllCardsFromRepository_loadingTogglesAndDataLoaded() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Trigger loading of cards
        cardsListViewModel.loadCards(true)
        // Observe the items to keep LiveData emitting
        cardsListViewModel.items.observeForTesting {

            // Then progress indicator is shown
            assertEquals(true, cardsListViewModel.dataLoading.getOrAwaitValue())

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // Then progress indicator is hidden
            assertEquals(false, cardsListViewModel.dataLoading.getOrAwaitValue())

            // And data correctly loaded
            assertEquals(2, cardsListViewModel.items.getOrAwaitValue().size)
        }
    }

    @Test
    fun loadCards_error() {
        // Make the repository return errors
        cardsRepository.setReturnError(true)

        // Load cards
        cardsListViewModel.loadCards(true)
        // Observe the items to keep LiveData emitting
        cardsListViewModel.items.observeForTesting {

            // Then progress indicator is hidden
            assertEquals(false, cardsListViewModel.dataLoading.getOrAwaitValue())

            // And the list of items is empty
            assertEquals(true, cardsListViewModel.items.getOrAwaitValue().isEmpty())

            // And the snackbar updated
            assertSnackbarMessage(cardsListViewModel.snackbarText, R.string.loading_cards_error)
        }
    }

    @Test
    fun clickOnAdd_showsAddCardUi() {
        // When adding a new card
        cardsListViewModel.addNewCard()

        // Then the event is triggered
        val value = cardsListViewModel.newCardEvent.getOrAwaitValue()
        assertEquals(true, value.getContentIfNotHandled() != null)
    }

    @Test
    fun clickOnOpenCard_setsEvent() {
        // When opening a new card
        val cardId = "12345"
        cardsListViewModel.openCard(cardId)

        // Then the event is triggered
        assertLiveDataEventTriggered(cardsListViewModel.openCardEvent, cardId)
    }

    @Test
    fun showResultMessages_addOk_snackbarUpdated() {
        // When the viewmodel receives a result from another destination
        cardsListViewModel.showResultMessage(ADD_RESULT_OK)

        // The snackbar is updated
        assertSnackbarMessage(
            cardsListViewModel.snackbarText, R.string.successfully_added_card_message
        )
    }

    @Test
    fun showResultMessages_deleteOk_snackbarUpdated() {
        // When the viewmodel receives a result from another destination
        cardsListViewModel.showResultMessage(DELETE_RESULT_OK)

        // The snackbar is updated
        assertSnackbarMessage(cardsListViewModel.snackbarText, R.string.successfully_deleted_card_message)
    }

    @Test
    fun deleteCard_dataAndSnackbarUpdated() {
        // With a repository that has an active card
        val card = Card("Title", "Description")
        cardsRepository.addCards(card)

        // Delete card
        cardsListViewModel.deleteCard(card)

        // Verify the card is completed
        assertEquals(null, cardsRepository.cardsServiceData[card.id])

        // The snackbar is updated
        assertSnackbarMessage(
            cardsListViewModel.snackbarText, R.string.card_deleted
        )
    }

}
