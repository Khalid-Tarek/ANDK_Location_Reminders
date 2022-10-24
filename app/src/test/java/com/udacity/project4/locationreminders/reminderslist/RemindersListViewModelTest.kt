package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //DONE: provide testing to the RemindersListViewModel and its live data objects
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        stopKoin()

        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun loadReminders_validDataSource_remindersListContainsReminders() {
        //GIVEN a valid data source and the reminderViewModel
        val reminderDTO1 = ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)
        val reminderDTO2 = ReminderDTO("Title2", "Description2", "Location2", 0.0, 0.0)
        val reminderDTOs =
            linkedMapOf(reminderDTO1.id to reminderDTO1, reminderDTO2.id to reminderDTO2)
        val remindersListViewModel =
            RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                FakeDataSource(reminderDTOs)
            )

        //WHEN loadReminders() is called
        remindersListViewModel.loadReminders()

        //THEN both lists will be equal
        val reminder1 =
            ReminderDataItem("Title1", "Description1", "Location1", 0.0, 0.0, reminderDTO1.id)
        val reminder2 =
            ReminderDataItem("Title2", "Description2", "Location2", 0.0, 0.0, reminderDTO2.id)
        assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue(),
            `is`(listOf(reminder1, reminder2))
        )
    }

    @Test
    fun loadReminders_invalidDataSource_showSnackBarNotNull() {
        //GIVEN an invalid data source and the reminderViewModel
        val remindersListViewModel =
            RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                FakeDataSource().apply { setReturnError(true) })

        //WHEN loadReminders() is called
        remindersListViewModel.loadReminders()

        //THEN showSnackBar LiveData value will not be null or empty
        assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            anyOf(notNullValue(), `is`(""))
        )
    }

    @Test
    fun loadReminders_checkLoading() {
        //GIVEN - A Reminder List Screen
        val remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource())

        //WHEN - loadReminders is called
        testCoroutineDispatcher.pauseDispatcher()
        remindersListViewModel.loadReminders()

        //THEN - Loading indicator appears then disappears
        assertThat(remindersListViewModel.showLoading.value, `is`(true))
        testCoroutineDispatcher.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.value, `is`(false))
    }
}