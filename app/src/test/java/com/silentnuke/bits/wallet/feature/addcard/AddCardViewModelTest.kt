package com.silentnuke.bits.wallet.feature.addcard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.silentnuke.bits.wallet.MainCoroutineRule
import com.silentnuke.bits.wallet.R
import com.silentnuke.bits.wallet.assertSnackbarMessage
import com.silentnuke.bits.wallet.data.source.FakeRepository
import com.silentnuke.bits.wallet.data.source.TestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [AddCardViewModel].
 */
@ExperimentalCoroutinesApi
class AddCardViewModelTest {

    private lateinit var addCardViewModel: AddCardViewModel

    private lateinit var cardsRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each card synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val card = TestData.card1

    @Before
    fun setupViewModel() {
        cardsRepository = FakeRepository()
        addCardViewModel = AddCardViewModel(cardsRepository)
    }

    @Test
    fun saveNewCardToRepository_showsSuccessMessageUi() {
        (addCardViewModel).apply {
            holder.value = card.holder
            cardNumber.value = card.number
            expirationDate.value = card.expiryDate
            cvv.value = card.cvv
        }
        addCardViewModel.saveCard()

        val newCard = cardsRepository.cardsServiceData.values.first()

        // Then a card is saved in the repository and the view updated
        assertEquals(card.holder, newCard.holder)
        assertEquals(card.number, newCard.number)
        assertEquals(card.expiryDate, newCard.expiryDate)
        assertEquals(card.cvv, newCard.cvv)
    }

    @Test
    fun saveNewCardToRepository_emptyHolder_error() {
        saveCardAndAssertSnackbarError("", "Some Card Description", R.string.empty_holder_message)
    }

    @Test
    fun saveNewCardToRepository_nullHolder_error() {
        saveCardAndAssertSnackbarError(null, "Some Card Description", R.string.empty_holder_message)
    }

    @Test
    fun saveNewCardToRepository_emptyNumber_error() {
        saveCardAndAssertSnackbarError("Title", "", R.string.empty_number_message)
    }

    @Test
    fun saveNewCardToRepository_nullNumber_error() {
        saveCardAndAssertSnackbarError("Title", null, R.string.empty_number_message)
    }

    @Test
    fun saveNewCardToRepository_nullHolderNullNumber_error() {
        saveCardAndAssertSnackbarError(null, null, R.string.empty_holder_message)
    }

    @Test
    fun saveNewCardToRepository_emptyHolderEmptyNumber_error() {
        saveCardAndAssertSnackbarError("", "", R.string.empty_holder_message)
    }

    private fun saveCardAndAssertSnackbarError(title: String?, description: String?, error: Int) {
        (addCardViewModel).apply {
            this.holder.value = title
            this.cardNumber.value = description
        }

        // When saving an incomplete card
        addCardViewModel.saveCard()

        // Then the snackbar shows an error
        assertSnackbarMessage(addCardViewModel.snackbarText, error)
    }
}
