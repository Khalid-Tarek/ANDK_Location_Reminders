package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.dto.ReminderDTO

//Test Double for RemindersDao. Used in RemindersLocalRepositoryTest
class FakeReminderDao(private var reminders: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()):
    RemindersDao {

    override suspend fun getReminders(): List<ReminderDTO> {
        return reminders.values.toList()
    }

    override suspend fun getReminderById(reminderId: String): ReminderDTO? {
        return reminders[reminderId]
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders[reminder.id] = reminder
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}