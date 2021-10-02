package com.silentnuke.bits.wallet.data.source

import com.silentnuke.bits.wallet.data.Card
import kotlinx.coroutines.runBlocking

fun CardsRepository.saveTaskBlocking(card: Card) = runBlocking {
    this@saveTaskBlocking.saveCard(card)
}

fun CardsRepository.getTasksBlocking(forceUpdate: Boolean) = runBlocking {
    this@getTasksBlocking.getCards(forceUpdate)
}
