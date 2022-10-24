package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
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

    private lateinit var repository: RemindersLocalRepository

    private lateinit var database: RemindersDatabase

    @Before
    fun init() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Unconfined)
    }

    @After
    fun end() {
        database.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getReminders_returnsAllReminders() = runBlocking {
        //GIVEN - all reminders are saved into the database
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        repository.saveReminder(reminder3)

        //WHEN we all getReminders()
        val returnReminders = repository.getReminders() as Result.Success<List<ReminderDTO>>

        //THEN the returned reminders are the same as the reminders we have
        assertThat(returnReminders.data, IsEqual(reminders.values.toList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getInvalidReminder_returnsError() = runBlocking {
        //WHEN we request an id that doesn't exist
        val returnReminder = repository.getReminder("") as Result.Error

        //THEN  the returnReminder should be an error
        assertThat(returnReminder, `is`(Result.Error("Reminder not found!")))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveReminder_getSameReminder_bothRemindersEqual() = runBlocking {
        //GIVEN a reminderDTO
        val originalReminder = ReminderDTO("Title", "Description", "Location", 0.0, 0.0)

        //WHEN the reminder is saved and requested
        repository.saveReminder(originalReminder)
        val returnedReminder =
            repository.getReminder(originalReminder.id) as Result.Success<ReminderDTO>

        //THEN the reminders are the same
        assertThat(returnedReminder.data, `is`(originalReminder))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAllReminders_reminderListEmpty() = runBlocking {
        //WHEN deleteAllReminders() is called
        repository.deleteAllReminders()
        val remindersList = repository.getReminders() as Result.Success<List<ReminderDTO>>

        //THEN the remindersList is empty
        assertThat(remindersList.data.isEmpty(), `is`(true))
    }

}