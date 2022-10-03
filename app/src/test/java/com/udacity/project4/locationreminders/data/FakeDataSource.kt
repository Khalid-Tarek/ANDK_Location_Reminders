package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

//    DONE: Create a fake data source to act as a double to the real data source

    companion object {
        const val INVALID_DTO_ID = 101
    }

    private val reminders = LinkedHashMap<String, ReminderDTO>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(reminders.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if(reminders.containsKey(id)){
            Result.Success(reminders[id]!!)
        } else {
            Result.Error("ReminderDTO with this id doesn't Exist", INVALID_DTO_ID)
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}