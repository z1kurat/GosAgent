package com.example.gosagentnewrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class InformationActivity extends AppCompatActivity implements ReadData.OnLotReadListener{
    private AdapterInfoWindow adapterInfoWindow;
    private LinearLayout linearLayout;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        linearLayout = findViewById(R.id.layoutOnAction);
        viewPager2 = findViewById(R.id.viewOnAction);

        PluginModel.Reading.setOnLotsReadListener(this);
        PluginModel.Reading.Lot.execute(PluginModel.Data.CoordinatesLot);
    }

    @Override
    public void finish() {
        PluginModel.clear();
        adapterInfoWindow.clear();
        linearLayout = null;
        viewPager2 = null;
        super.finish();
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
        viewPager2.setAdapter(adapterInfoWindow);
        setupIndicator();
    }
}