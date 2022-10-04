package com.udacity.project4.locationreminders.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    DONE: Add testing implementation to the RemindersLocalRepository.kt
    private val reminder1 = ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "Location2", 0.0, 0.0)
    private val reminder3 = ReminderDTO("Title3", "Description3", "Location3", 0.0, 0.0)
    private val reminders =
        linkedMapOf(reminder1.id to reminder1, reminder2.id to reminder2, reminder3.id to reminder3)
    private lateinit var fakeReminderDTO: FakeReminderDao

    private lateinit var repository: RemindersLocalRepository

    @Before
    fun init() {
        fakeReminderDTO = FakeReminderDao(reminders)

        repository = RemindersLocalRepository(fakeReminderDTO, Dispatchers.Unconfined)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getReminders_returnsAllReminders() = runBlockingTest{
        //WHEN we all getReminders()
        val returnReminders = repository.getReminders() as Result.Success<List<ReminderDTO>>

        //THEN the returned reminders are the same as the reminders we have
        assertThat(returnReminders.data, IsEqual(reminders.values.toList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getInvalidReminder_returnsError() = runBlockingTest {
        //WHEN we request an id that doesn't exist
        val returnReminder = repository.getReminder("") as Result.Error

        //THEN  the returnReminder should be an error
        assertThat(returnReminder, `is`(Result.Error("Reminder not found!")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveReminder_getSameReminder_bothRemindersEqual() = runBlockingTest{
        //GIVEN a reminderDTO
        val originalReminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0)

        //WHEN the reminder is saved and requested
        repository.saveReminder(originalReminder)
        val returnedReminder = repository.getReminder(originalReminder.id) as Result.Success<ReminderDTO>

        //THEN the reminders are the same
        assertThat(returnedReminder.data, `is`(originalReminder))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAllReminders_reminderListEmpty() = runBlockingTest {
        //WHEN deleteAllReminders() is called
        repository.deleteAllReminders()
        val remindersList = repository.getReminders() as Result.Success<List<ReminderDTO>>

        //THEN the remindersList is empty
        assertThat(remindersList.data.isEmpty(), `is`(true))
    }

}