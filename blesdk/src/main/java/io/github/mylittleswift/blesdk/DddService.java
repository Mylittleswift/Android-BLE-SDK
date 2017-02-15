package io.github.mylittleswift.blesdk;

import android.app.Activity;
import android.support.annotation.NonNull;

import io.github.mylittleswift.blesdk.dfulib.dfu.DfuBaseService;


public class DddService extends DfuBaseService {

    Class<? extends Activity> mClass;

    DddService(@NonNull Class<? extends Activity> cls) {

        this.mClass = cls;

    }

    protected Class<? extends Activity> getNotificationTarget() {
        /*
         * As a target activity the NotificationActivity is returned, not the MainActivity. This is because the notification must create a new task:
		 * 
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * 
		 * when user press it. Using NotificationActivity we can check whether the new activity is a root activity (that means no other activity was open before)
		 * or that there is other activity already open. In the later case the notificationActivity will just be closed. System will restore the previous activity from 
		 * this application - the MainActivity. However if nRF Beacon has been closed during upload and user click the notification a NotificationActivity will
		 * be launched as a root activity. It will create and start the MainActivity and finish itself.
		 * 
		 * This method may be used to restore the target activity in case the application was closed or is open. It may also be used to recreate an activity history (see NotificationActivity).
		 */
        return mClass;
    }
}
