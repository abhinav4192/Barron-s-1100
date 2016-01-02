package fightingpit.barrons1100;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;


/**
 * Service to remind user to learn words.
 */

public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Intent in = new Intent(this, MainActivity.class);

            int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
            int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
            PendingIntent pIntent = PendingIntent.getActivity(this, requestID, in, flags);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.barrons_rounded);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.small_b)
                            .setLargeIcon(largeIcon)
                            .setContentTitle("Barron's 1100")
                            .setContentText("Time to learn new words !!")
                            .setContentIntent(pIntent);
            mBuilder.setAutoCancel(true);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(001, mBuilder.build());
        }
    }

}
