package com.obdasystems.pocmedici.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.activity.MainActivity;

import static android.support.constraint.Constraints.TAG;
import static android.widget.Toast.*;

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {

    private final String CHANNEL_ID = "appMedici push notification";
    private final int NOTIFICATION_ID = 98765;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icons8_caduceus_48);

        super.onMessageReceived(remoteMessage);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.icons8_caduceus_48)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("appMedici", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.i("TOKEN", token);
                        //sendRegistrationToServer(token);

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        /*Log.d(TAG, msg);
                        makeText(this, msg, LENGTH_SHORT).show();*/
                    }
                });

    }
}
