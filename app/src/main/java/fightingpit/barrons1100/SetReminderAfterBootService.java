package fightingpit.barrons1100;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by AG on 23/01/16.
 * This class receives the Broadcast at restart of device. This will then reset the reminder.
 */
public class SetReminderAfterBootService extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            SharedPreferences aSharedPref = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
            String aReminderTime = aSharedPref.getString("reminder_time", "");
            if(!aReminderTime.equalsIgnoreCase("")) {
                if (aReminderTime.substring(6, 8).equalsIgnoreCase("AM")) {
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aReminderTime.substring(0, 2)));
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, 12 + Integer.parseInt(aReminderTime.substring(0, 2)));
                }
                calendar.set(Calendar.MINUTE, Integer.parseInt(aReminderTime.substring(3, 5)));
                // Set Actual repeating notification reminder.
                Intent myIntent = new Intent(context, NotificationService.class);
                PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);

                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.cancel(pendingIntent);
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);

            }
        }
    }

}
