package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.message.model.Message;

import java.util.Date;

public class ReadMessageActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);

        Toolbar toolbar = find(R.id.read_message_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> goToMessageList());

        Intent intent = getIntent();
        Message msg = intent.getParcelableExtra("message");

        TextView subjectView = find(R.id.message_read_subject_field);
        subjectView.setText(msg.getSubject());

        TextView bodyView = find(R.id.message_read_body_edit);
        bodyView.setText(msg.getText());

        TextView senderView = find(R.id.message_read_sender_field);
        senderView.setText(msg.getSender().getUsername());

        TextView receiverView = find(R.id.message_read_recipient_field);
        receiverView.setText(t(R.string.message_receiver_you));

        //senderView.setText(msg.getTo());
        TextView timeView = find(R.id.message_read_timestamp_field);
        timeView.setText(DateFormat.getDateFormat(this)
                .format(new Date(msg.getDate())));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Message msg = intent.getParcelableExtra("message");

        TextView subjectView = find(R.id.message_read_subject_field);
        subjectView.setText(msg.getSubject());

        TextView bodyView = find(R.id.message_read_body_edit);
        bodyView.setText(msg.getText());

        TextView senderView = find(R.id.message_read_sender_field);
        senderView.setText(msg.getSender().getUsername());

        TextView receiverView = find(R.id.message_read_recipient_field);
        receiverView.setText(t(R.string.message_receiver_you));

        //senderView.setText(msg.getTo());
        TextView timeView = find(R.id.message_read_timestamp_field);
        timeView.setText(DateFormat.getDateFormat(this)
                .format(new Date(msg.getDate())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_read_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.read_message_action_delete) {
            snack(find(R.id.message_read_body_edit),
                    R.string.message_read_delete_confirm, Snackbar.LENGTH_LONG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToMessageList() {
        Intent messageListIntent = new Intent(this, MessageListActivity.class);
        startActivity(messageListIntent);
    }

    @Override
    public void onBackPressed() {
        goToMessageList();
    }

}
