package com.silentnuke.bits.wallet.feature.addcard

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.silentnuke.bits.wallet.R
import com.silentnuke.bits.wallet.WalletActivity
import com.silentnuke.bits.wallet.data.Result
import com.silentnuke.bits.wallet.data.source.CardsRepository
import com.silentnuke.bits.wallet.data.source.getTasksBlocking
import com.silentnuke.bits.wallet.data.source.TestData
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class AddCardFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: CardsRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun emptyCardHolderName_isNotSaved() {
        // GIVEN - On the "Add Card" screen.
        launchActivity()

        // WHEN - Enter invalid card holder name and click save
        onView(withId(R.id.card_form_cardholder_name)).perform(clearText())
        onView(withId(R.id.menu_done)).perform(click())

        // THEN - Entered Card is still displayed (a correct card would close it).
        onView(withId(R.id.card_form_card_number)).check(matches(isDisplayed()))
    }

    @Test
    fun emptyCardNumber_isNotSaved() {
        // GIVEN - On the "Add Card" screen.
        launchActivity()

        // WHEN - Enter invalid card number and click save
        onView(withId(R.id.card_form_cardholder_name)).perform(replaceText(TestData.card1.holder))
        onView(withId(R.id.card_form_card_number)).perform(clearText())
        onView(withId(R.id.menu_done)).perform(click())

        // THEN - Entered Card is still displayed (a correct card would close it).
        onView(withId(R.id.card_form_card_number)).check(matches(isDisplayed()))
    }

    @Test
    fun emptyExpiryDate_isNotSaved() {
        // GIVEN - On the "Add Card" screen.
        launchActivity()

        // WHEN - Enter invalid expiry date and click save
        onView(withId(R.id.card_form_cardholder_name)).perform(replaceText(TestData.card1.holder))
        onView(withId(R.id.card_form_card_number)).perform(replaceText(TestData.card1.number))
        onView(withId(R.id.card_form_expiration)).perform(clearText())
        onView(withId(R.id.menu_done)).perform(click())

        // THEN - Entered Card is still displayed (a correct card would close it).
        onView(withId(R.id.card_form_card_number)).check(matches(isDisplayed()))
    }

    @Test
    fun emptyCVV_isNotSaved() {
        // GIVEN - On the "Add Card" screen.
        launchActivity()

        // WHEN - Enter invalid cvv and click save
        onView(withId(R.id.card_form_cardholder_name)).perform(replaceText(TestData.card1.holder))
        onView(withId(R.id.card_form_card_number)).perform(replaceText(TestData.card1.number))
        onView(withId(R.id.card_form_expiration)).perform(replaceText(TestData.card1.expiryDate))
        onView(withId(R.id.card_form_cvv)).perform(clearText())
        onView(withId(R.id.menu_done)).perform(click())

        // THEN - Entered Card is still displayed (a correct card would close it).
        onView(withId(R.id.card_form_card_number)).check(matches(isDisplayed()))
    }

    @Test
    fun validCard_navigatesBackAfterSaved() {
        // GIVEN - On the "Add Card" screen.
        launchActivity()

        // WHEN - Enter all valid data and click save
        onView(withId(R.id.card_form_cardholder_name)).perform(replaceText(TestData.card1.holder))
        onView(withId(R.id.card_form_card_number)).perform(replaceText(TestData.card1.number))
        onView(withId(R.id.card_form_expiration)).perform(replaceText(TestData.card1.expiryDate))
        onView(withId(R.id.card_form_cvv)).perform(replaceText(TestData.card1.cvv))
        onView(withId(R.id.menu_done)).perform(click())

        // THEN - Verify that we navigated back to the cards screen.
        onView(withId(R.id.cards_container_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun validCard_isSaved() {
        // GIVEN - On the "Add Card" screen.
        launchActivity()

        // WHEN - Enter all valid data and click save
        onView(withId(R.id.card_form_cardholder_name)).perform(replaceText(TestData.card1.holder))
        onView(withId(R.id.card_form_card_number)).perform(replaceText(TestData.card1.number))
        onView(withId(R.id.card_form_expiration)).perform(replaceText(TestData.card1.expiryDate))
        onView(withId(R.id.card_form_cvv)).perform(replaceText(TestData.card1.cvv))
        onView(withId(R.id.menu_done)).perform(click())

        // THEN - Verify that the repository saved the card
        val cards = (repository.getTasksBlocking(true) as Result.Success).data
        assertEquals(1, cards.size)
        assertEquals(TestData.card1.holder, cards[0].holder)
        assertEquals(TestData.card1.number, cards[0].number)
        assertEquals(TestData.card1.expiryDate, cards[0].expiryDate)
        assertEquals(TestData.card1.cvv, cards[0].cvv)
    }

    @Test
    fun validCard_isNotSaved_ifPressBack() {
        // GIVEN - On the "Add Card" screen.
        launchActivity()

        // WHEN - Enter all valid data and click save
        onView(withId(R.id.card_form_cardholder_name)).perform(replaceText(TestData.card1.holder))
        onView(withId(R.id.card_form_card_number)).perform(replaceText(TestData.card1.number))
        onView(withId(R.id.card_form_expiration)).perform(replaceText(TestData.card1.expiryDate))
        onView(withId(R.id.card_form_cvv)).perform(replaceText(TestData.card1.cvv))
        Espresso.pressBack();

        // THEN - Verify that the repository saved the card
        val cards = (repository.getTasksBlocking(true) as Result.Success).data
        assertEquals(cards.size, 0)
        onView(withId(R.id.cards_container_layout)).check(matches(isDisplayed()))
    }

    private fun launchActivity(): ActivityScenario<WalletActivity>? {
        val activityScenario = ActivityScenario.launch(WalletActivity::class.java)
        activityScenario.onActivity { activity ->
            (activity.findViewById(R.id.cards_list) as RecyclerView).itemAnimator = null
        }
        onView(withId(R.id.menu_add)).perform(click())
        return activityScenario
    }
}
