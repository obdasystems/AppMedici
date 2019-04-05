package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.message.model.Message;

public class ReadMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.read_message_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMessageList();
            }
        });

        Intent intent = getIntent();
        Message msg = intent.getParcelableExtra("message");

        TextView subjectView = (TextView) findViewById(R.id.read_message_subject);
        subjectView.setText(msg.getSubject());
        TextView bodyView = (TextView) findViewById(R.id.read_message_body);
        bodyView.setText(msg.getText());
        TextView senderView = (TextView) findViewById(R.id.read_message_sender);
        Log.i("appMedici", "msg from: "+msg.getSender());
        senderView.setText(msg.getSender().getUsername());
        TextView receiverView = (TextView) findViewById(R.id.read_message_receiver);
        receiverView.setText("me");
        //senderView.setText(msg.getTo());
        TextView timeView = (TextView) findViewById(R.id.read_message_timestamp);
        timeView.setText(""+msg.getDate());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Message msg = intent.getParcelableExtra("message");

        TextView subjectView = (TextView) findViewById(R.id.read_message_subject);
        subjectView.setText(msg.getSubject());
        TextView bodyView = (TextView) findViewById(R.id.read_message_body);
        bodyView.setText(msg.getText());
        TextView senderView = (TextView) findViewById(R.id.read_message_sender);
        senderView.setText(msg.getSender().getUsername());
        TextView receiverView = (TextView) findViewById(R.id.read_message_receiver);
        receiverView.setText("me");
        //senderView.setText(msg.getTo());
        TextView timeView = (TextView) findViewById(R.id.read_message_timestamp);
        timeView.setText(""+msg.getDate());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.read_message_action_delete) {
            Toast.makeText(getApplicationContext(), "Deleting...", Toast.LENGTH_SHORT).show();
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
