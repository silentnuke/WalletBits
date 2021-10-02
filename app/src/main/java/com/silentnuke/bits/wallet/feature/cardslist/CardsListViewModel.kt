package com.silentnuke.bits.wallet.feature.cardslist

import androidx.lifecycle.*
import com.silentnuke.bits.wallet.ADD_RESULT_OK
import com.silentnuke.bits.wallet.DELETE_RESULT_OK
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
class CardsListViewModel @Inject constructor(
    private val cardsRepository: CardsRepository
) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    private val _items: LiveData<List<Card>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                cardsRepository.refreshCards()
                _dataLoading.value = false
            }
        }
        cardsRepository.observeCards().distinctUntilChanged().switchMap { computeResult(it) }
    }

    val items: LiveData<List<Card>> = _items
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Any>>()
    val snackbarText: LiveData<Event<Any>> = _snackbarText

    private val _openCardEvent = MutableLiveData<Event<String>>()
    val openCardEvent: LiveData<Event<String>> = _openCardEvent

    private val _newCardEvent = MutableLiveData<Event<Unit>>()
    val newCardEvent: LiveData<Event<Unit>> = _newCardEvent

    private var resultMessageShown: Boolean = false

    init {
        loadCards(true)
    }

    fun addNewCard() {
        _newCardEvent.value = Event(Unit)
    }

    fun openCard(taskId: String) {
        _openCardEvent.value = Event(taskId)
    }

    fun deleteCard(card: Card) = viewModelScope.launch {
        cardsRepository.deleteCard(card.id)
        showSnackbarMessage(R.string.card_deleted)
    }

    fun showResultMessage(result: Int) {
        if (resultMessageShown) return
        when (result) {
            ADD_RESULT_OK -> showSnackbarMessage(R.string.successfully_added_card_message)
            DELETE_RESULT_OK -> showSnackbarMessage(R.string.successfully_deleted_card_message)
        }
        resultMessageShown = true
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun computeResult(cardsResult: Result<List<Card>>): LiveData<List<Card>> {
        val result = MutableLiveData<List<Card>>()

        if (cardsResult is Success) {
            result.value = cardsResult.data!!
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.loading_cards_error)
        }

        return result
    }

    fun loadCards(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    fun refresh() {
        _forceUpdate.value = true
    }

}
