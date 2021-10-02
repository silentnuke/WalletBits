package com.silentnuke.bits.wallet.feature.addcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silentnuke.bits.wallet.Event
import com.silentnuke.bits.wallet.R
import com.silentnuke.bits.wallet.data.Card
import com.silentnuke.bits.wallet.data.source.CardsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val cardsRepository: CardsRepository
) : ViewModel() {

    val holder = MutableLiveData<String?>()
    val holderError = MutableLiveData<String?>()
    val cardNumber = MutableLiveData<String?>()
    val cardNumberError = MutableLiveData<String?>()
    val expirationDate = MutableLiveData<String?>()
    val expirationDateError = MutableLiveData<String?>()
    val cvv = MutableLiveData<String?>()
    val cvvError = MutableLiveData<String?>()

    private val _snackbarText = MutableLiveData<Event<Any>>()
    val snackbarText: LiveData<Event<Any>> = _snackbarText

    private val _cardCreatedEvent = MutableLiveData<Event<Unit>>()
    val cardCreatedEvent: LiveData<Event<Unit>> = _cardCreatedEvent

    fun saveCard() {
        val currentHolder = holder.value
        val currentCardNumber = cardNumber.value
        val currentExpirationDate = expirationDate.value
        val currentCvv = cvv.value

        if (!isValid(currentHolder, holderError.value, R.string.empty_holder_message)) {
            return
        }

        if (!isValid(currentCardNumber, cardNumberError.value, R.string.empty_number_message)) {
            return
        }

        if (!isValid(currentExpirationDate, expirationDateError.value, R.string.empty_expiration_message)) {
            return
        }

        if (!isValid(currentCvv, cvvError.value, R.string.empty_cvv_message)) {
            return
        }

        createCard(Card(currentHolder!!, currentCardNumber!!, currentExpirationDate!!, currentCvv!!))
    }

    private fun isValid(value: String?, error: String?, errorDefault: Int): Boolean {
        if (value.isNullOrBlank() || !error.isNullOrBlank()) {
            _snackbarText.value = Event(if (error.isNullOrBlank()) errorDefault else error)
            return false
        }
        return true
    }

    private fun createCard(newCard: Card) = viewModelScope.launch {
        cardsRepository.saveCard(newCard)
        _cardCreatedEvent.value = Event(Unit)
    }

}
