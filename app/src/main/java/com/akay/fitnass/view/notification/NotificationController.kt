package com.akay.fitnass.view.notification

import android.app.Notification

interface NotificationController {

    fun getPersistentNotification(): Notification

    fun showStartPauseNotification(showPauseText: Boolean)

    fun notify(id: Int, notification: Notification)
}
