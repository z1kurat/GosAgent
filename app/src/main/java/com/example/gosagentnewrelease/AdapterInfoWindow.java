package com.example.gosagentnewrelease;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterInfoWindow extends RecyclerView.Adapter<AdapterInfoWindow.LotViewHolder>{
    static class LotViewHolder extends RecyclerView.ViewHolder {
        private final Button searchButton;
        private final ImageView imageView;
        private final TextView textNumber;
        private final TextView textLot;
        private final TextView textDescription;
        private final TextView link;
        private final TextView textAddresses;
        private final TextView textPrice;

        public LotViewHolder(View itemView) {
            super(itemView);
            searchButton = itemView.findViewById(R.id.search_button);
            link = itemView.findViewById(R.id.textLink);
            imageView = itemView.findViewById(R.id.imageType);
            textNumber = itemView.findViewById(R.id.textNumber);
            textLot = itemView.findViewById(R.id.textLot);
            textDescription = itemView.findViewById(R.id.textDescription);
            textAddresses = itemView.findViewById(R.id.textAdress);
            textPrice = itemView.findViewById(R.id.textPrice);
        }

        void setOnPageInformation(InformationPages information) {
            imageView.setBackground(information.getImage());
            textNumber.setText(information.getNameOfLot());
            textLot.setText(information.getTextLot());
            textDescription.setText(information.getDescription());
            textAddresses.setText(information.getAddresses());
            textPrice.setText(information.getPrice());

            String linkText = "<a href=\"" + information.getLink() + "\">Подробнее</a>";
            link.setText(Html.fromHtml(linkText, null, null));
            link.setPaintFlags(0);
            link.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private final List<InformationPages> informationOnPageList;

    public AdapterInfoWindow(List<InformationPages> informationOnPageList) {
        this.informationOnPageList = informationOnPageList;
    }

    public void clear() {
        informationOnPageList.clear();
    }

    @Override
    public LotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.information_lot_pattern,
                                                                                            parent, false));
    }

    @Override
    public void onBindViewHolder(LotViewHolder lotViewHolder, int position) {
        lotViewHolder.setOnPageInformation(informationOnPageList.get(position));
    }

    @Override
    public int getItemCount() {
        return informationOnPageList.size();
    }

}
