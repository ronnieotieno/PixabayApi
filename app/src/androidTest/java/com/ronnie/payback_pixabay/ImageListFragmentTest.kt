package com.ronnie.payback_pixabay

import android.view.KeyEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ronnie.presenatation.MainImageActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageListFragmentTest {

    @Rule
    @JvmField
    var activityScenarioRule = ActivityScenarioRule(MainImageActivity::class.java)

    @Test
    fun return_true_recyclerview_is_showing() {
        onView(withId(R.id.list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun return_true_searchView_is_showing() {
        onView(withId(R.id.searchView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    /**
     * Writes bogus search to query, waits for a moment then check if empty section is shown.
     */
    @Test
    fun return_true_empty_section_is_showing() {
        runBlocking {
            onView(withId(R.id.searchView)).perform(replaceText("Anotherewsxrtdcyfguvhibjnkhugytfdr"))
                .perform(pressImeActionButton())
            delay(5000)
            onView(withId(R.id.empty_section)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }

    }

    /**
     * Test the visibility of the cancel search entry imageview if it becomes invisible when the searchview is empty
     */
    @Test
    fun show_cancel_image_visibility() {
        runBlocking {
            onView(withId(R.id.searchView))
                .perform(click())
                .perform(pressKey(KeyEvent.KEYCODE_DEL))
                .perform(pressKey(KeyEvent.KEYCODE_DEL))
                .perform(pressKey(KeyEvent.KEYCODE_DEL))
                .perform(pressKey(KeyEvent.KEYCODE_DEL))
                .perform(pressKey(KeyEvent.KEYCODE_DEL))
                .perform(pressKey(KeyEvent.KEYCODE_DEL))
            onView(withId(R.id.cancel_search)).check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        }
    }
}