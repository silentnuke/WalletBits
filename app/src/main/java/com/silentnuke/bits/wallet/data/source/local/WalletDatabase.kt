package com.silentnuke.bits.wallet.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.silentnuke.bits.wallet.data.Card

@Database(entities = [Card::class], version = 1, exportSchema = true)
abstract class WalletDatabase : RoomDatabase() {

    abstract fun cardsDao(): CardsDao
}
