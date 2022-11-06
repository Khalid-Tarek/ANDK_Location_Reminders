package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val reminders: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()) :
    ReminderDataSource {

//    DONE: Create a fake data source to act as a double to the real data source

    private var shouldReturnError = false
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(shouldReturnError)
            try {
                throw Exception("Exception while retrieving data")
            } catch (e: Exception) {
                return Result.Error(e.localizedMessage)
            }

        return Result.Success(reminders.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(shouldReturnError)
            try {
                throw Exception("Exception while retrieving data")
            }
            catch (e: Exception) {
                return Result.Error(e.localizedMessage)
            }

        return if(reminders.containsKey(id)){
            Result.Success(reminders[id]!!)
        } else {
            Result.Error("Reminder Not found!")
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}