package com.udacity.project4.locationreminders.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.LocationServices
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment.Companion.ACTION_GEOFENCE_EVENT
import com.udacity.project4.utils.sendNotification
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private val TAG = "GeofenceTJIS"

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    private lateinit var geofenceClient: GeofencingClient

    companion object {
        private const val JOB_ID = 573

        //        DONE: call this to start the JobIntentService to handle the geofencing transition events
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        //DONE: handle the geofencing transition events and
        // send a notification to the user when he enters the geofence area
        // call @sendNotification
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            geofenceClient = LocationServices.getGeofencingClient(applicationContext)

            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            if (geofencingEvent!!.hasError()) {
                Log.e(TAG, geofencingEvent.errorCode.toString())
                return
            }
            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                if (geofencingEvent.triggeringGeofences!!.isNotEmpty())
                    sendNotificationAndRemoveGeoFences(geofencingEvent.triggeringGeofences!!)
                else
                    Log.e(TAG, "No Geofence Trigger Found")
            }
        }
    }

    //DONE: get the request id of the current geofence
    private fun sendNotificationAndRemoveGeoFences(triggeringGeoFences: List<Geofence>) {
        val remindersLocalRepository: ReminderDataSource by inject()

        //Get the local repository instance
        //Interaction to the repository has to be through a coroutine scope
        CoroutineScope(coroutineContext).launch(SupervisorJob()) {
            triggeringGeoFences.forEach {
                //get the reminder with the request id
                val result = remindersLocalRepository.getReminder(it.requestId)
                if (result is Result.Success<ReminderDTO>) {
                    val reminderDTO = result.data
                    //send a notification to the user with the reminder details
                    sendNotification(
                        this@GeofenceTransitionsJobIntentService, ReminderDataItem(
                            reminderDTO.title,
                            reminderDTO.description,
                            reminderDTO.location,
                            reminderDTO.latitude,
                            reminderDTO.longitude,
                            reminderDTO.id
                        )
                    )
                }
                geofenceClient.removeGeofences(triggeringGeoFences.map { geofence -> geofence.requestId })
            }
        }
    }

}
