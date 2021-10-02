package com.silentnuke.bits.wallet.feature.carddetails

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.silentnuke.bits.wallet.Event
import com.silentnuke.bits.wallet.R
import com.silentnuke.bits.wallet.data.Result
import com.silentnuke.bits.wallet.data.Result.Success
import com.silentnuke.bits.wallet.data.Card
import com.silentnuke.bits.wallet.data.source.CardsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    private val cardsRepository: CardsRepository
) : ViewModel() {

    private val _cardId = MutableLiveData<String>()
    private val _card = _cardId.switchMap { cardId ->
        cardsRepository.observeCard(cardId).map { computeResult(it) }
    }
    val card: LiveData<Card?> = _card

    val isDataAvailable: LiveData<Boolean> = _card.map { it != null }

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Any>>()
    val snackbarText: LiveData<Event<Any>> = _snackbarText

    fun start(cardId: String) {
        if (_dataLoading.value == true || cardId == _cardId.value) {
            return
        }

        _cardId.value = cardId
    }

    private fun computeResult(cardResult: Result<Card>): Card? {
        return if (cardResult is Success) {
            cardResult.data
        } else {
            showSnackbarMessage(R.string.loading_cards_error)
            null
        }
    }

    fun refresh() {
        _card.value?.let {
            _dataLoading.value = true
            viewModelScope.launch {
                cardsRepository.refreshCard(it.id)
                _dataLoading.value = false
            }
        }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = Event(message)
    }
}
