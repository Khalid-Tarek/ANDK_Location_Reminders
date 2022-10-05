package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.isNull

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    //TODO: provide testing to the SaveReminderView and its live data objects

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource())
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

}