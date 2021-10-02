package com.silentnuke.bits.wallet.feature.carddetails

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.silentnuke.bits.wallet.R
import com.silentnuke.bits.wallet.WalletActivity
import com.silentnuke.bits.wallet.data.source.CardsRepository
import com.silentnuke.bits.wallet.data.source.saveTaskBlocking
import com.silentnuke.bits.wallet.data.source.TestData
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@HiltAndroidTest
class CardDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: CardsRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun validCard_DisplayedInUi() {
        // GIVEN - Add Card to the DB
        val testCard = TestData.card1
        repository.saveTaskBlocking(testCard)

        // WHEN - Details fragment launched to display card
        launchActivity()
        onView(withText(testCard.titleForList)).perform(ViewActions.click())

        // THEN - Card details are displayed on the screen
        onView(withId(R.id.card_form_cardholder_name)).check(matches(isDisplayed()))
        onView(withId(R.id.card_form_cardholder_name)).check(matches(withText(testCard.holder)))
        onView(withId(R.id.card_form_card_number)).check(matches(isDisplayed()))
        onView(withId(R.id.card_form_card_number)).check(matches(withText(testCard.number)))
        onView(withId(R.id.card_form_expiration)).check(matches(isDisplayed()))
        onView(withId(R.id.card_form_expiration)).check(matches(withText(testCard.expiryDate)))
        onView(withId(R.id.card_form_cvv)).check(matches(isDisplayed()))
        onView(withId(R.id.card_form_cvv)).check(matches(withText(testCard.cvv)))
    }

    private fun launchActivity(): ActivityScenario<WalletActivity>? {
        val activityScenario = ActivityScenario.launch(WalletActivity::class.java)
        activityScenario.onActivity { activity ->
            (activity.findViewById(R.id.cards_list) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }
}
