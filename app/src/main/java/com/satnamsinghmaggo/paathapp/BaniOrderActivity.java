package com.satnamsinghmaggo.paathapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.satnamsinghmaggo.paathapp.adapter.BaniOrderAdapter;
import com.satnamsinghmaggo.paathapp.util.BaniOrderTouchHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaniOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BaniOrderAdapter adapter;
    private List<String> banis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bani_order);

        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.bani_order_title);
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        banis = new ArrayList<>();
        banis.add("Japji Sahib");
        banis.add("Jaap Sahib");
        banis.add("Tav Prasad Savaiye");
        banis.add("Chaupai Sahib");
        banis.add("Anand Sahib");

        adapter = new BaniOrderAdapter(banis, (fromPosition, toPosition) -> {
            Collections.swap(banis, fromPosition, toPosition);
            adapter.notifyItemMoved(fromPosition, toPosition);
        });

        recyclerView.setAdapter(adapter);

        BaniOrderTouchHelper touchHelper = new BaniOrderTouchHelper(adapter.getMoveListener());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 