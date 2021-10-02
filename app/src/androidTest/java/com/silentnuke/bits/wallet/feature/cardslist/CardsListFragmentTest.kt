package com.silentnuke.bits.wallet.feature.cardslist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
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
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class CardsListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: CardsRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun displayCard_whenRepositoryHasData() {
        // GIVEN - One card already in the repository
        val testCard = TestData.card1
        repository.saveTaskBlocking(testCard)

        // WHEN - On startup
        launchActivity()

        // THEN - Verify task is displayed on screen
        onView(withText(testCard.titleForList)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteOneCard() {
        // GIVEN - One card already in the repository
        val testCard = TestData.card1
        repository.saveTaskBlocking(testCard)

        // WHEN - On startup
        launchActivity()
        onView(withText(testCard.titleForList)).check(matches(isDisplayed()))

        // Remove card
        onView(deleteButtonWithText(testCard.titleForList)).perform(click())

        // Verify it was deleted
        onView(withText(R.string.no_cards)).check(matches(isDisplayed()))
    }

    @Test
    fun deleteOneOfTwoCards() {
        // GIVEN - Two cards already in the repository
        val testCard = TestData.card1
        val testCard2 = TestData.card2
        repository.saveTaskBlocking(testCard)
        repository.saveTaskBlocking(testCard2)

        // WHEN - On startup
        launchActivity()
        onView(withText(testCard.titleForList)).check(matches(isDisplayed()))
        onView(withText(testCard2.titleForList)).check(matches(isDisplayed()))

        // Remove first card
        onView(deleteButtonWithText(testCard.titleForList)).perform(click())

        // Verify it was deleted
        onView(withText(testCard.titleForList)).check(doesNotExist())
        onView(withText(testCard2.titleForList)).check(matches(isDisplayed()))
    }

    @Test
    fun showAllCards() {
        // GIVEN - Two cards already in the repository
        val testCard = TestData.card1
        val testCard2 = TestData.card2
        repository.saveTaskBlocking(testCard)
        repository.saveTaskBlocking(testCard2)

        // WHEN - On startup
        launchActivity()

        // THEN - all card displayed
        onView(withText(testCard.titleForList)).check(matches(isDisplayed()))
        onView(withText(testCard2.titleForList)).check(matches(isDisplayed()))
    }

    @Test
    fun noCards_EmptyViewVisible() {
        launchActivity()

        // Verify the "You have no cards!" text is shown
        onView(withText(R.string.no_cards)).check(matches(isDisplayed()))
    }

    @Test
    fun clickAddCardButton_navigateToAddFragment() {
        // GIVEN - On the home screen
        launchActivity()

        // WHEN - Click on the "+" button
        onView(withId(R.id.menu_add)).perform(click())

        // THEN - Verify that we navigate to the add screen
        onView(withId(R.id.card_form_cardholder_name)).check(matches(isDisplayed()))
    }

    private fun launchActivity(): ActivityScenario<WalletActivity>? {
        val activityScenario = launch(WalletActivity::class.java)
        activityScenario.onActivity { activity ->
            // Disable animations in RecyclerView
            (activity.findViewById(R.id.cards_list) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }

    private fun deleteButtonWithText(text: String): Matcher<View> {
        return allOf(withId(R.id.delete_button), hasSibling(withText(text)))
    }
}
