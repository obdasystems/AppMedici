package com.obdasystems.pocmedici.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jaredrummler.android.device.DeviceName;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.request.UserDeviceRegistrationRequest;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

public class PushNotificationService extends FirebaseMessagingService {
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

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("appMedici", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    String deviceDescription = DeviceName.getDeviceName();
                    String authToken = SaveSharedPreference.getAuthorizationToken(this);
                    MediciApi apiInterface = MediciApiClient.createService(MediciApi.class, authToken);
                    UserDeviceRegistrationRequest registrationRequest = new UserDeviceRegistrationRequest();
                    registrationRequest.setDeviceDescription(deviceDescription);
                    registrationRequest.setRegistrationToken(token);
                    apiInterface.registerInstanceId(registrationRequest);
                });
    }

}
