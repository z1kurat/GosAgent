package com.example.gosagentnewrelease.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.gosagentnewrelease.custom.types.FavoritesLot;
import com.example.gosagentnewrelease.data.PluginModel;
import com.example.gosagentnewrelease.R;
import com.example.gosagentnewrelease.adapter.RVAdapter;
import com.example.gosagentnewrelease.data.UserData;

public class MainActivity extends AppCompatActivity implements RVAdapter.OnCardClickListener, View.OnClickListener {
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userData = new UserData(this);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ImageButton favorites = findViewById(R.id.main_favorites);
        favorites.setOnClickListener(this);

        RecyclerView rv = findViewById(R.id.rv);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        rv.setLayoutManager(manager);

        RVAdapter adapter = new RVAdapter(PluginModel.Data.Cards);
        adapter.setOnCardClickListener(this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onCardClick(View view, int lotType) {
        PluginModel.Data.MarkerType = lotType;

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        PluginModel.Data.IsFavorites = true;

        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFavoritesLots();
    }

    private void getFavoritesLots() {
        PluginModel.Data.FavoritesLots.clear();
        SQLiteDatabase sqLiteDatabase = userData.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(UserData.TABLE_NAME_FAVORITES, null, null, null, null, null,null);

        if (cursor.moveToFirst()) {
            int idNumber = cursor.getColumnIndex(UserData.KEY_NAME_FAVORITES);
            int idType = cursor.getColumnIndex(UserData.KEY_TYPE_FAVORITES);
            do {
                PluginModel.Data.FavoritesLots.add(new FavoritesLot(cursor.getString(idNumber), cursor.getInt(idType)));
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onStop() {
        try {
            PluginModel.Reading.FavoritesLotInformation.executor.shutdown();
        }
        catch (Exception e) {
            Log.d("GosAgent", "bam!");
        }
        super.onStop();
    }
}