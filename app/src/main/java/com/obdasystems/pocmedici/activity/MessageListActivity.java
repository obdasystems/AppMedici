package com.obdasystems.pocmedici.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.MessagesAdapter;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.message.model.Message;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener {
    private List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessagesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private Context ctx;

    private int recursiveGetInboxCallCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.message_list_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent mainIntent = new Intent(ctx, WriteMessageActivity.class);
                startActivity(mainIntent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MessagesAdapter(this, messages, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox(0);
                    }
                }
        );
    }

    private void backToMain() {
        Intent mainIntent = new Intent(ctx, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public void onBackPressed() {
        backToMain();
    }

    /**
     * Fetches mail messages by making HTTP request
     * url: https://api.androidhive.info/json/inbox.json
     */
    private void getInbox(int counter) {
        swipeRefreshLayout.setRefreshing(true);

        if(counter<15) {
            String usr = "james";
            String pwd = "bush";

            String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
            if (authorizationToken == null) {
                NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
            }
            authorizationToken = SaveSharedPreference.getAuthorizationToken(this);

            MediciApi apiService = MediciApiClient.createService(MediciApi.class, authorizationToken);

            Call<List<Message>> call = apiService.getInbox();
            call.enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {

                    if (response.isSuccessful()) {
                        // clear the inbox
                        messages.clear();
                        // add all the messages
                        // messages.addAll(response.body());
                        // TODO - avoid looping
                        // the loop was performed to add colors to each message
                        for (Message message : response.body()) {
                            // generate a random color
                            message.setColor(getRandomMaterialColor("400"));
                            messages.add(message);
                        }
                        recursiveGetInboxCallCounter=0;
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        switch (response.code()) {
                            case 401:
                                NetworkUtils.requestNewAuthorizationToken(pwd, usr, ctx);
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch message list (401)");
                                if (!SaveSharedPreference.getAuthorizationIssue(ctx)) {
                                    getInbox(recursiveGetInboxCallCounter++);
                                } else {
                                    String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(ctx);
                                    Toast.makeText(getApplicationContext(), "Unable to fetch message list (401) [" + issueDescription + "]", Toast.LENGTH_LONG).show();
                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch message list (401) [" + issueDescription + "]");
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                break;
                            case 404:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch message list (404)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch message list (404)", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                break;
                            case 500:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch message list (500)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch message list (500)", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                break;
                            default:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch message list (UNKNOWN)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch message list (UNKNOWN)", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch message list: " + t.getMessage());
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch message list: " + t.getStackTrace());
                    Toast.makeText(getApplicationContext(), "Unable to fetch message list..", Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        else {
            Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Max number of calls to getInbox() reached!!");
            Toast.makeText(getApplicationContext(), "Max number of calls to getInbox() reached!!", Toast.LENGTH_LONG).show();
            recursiveGetInboxCallCounter=0;
        }
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getInbox(0);
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the message as important
        Message message = messages.get(position);
        message.setAdverseEvent(!message.getAdverseEvent());
        messages.set(position, message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            Message message = messages.get(position);
            message.setRead(true);
            messages.set(position, message);
            mAdapter.notifyDataSetChanged();

            Intent readMessageIntent = new Intent(this,ReadMessageActivity.class);
            readMessageIntent.putExtra("message",message);
            startActivity(readMessageIntent);
            //Toast.makeText(getApplicationContext(), "Read: " + message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.message_list_menu_action_mode, menu);

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }
}
