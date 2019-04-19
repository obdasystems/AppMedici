package com.obdasystems.pocmedici.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jaredrummler.android.device.DeviceName;
import com.obdasystems.pocmedici.activity.CalendarActivity;
import com.obdasystems.pocmedici.activity.FormListActivity;
import com.obdasystems.pocmedici.activity.MainActivity;
import com.obdasystems.pocmedici.activity.MessageListActivity;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.request.UserDeviceRegistrationRequest;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotificationService extends FirebaseMessagingService {
    private final String CHANNEL_ID = "appMedici push notification";

    private final int NOTIFICATION_ID_MESSAGE = 98765;
    private final int NOTIFICATION_ID_FORM = 98766;
    private final int NOTIFICATION_ID_EVENT = 98767;

    private final String NOTIFICATION_TYPE_MESSAGE = "new_message";
    private final String NOTIFICATION_TYPE_FORM = "new_form";
    private final String NOTIFICATION_TYPE_EVENT = "new_event";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> dataMap =  remoteMessage.getData();
        String type = dataMap.get("type");

        Class activityClass;
        int notId;

        switch (type){
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
                        apiInterface.registerInstanceId(registrationRequest)
                                .enqueue(new Callback<JSONObject>() {
                                    @Override
                                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                        if (response.isSuccessful()) {
                                            Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Firebase token sent!!");

                                        } else {
                                            switch (response.code()) {
                                                case 401:
                                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send firebase token to server (401)");
                                                    break;
                                                case 404:
                                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send firebase token to server (404)");
                                                    Toast.makeText(getApplicationContext(), "Unable to send firebase token to server (404)", Toast.LENGTH_LONG).show();

                                                    break;
                                                case 500:
                                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send firebase token to server (500)");
                                                    Toast.makeText(getApplicationContext(), "Unable to send firebase token to server (500)", Toast.LENGTH_LONG).show();

                                                    break;
                                                default:
                                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send firebase token to server (UNKNOWN)");
                                                    Toast.makeText(getApplicationContext(), "Unable to send firebase token to server (UNKNOWN)", Toast.LENGTH_LONG).show();

                                                    break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JSONObject> call, Throwable t) {

                                    }
                                });
                });
    }

}
