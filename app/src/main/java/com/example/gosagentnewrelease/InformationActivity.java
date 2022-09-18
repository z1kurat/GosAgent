package com.example.gosagentnewrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InformationActivity extends AppCompatActivity implements ReadData.OnLotReadListener, AdapterInfoWindow.OnLotClickListener{
    private AdapterInfoWindow adapterInfoWindow;
    private LinearLayout linearLayout;
    private ViewPager2 viewPager2;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        linearLayout = findViewById(R.id.layoutOnAction);
        viewPager2 = findViewById(R.id.viewOnAction);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InformationActivity.this);

        PluginModel.Reading.setOnLotsReadListener(this);
        readData();
    }

    @Override
    public void finish() {
        saveData();
        PluginModel.Data.LotData.clear();
        super.finish();
    }

    private void readData() {
        if (PluginModel.Data.IsFavorites) {
            PluginModel.Reading.FavoritesLotInformation.execute();
        } else if (PluginModel.Data.CoordinatesLot != null) {
            PluginModel.Reading.Lot.execute(PluginModel.Data.CoordinatesLot);
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder result = new StringBuilder();

        for (FavoritesLot favoritesLot : PluginModel.Data.FavoritesLots)
            result.append(favoritesLot.getName()).append(",");

        editor.putString(PluginModel.Data.getIDFavoritesLots(), result.toString());
        editor.apply();
    }

    private void setupAdapterInfoWindow() {
        List<InformationPages> elements = new ArrayList<>();
        int nameLot = 1;
        int countLot = PluginModel.Data.LotData.size();

        for (LotData lot : PluginModel.Data.LotData) {
            InformationPages item = new InformationPages();

            item.setTextLot("Лот " + nameLot + "/" + countLot);
            item.setNameOfLot(lot.number);
            item.setImage(PluginModel.getIconDrawable(this));
            item.setAddresses(lot.location);
            item.setPrice(lot.initialPrice);
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

        adapterInfoWindow = new AdapterInfoWindow(elements);
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
                    "Не удалось загрузить информацию по указанным лотам.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        setupAdapterInfoWindow();
        adapterInfoWindow.setLotClickListener(this);

        viewPager2.setAdapter(adapterInfoWindow);
        setupIndicator();
    }

    @Override
    public void onButtonClick(View view, String lotLink) {
        switch (view.getId()) {
            case R.id.search_button: updateFavoritesLots(lotLink); break;
            case R.id.textLink:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lotLink));
                startActivity(browserIntent);
                break;
        }
    }

    private void updateFavoritesLots(String lotLink) {
        if (PluginModel.Data.existFavoritesLots(lotLink)) {
            PluginModel.Data.deleteFavoritesLot(lotLink);
        } else {
            PluginModel.Data.FavoritesLots.add(new FavoritesLot(lotLink));
        }
    }

}