package com.akay.fitnass.ui.notification;

import android.app.Notification;

public interface NotificationController {

    Notification getPersistentNotification();

    void showStartPauseNotification(boolean showPauseText);

    void notify(int id, Notification notification);
}
