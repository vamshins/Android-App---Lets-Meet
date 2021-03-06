

package org.vamshi.geofencesdemo.managers;

import org.vamshi.geofencesdemo.R;
import org.vamshi.geofencesdemo.geofence.data.SimpleGeofence;
import org.vamshi.geofencesdemo.ui.activities.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class NotificationsManager {

    /** Singleton */
    private static NotificationsManager sInstance;

    /** Data Members */
    private Notification mNotification;
    private final NotificationManager mNotificationManager;
    private final Context mContext;

    public static synchronized NotificationsManager getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new NotificationsManager(context);
        }

        return sInstance;
    }

    public NotificationsManager(final Context ctx) {
        mContext = ctx;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Service.NOTIFICATION_SERVICE);
    }

    /**
     * This notification it has to be launched when we enter or exit from a
     * location we've sepcified before in the {@link MainActivity}
     * 
     * @param geofence
     */
    public void showLocationReminderNotification(final SimpleGeofence geofence) {

        mNotification = new NotificationCompat.Builder(mContext)
                .setTicker(mContext.getString(R.string.location_reminder_label))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(mContext.getString(R.string.location_reminder_label))
                .setContentText(geofence.getAddress()).setOnlyAlertOnce(true).setContentIntent(null).build();

        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotification.defaults |= Notification.DEFAULT_LIGHTS;
        mNotification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotification.defaults |= Notification.DEFAULT_SOUND;

        // Launch notification
        mNotificationManager.notify(Integer.valueOf(geofence.getPlaceId()), mNotification);
    }
}
