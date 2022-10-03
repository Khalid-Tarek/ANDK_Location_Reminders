package com.udacity.project4.locationreminders.savereminder

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    //TODO: provide testing to the SaveReminderView and its live data objects

    @Test
    fun onClear_allLiveDataValuesCleared(){
        //Given a SaveReminderViewModel
        val saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource())

        //When onClear() is called
        saveReminderViewModel.onClear()

        //Then all live data values must be null
        assertThat(saveReminderViewModel.reminderTitle.value, nullValue())
    }



    @Test
    fun something(){
        //GIVEN

        //WHEN

        //THEN

    }

}