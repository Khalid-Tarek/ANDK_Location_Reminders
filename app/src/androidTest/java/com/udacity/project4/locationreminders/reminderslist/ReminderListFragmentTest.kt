package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    DONE: test the navigation of the fragments.
//    DONE: test the displayed data on the UI.
//    DONE: add testing for the error messages.

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var database: RemindersDatabase

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        Dispatchers.setMain(testCoroutineDispatcher)
        testCoroutineDispatcher.runBlockingTest {
            database = Room.databaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java, "locationReminders.db"
            ).build()
        }
    }

    @After
    fun end() {
        testCoroutineDispatcher.runBlockingTest {
            database.reminderDao().deleteAllReminders()
        }

        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun addReminderFABonClick_navigateToSaveReminderFragment() {
        //GIVEN - On Reminder List Screen and a valid navigation controller
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(themeResId = R.style.AppTheme)

        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //WHEN - add Reminder FAB clicked
        onView(withId(R.id.addReminderFAB)).perform(click())

        //THEN - Will Navigate to SaveReminderFragment
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    @Test
    fun addNewReminders_remindersDisplayedOnFragment() = runBlockingTest {
        //GIVEN - A Reminder List Screen and 2 new reminderDTOs
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(themeResId = R.style.AppTheme)

        val reminderDTO1 = ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)
        val reminderDTO2 = ReminderDTO("Title2", "Description2", "Location2", 0.0, 0.0)

        //WHEN - New reminders are added in the database
        database.reminderDao().saveReminder(reminderDTO1)
        database.reminderDao().saveReminder(reminderDTO2)
        scenario.onFragment {
            it._viewModel.loadReminders()
            it._viewModel.remindersList.getOrAwaitValue()
        }

        //THEN - The same reminders are displayed on the Screen
        onView(withText("Title1")).check(matches(isDisplayed()))
        onView(withText("Description1")).check(matches(isDisplayed()))
        onView(withText("Title2")).check(matches(isDisplayed()))
        onView(withText("Description2")).check(matches(isDisplayed()))
    }

    @Test
    fun changeShowSnackBarLiveData_SnackBarAppears() {
        //GIVEN - A Reminder List Screen
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(themeResId = R.style.AppTheme)

        //WHEN - showSnackBar is changed
        scenario.onFragment {
            it._viewModel.showSnackBar.value = "Test Error"
        }

        //THEN - A Snack Bar is shown
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(isDisplayed()))
    }
}