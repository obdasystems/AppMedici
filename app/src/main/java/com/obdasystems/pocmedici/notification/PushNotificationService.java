package com.obdasystems.pocmedici.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jaredrummler.android.device.DeviceName;
import com.obdasystems.pocmedici.activity.CalendarActivity;
import com.obdasystems.pocmedici.activity.FormListActivity;
import com.obdasystems.pocmedici.activity.MainActivity;
import com.obdasystems.pocmedici.activity.MessageListActivity;
import com.obdasystems.pocmedici.network.ApiClient;
import com.obdasystems.pocmedici.network.ItcoService;
import com.obdasystems.pocmedici.network.interceptors.AuthenticationInterceptor;
import com.obdasystems.pocmedici.network.request.UserDeviceRegistrationRequest;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotificationService extends FirebaseMessagingService {
    private static final String TAG = PushNotificationService.class.getSimpleName();
    private static final String CHANNEL_ID = "appMedici push notification";

    private static final int NOTIFICATION_ID_MESSAGE = 98765;
    private static final int NOTIFICATION_ID_FORM = 98766;
    private static final int NOTIFICATION_ID_EVENT = 98767;

    private static final String NOTIFICATION_TYPE_MESSAGE = "new_message";
    private static final String NOTIFICATION_TYPE_FORM = "new_form";
    private static final String NOTIFICATION_TYPE_EVENT = "new_event";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> dataMap = remoteMessage.getData();
        String type = dataMap.get("type");

        Class activityClass;
        int notId;

        switch (type) {
            case NOTIFICATION_TYPE_MESSAGE:
                activityClass = MessageListActivity.class;
                notId = NOTIFICATION_ID_MESSAGE;
                break;
            case NOTIFICATION_TYPE_FORM:
                activityClass = FormListActivity.class;
                notId = NOTIFICATION_ID_FORM;
                break;
            case NOTIFICATION_TYPE_EVENT:
                activityClass = CalendarActivity.class;
                notId = NOTIFICATION_ID_EVENT;
                break;
            default:
                activityClass = MainActivity.class;
                notId = NOTIFICATION_ID_MESSAGE;
        }

        Intent intent = new Intent(this, activityClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icons8_caduceus_48);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                //.setSmallIcon(R.drawable.icons8_caduceus_48)
                //.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notId, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    String deviceDescription = DeviceName.getDeviceName();
                    UserDeviceRegistrationRequest request =
                            new UserDeviceRegistrationRequest();
                    request.setDeviceDescription(deviceDescription);
                    request.setRegistrationToken(token);

                    ItcoService service = ApiClient
                            .forService(ItcoService.class)
                            .baseURL(ApiClient.BASE_URL)
                            .logging(HttpLoggingInterceptor.Level.BODY)
                            .addInterceptor(new AuthenticationInterceptor(this))
                            .build();

                    service.registerInstanceId(request)
                            .enqueue(new Callback<JSONObject>() {
                                @Override
                                public void onResponse(Call<JSONObject> call,
                                                       Response<JSONObject> response) {
                                    if (response.isSuccessful()) {
                                        Log.e(TAG, "Firebase token sent!!");
                                    } else {
                                        Log.e(TAG, "Unable to send firebase token to server");
                                    }
                                }

                                @Override
                                public void onFailure(Call<JSONObject> call, Throwable t) {

                                }
                            });
                });
    }

}
