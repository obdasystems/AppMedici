package com.obdasystems.pocmedici.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.FormListAdapter;
import com.obdasystems.pocmedici.listener.OnFormRecyclerViewItemClickListener;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormRepository;
import com.obdasystems.pocmedici.persistence.viewmodel.CtcaeFormListViewModel;

import java.util.ArrayList;
import java.util.List;

public class FormListActivity extends AppCompatActivity {

    private final int CTCAE_FORM_SUBMITTED_CODE = 11111;
    private CtcaeFormListViewModel formListViewModel;

    private ArrayList<String> items;
    private ListView listView;

    private boolean firstLoad = true;

    private int counter = 0;
    private CtcaeFormRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
        /*listView = findViewById(R.id.questionnaire_list);

        items = new ArrayList<>();
        items.add("Questionnaire 1");
        items.add("Questionnaire 2");
        items.add("Questionnaire 3");
        items.add("Questionnaire 4");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAtPosition = parent.getItemAtPosition(position);
                Intent intent = new Intent(view.getContext(),CtcaeFormActivity.class);
                intent.putExtra("clickedItem",itemAtPosition.toString());
                intent.putExtra("clickedItemPosition",position);
                startActivityForResult(intent, CTCAE_FORM_SUBMITTED_CODE);
            }
        });*/

        RecyclerView recyclerView = findViewById(R.id.formRecyclerView);
        final FormListAdapter adapter = new FormListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        adapter.setOnItemClickListener(new OnFormRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                JoinFormWithMaxPageNumberData clickedForm = adapter.getFormAtPosition(position);
                if(clickedForm!=null) {
                    Intent intent = new Intent(view.getContext(),CtcaeFormActivity.class);
                    intent.putExtra("clickedForm", clickedForm);
                    //startActivityForResult(intent, CTCAE_FORM_SUBMITTED_CODE);
                    startActivity(intent);
                }
            }
        });

        formListViewModel = ViewModelProviders.of(this).get(CtcaeFormListViewModel.class);
        formListViewModel.getAllForms().observe(this, new Observer<List<JoinFormWithMaxPageNumberData>>() {
            @Override
            public void onChanged(@Nullable List<JoinFormWithMaxPageNumberData> ctcaeForms) {
                adapter.setForms(ctcaeForms);
                adapter.notifyDataSetChanged();
            }
        });
    }




    /*@Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, getIntent().getStringExtra("name"), Toast.LENGTH_SHORT).show();
        RecyclerView recyclerView = findViewById(R.id.formRecyclerView);
        final FormListAdapter adapter = new FormListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        formListViewModel = ViewModelProviders.of(this).get(CtcaeFormViewModel.class);
        formListViewModel.getAllForms().observe(this, new Observer<List<CtcaeForm>>() {
            @Override
            public void onChanged(@Nullable List<CtcaeForm> ctcaeForms) {
                adapter.setForms(ctcaeForms);
                adapter.notifyDataSetChanged();
            }
        });
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CTCAE_FORM_SUBMITTED_CODE){
            if(resultCode == RESULT_OK) {
                CtcaeForm submittedForm = data.getParcelableExtra("submittedForm");

                /*String removedItem = items.get(submittedItemPosition);
                items.remove(submittedItemPosition);
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();*/
                Toast.makeText(this,"You have just submitted form with id="+submittedForm.getId(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
