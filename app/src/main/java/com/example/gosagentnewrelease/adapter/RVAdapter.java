package com.example.gosagentnewrelease.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gosagentnewrelease.custom.types.Card;
import com.example.gosagentnewrelease.R;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CardViewHolder>{
    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView cardDescription;
        ImageView cardImage;
        int lotType;

        public CardViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

            cardDescription = itemView.findViewById(R.id.card_description);
            cardImage = itemView.findViewById(R.id.card_image);
        }

        @Override
        public void onClick(View v) {
            mListener.onCardClick(v, lotType);
        }

    }

    private final List<Card> cards;
    private static OnCardClickListener mListener;

    public interface OnCardClickListener {
        void onCardClick(View view, int lotType);
    }

    public RVAdapter(List<Card> cards){
        this.cards = cards;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_pattern, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        cardViewHolder.cardDescription.setText(cards.get(i).getDescription());
        cardViewHolder.cardImage.setImageResource(cards.get(i).getImageID());
        cardViewHolder.lotType = cards.get(i).getType();
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}