package com.obdasystems.pocmedici.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.obdasystems.pocmedici.BuildConfig;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.WriteMessageAttachmentAdapter;
import com.obdasystems.pocmedici.listener.OnRecyclerViewPositionClickListener;
import com.obdasystems.pocmedici.message.model.Message;
import com.obdasystems.pocmedici.message.model.OutMessage;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteMessageActivity extends AppCompatActivity {

    private static final int FILE_READ_REQUEST_CODE = 100;
    private static final int PICTURE_READ_REQUEST_CODE = 101;
    private static final int PICTURE_CAMERA_REQUEST_CODE = 102;

    private static final int ENABLE_CAMERA_REQUEST_CODE = 200;

    private List<Uri> attachmentUris = new LinkedList<>();
    private List<String> attachmentNames = new LinkedList<>();
    private Uri currentCameraAttachmentUri;

    private boolean cameraEnabled ;

    private RecyclerView attachmentRecyclerView;
    private WriteMessageAttachmentAdapter attachmentAdapter;

    private Context ctx;

    private int recursiveSendMessageCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        recursiveSendMessageCounter = 0;
        setContentView(R.layout.activity_write_message);

        attachmentRecyclerView = findViewById(R.id.write_message_attachment_recycler_view);
        attachmentAdapter = new WriteMessageAttachmentAdapter(this, new OnRecyclerViewPositionClickListener() {
            @Override
            public void onPositionClicked(int position) {
                attachmentUris.remove(position);
                attachmentNames.remove(position);
                attachmentAdapter.setAttachments(attachmentNames);
                attachmentAdapter.notifyDataSetChanged();
            }
        });
        attachmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attachmentRecyclerView.setAdapter(attachmentAdapter);
        attachmentAdapter.setAttachments(attachmentNames);
        attachmentAdapter.notifyDataSetChanged();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, ENABLE_CAMERA_REQUEST_CODE);
        }
        else {
            cameraEnabled = true;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.write_message_toolbar);
        setSupportActionBar(toolbar);

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        attachmentRecyclerView = findViewById(R.id.write_message_attachment_recycler_view);
        attachmentAdapter = new WriteMessageAttachmentAdapter(this, new OnRecyclerViewPositionClickListener() {
            @Override
            public void onPositionClicked(int position) {
                attachmentUris.remove(position);
                attachmentAdapter.setAttachments(attachmentUris);
                attachmentAdapter.notifyDataSetChanged();
            }
        });
        attachmentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attachmentRecyclerView.setAdapter(attachmentAdapter);
        attachmentAdapter.setAttachments(attachmentUris);
        attachmentAdapter.notifyDataSetChanged();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, ENABLE_CAMERA_REQUEST_CODE);
        }
        else {
            cameraEnabled = true;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.write_message_toolbar);
        setSupportActionBar(toolbar);

    }*/

    private void backToMessageList() {
        Intent messageListIntent = new Intent(this, MessageListActivity.class);
        startActivity(messageListIntent);
    }

    @Override
    public void onBackPressed() {
        backToMessageList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_READ_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            Log.i("appMedici", "data.getData().getPath()= "+data.getData().getPath());
            Log.i("appMedici", "data.getData().getLastPathSegment()= "+data.getData().getLastPathSegment());

            String uriString = uri.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }
            Log.i("appMedici", "displayName= "+displayName);

            attachmentUris.add(uri);
            attachmentNames.add(displayName);
            attachmentAdapter.setAttachments(attachmentNames);
            attachmentAdapter.notifyDataSetChanged();
        }
        else {
            if(requestCode == PICTURE_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                attachmentUris.add(currentCameraAttachmentUri);
                String uriString = currentCameraAttachmentUri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String displayName = null;

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(currentCameraAttachmentUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }
                Log.i("appMedici", "displayName= "+displayName);

                attachmentUris.add(currentCameraAttachmentUri);
                attachmentNames.add(displayName);
                attachmentAdapter.setAttachments(attachmentNames);
                attachmentAdapter.notifyDataSetChanged();
            }
            else {
                if (requestCode == FILE_READ_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }
                    Log.i("appMedici", "displayName= "+displayName);

                    attachmentUris.add(uri);
                    attachmentNames.add(displayName);
                    attachmentAdapter.setAttachments(attachmentNames);
                    attachmentAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ENABLE_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                cameraEnabled = true;
            }
            else {
                cameraEnabled = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_write_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.write_message_action_attach) {
            showAttachmentDialog();
            return true;
        }
        if (id == R.id.write_message_action_send) {
            Toast.makeText(this, "SENDING....", Toast.LENGTH_SHORT).show();

            OutMessage msg = new OutMessage();
            EditText body = findViewById(R.id.write_message_edit_body);
            msg.setText(body.getText().toString());
            EditText subject = findViewById(R.id.write_message_edit_subject);
            msg.setSubject(subject.getText().toString());
            msg.setAdverseEvent(false);
            msg.setSender("james");
            msg.setRecipient("admin");
            msg.setDate(System.currentTimeMillis());

            sendMessage(System.currentTimeMillis(), body.getText().toString(), subject.getText().toString(), true, "james", "admin", recursiveSendMessageCounter);

            Toast.makeText(this, "MESSAGE SENT", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void sendMessage(Long date, String text, String subject, boolean adverseEvent, String sender, String recipient, int counter) {

        if(counter<15) {
            recursiveSendMessageCounter++;
            String usr = "james";
            String pwd = "bush";


            String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
            if (authorizationToken == null) {
                NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
            }
            authorizationToken = SaveSharedPreference.getAuthorizationToken(this);

            MediciApi apiService = MediciApiClient.createService(MediciApi.class, authorizationToken);

            apiService.sendMessage(date, text, subject, adverseEvent, sender, recipient).enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    if (response.isSuccessful()) {
                        Log.i("appMedici", "Message sent to server." + response.body().toString());
                        recursiveSendMessageCounter=0;
                        backToMessageList();
                    } else {
                        switch (response.code()) {
                            case 401:
                                NetworkUtils.requestNewAuthorizationToken(pwd, usr, ctx);
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch message list (401)");
                                if (!SaveSharedPreference.getAuthorizationIssue(ctx)) {
                                    sendMessage(date, text, subject, adverseEvent, sender, recipient, recursiveSendMessageCounter);
                                } else {
                                    String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(ctx);
                                    Toast.makeText(getApplicationContext(), "Unable to send message (401) [" + issueDescription + "]", Toast.LENGTH_LONG).show();
                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send message (401) [" + issueDescription + "]");
                                }
                                break;
                            case 404:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send message (404)");
                                Toast.makeText(getApplicationContext(), "Unable to send message (404)", Toast.LENGTH_LONG).show();
                                break;
                            case 500:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send message (500)");
                                Toast.makeText(getApplicationContext(), "Unable to send message (500)", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send message (UNKNOWN)");
                                Toast.makeText(getApplicationContext(), "Unable to send message (UNKNOWN)", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send message : " + t.getMessage());
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send message : " + t.getStackTrace());
                    Toast.makeText(getApplicationContext(), "Unable to send message ..", Toast.LENGTH_LONG).show();
                    backToMessageList();
                }
            });

        }
        else {
            Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Max number of calls to sendMessage() reached!!");
            Toast.makeText(getApplicationContext(), "Max number of calls to sendMessage() reached!!", Toast.LENGTH_LONG).show();
            recursiveSendMessageCounter=0;
        }
    }

    private void showAttachmentDialog() {
        final String[] items = {
                getString(R.string.action_attach_file),
                getString(R.string.action_attach_picture),
                getString(R.string.action_take_picture)
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.action_attachment_options))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        switch (position) {
                            case 0 :
                                openFileBrowser();
                                break;
                            case 1:
                                openGallery();
                                break;
                            case 2 :
                                openCamera();
                                break;
                        }
                    }
                })
                .show();
    }

    private void openFileBrowser() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("*/*");

        startActivityForResult(intent, FILE_READ_REQUEST_CODE);
    }

    private void openGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");

        startActivityForResult(intent, PICTURE_READ_REQUEST_CODE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //currentCameraAttachmentUri = Uri.fromFile(getOutputMediaFile());
        currentCameraAttachmentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID,getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentCameraAttachmentUri);

        startActivityForResult(intent, PICTURE_CAMERA_REQUEST_CODE);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "AppMedici");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }
}