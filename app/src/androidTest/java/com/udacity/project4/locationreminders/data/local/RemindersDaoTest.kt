package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    //    TODO: Add testing implementation to the RemindersDao.kt
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun init() {
        Dispatchers.setMain(testCoroutineDispatcher)
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun end() {
        database.close()
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun insertReminder_getById_matchingReminders() = testCoroutineDispatcher.runBlockingTest{
        //GIVEN - A reminder
        val reminder = ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)

        //WHEN - The reminder is inserted and gotten back
        database.reminderDao().saveReminder(reminder)
        val returnedReminder = database.reminderDao().getReminderById(reminder.id)

        //THEN - The loaded reminder is the same as the one created
        assertThat(returnedReminder, `is`(reminder))
    }

    @Test
    fun getReminders_noReminders_returnsEmptyList() = testCoroutineDispatcher.runBlockingTest{
        //WHEN - getReminders() is called without saving any reminders
        val returnedReminders = database.reminderDao().getReminders()

        //THEN - the returned list will be empty
        assertThat(returnedReminders.isEmpty(), `is`(true))
    }

    @Test
    fun addReminders_deleteAllRemindersThenGetReminders_returnsEmptyList() = testCoroutineDispatcher.runBlockingTest {
        //GIVEN - A list of reminders
        val reminders = listOf(
            ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0),
            ReminderDTO("Title2", "Description2", "Location2", 0.0, 0.0),
            ReminderDTO("Title3", "Description3", "Location3", 0.0, 0.0)
        )

        //WHEN - This list is added to the database and then deleted
        reminders.forEach { database.reminderDao().saveReminder(it) }
        var returnedReminders = database.reminderDao().getReminders()

        assertThat(returnedReminders, `is`(reminders))

        database.reminderDao().deleteAllReminders()
        returnedReminders = database.reminderDao().getReminders()

        //THEN - The returned list will be empty
        assertThat(returnedReminders.isEmpty(), `is`(true))
    }

}