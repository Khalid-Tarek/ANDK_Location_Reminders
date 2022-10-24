package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.ArgumentMatchers.isNull

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    //DONE: provide testing to the SaveReminderView and its live data objects

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {

        Dispatchers.setMain(testCoroutineDispatcher)
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource())
    }

    @After
    fun tearDown() {
        stopKoin()

        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun validateAndSaveReminder_navigationCommandBack() {
        //GIVEN a valid reminder
        val reminder = ReminderDataItem("Title", "Description", "Location", 0.0, 0.0)

        //WHEN the reminder is saved
        saveReminderViewModel.validateAndSaveReminder(reminder)

        //THEN the navigation command live data gets the value Back
        val navigationCommand = saveReminderViewModel.navigationCommand.getOrAwaitValue()
        assertThat(NavigationCommand.Back, `is`(navigationCommand)) //Feedback needed here, not sure why i needed to switch these for it to work
    }

    @Test
    fun onClear_allLiveDataNull() {
        //WHEN onClear() is called
        saveReminderViewModel.onClear()

        //THEN all live data values will be null
        assertThat(saveReminderViewModel.reminderTitle.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.reminderDescription.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.selectedPOI.getOrAwaitValue(),`is`(nullValue()))
        assertThat(saveReminderViewModel.longitude.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.latitude.getOrAwaitValue(), `is`(nullValue()))
    }

    @Test
    fun saveReminder_checkLoading() {
        //GIVEN - A reminder
        val reminderDataItem = ReminderDataItem("Title", "Description", "Location", 0.0, 0.0)

        //WHEN - A reminder is being saved
        testCoroutineDispatcher.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminderDataItem)

        //THEN - The loading indicator will appear
        assertThat(saveReminderViewModel.showLoading.value, `is`(true))
        testCoroutineDispatcher.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.value, `is`(false))
    }

}