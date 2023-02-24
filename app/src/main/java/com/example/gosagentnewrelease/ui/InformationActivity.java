package com.example.gosagentnewrelease.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gosagentnewrelease.adapter.AdapterInfoWindow;
import com.example.gosagentnewrelease.custom.types.FavoritesLot;
import com.example.gosagentnewrelease.custom.types.LotData;
import com.example.gosagentnewrelease.adapter.InformationPages;
import com.example.gosagentnewrelease.data.PluginModel;
import com.example.gosagentnewrelease.R;
import com.example.gosagentnewrelease.data.ReadData;
import com.example.gosagentnewrelease.data.UserData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class InformationActivity extends AppCompatActivity implements ReadData.OnLotReadListener, AdapterInfoWindow.OnLotClickListener {
    private AdapterInfoWindow adapterInfoWindow;
    private LinearLayout linearLayout;
    private ViewPager2 viewPager2;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        linearLayout = findViewById(R.id.layoutOnAction);
        viewPager2 = findViewById(R.id.viewOnAction);

        userData = new UserData(this);

        PluginModel.Reading.setOnLotsReadListener(this);
        readData();
    }

    @Override
    public void finish() {
        adapterInfoWindow = null;
        linearLayout.removeAllViews();
        viewPager2.removeAllViews();
        super.finish();
    }

    private void readData() {
        if (PluginModel.Data.IsFavorites) {
            PluginModel.Reading.FavoritesLotInformation.execute();
        } else if (PluginModel.Data.CoordinatesLotLat != null && PluginModel.Data.CoordinatesLotLang != null) {
            List<LotData> currentLot = new ArrayList<>();

            for (LotData lotData : PluginModel.Data.LotData)
                if (lotData.coordinatesX == PluginModel.Data.CoordinatesLotLat && lotData.coordinatesY == PluginModel.Data.CoordinatesLotLang)
                    currentLot.add(lotData);

            if (currentLot.size() == 0)
                return;

            setupAdapterInfoWindow(currentLot);
            adapterInfoWindow.setLotClickListener(this);

            viewPager2.setAdapter(adapterInfoWindow);
            setupIndicator();
        }
    }

    private void setupAdapterInfoWindow(List<LotData> currentLotData) {
        if (currentLotData == null)
            currentLotData = PluginModel.Data.LotData;

        List<InformationPages> elements = new ArrayList<>();
        int countLot = currentLotData.size();
        int nameLot = 1;

        for (LotData lot : currentLotData) {
            InformationPages item = new InformationPages();

            item.setTextLot("Лот " + nameLot + "/" + countLot);
            item.setNameOfLot(lot.number);
            item.setImageLinks(lot.imageLinks);
            item.setImage(PluginModel.getIconDrawable(this));
            item.setAddresses(lot.location);
            item.setPrice(String.valueOf(lot.initialPrice));
            item.setLink(lot.link);
            item.setSelectedLot(lot);

            StringBuilder description = new StringBuilder();
            for (String information : lot.description) {
                description.append(information).append("\n\n");
            }

            item.setDescription(description.toString().trim());

            elements.add(item);
            nameLot++;
        }

        adapterInfoWindow = new AdapterInfoWindow(elements, this);
    }

    private void setupIndicator() {
        ImageView[] indicators = new ImageView[adapterInfoWindow.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                        ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int index = 0; index < indicators.length; index++) {
            indicators[index] = new ImageView(getApplicationContext());
            indicators[index].setLayoutParams(layoutParams);
            linearLayout.addView(indicators[index]);
        }
    }

    @Override
    public void onLotsRead(Boolean isDone) {
        if (!isDone) {
            Toast.makeText(
                    this,
                    "Не удалось  загрузить информацию по указанным лотам.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        setupAdapterInfoWindow(null);
        adapterInfoWindow.setLotClickListener(this);

        viewPager2.setAdapter(adapterInfoWindow);
        setupIndicator();
        //!todo: remove
        try {
            PluginModel.Data.IsFavorites = false;
            PluginModel.Reading.FavoritesLotInformation.executor.shutdown();
        }catch (Exception e) {
            Log.e("Bam!", "");
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onButtonClick(View view, InformationPages informationPages) {
        switch (view.getId()) {
            case R.id.search_button: updateFavoritesLots(informationPages.getNameOfLot()); break;
            case R.id.textLink:
                // toDO hide on Adapter
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(informationPages.getLink()));
                startActivity(browserIntent);
                break;
        }
    }

    private void updateFavoritesLots(String number) {
        SQLiteDatabase sqLiteDatabase = userData.getWritableDatabase();

        if (PluginModel.Data.existFavoritesLots(number)) {
            try {
                PluginModel.Data.deleteFavoritesLot(new FavoritesLot(number, PluginModel.Data.MarkerType));
                sqLiteDatabase.delete(UserData.TABLE_NAME_FAVORITES, UserData.KEY_NAME_FAVORITES + "=?", new String[]{number});
            } catch (Exception e) {
                Log.e("DB", e.getMessage());
            }

        } else {
            PluginModel.Data.FavoritesLots.add(new FavoritesLot(number, PluginModel.Data.MarkerType));

            ContentValues contentValues = new ContentValues();
            contentValues.put(UserData.KEY_NAME_FAVORITES, number);
            contentValues.put(UserData.KEY_TYPE_FAVORITES, PluginModel.Data.MarkerType);

            sqLiteDatabase.insert(UserData.TABLE_NAME_FAVORITES, null, contentValues);
        }
    }
}