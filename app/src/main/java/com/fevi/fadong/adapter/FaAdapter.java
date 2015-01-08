package com.fevi.fadong.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fevi.fadong.MovieActivity;
import com.fevi.fadong.R;
import com.fevi.fadong.adapter.dto.Card;
import com.fevi.fadong.support.CircleTransform;
import com.fevi.fadong.support.ContextString;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 1000742 on 15. 1. 5..
 */
public class FaAdapter extends ArrayAdapter<Card> {

    private Context context;
    private List<Card> cards;

    public FaAdapter(Context context, int resource, List<Card> cards) {
        super(context, resource, cards);
        this.context = context;
        this.cards = cards;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CardAdapterHolder adapterHolder = new CardAdapterHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_facebook, null);
        }

        Card card = cards.get(position);
        adapterHolder.name = (TextView) v.findViewById(R.id.fa_name);
        adapterHolder.description = (TextView) v.findViewById(R.id.fa_description);
        adapterHolder.profile = (ImageView) v.findViewById(R.id.fa_profile);
        adapterHolder.picture = (ImageView) v.findViewById(R.id.fa_picture);
        adapterHolder.time = (TextView) v.findViewById(R.id.fa_time);

        adapterHolder.name.setText(card.getName());
        adapterHolder.description.setText(card.getDescription());
        adapterHolder.time.setText(card.getUpdated_time());

        Picasso.with(context).load(card.getProfile_image()).transform(new CircleTransform()).into(adapterHolder.profile);
        Picasso.with(context).load(card.getPicture()).into(adapterHolder.picture);

        adapterHolder.picture.setOnClickListener(new FacebookPictureClickListener(card.getSource()));
        adapterHolder.description.setOnClickListener(new DescriptionClickListener(card));

        return v;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Card getItem(int position) {
        return cards.get(position);
    }

    class CardAdapterHolder {
        TextView name;
        ImageView profile;
        ImageView picture;
        TextView description;
        TextView time;
    }

    private class FacebookPictureClickListener implements ImageView.OnClickListener {

        private String source;

        private FacebookPictureClickListener(String source) {
            this.source = source;
        }

        @Override
        public void onClick(View v) {
            Log.e("source", source);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(source), "video/*");

            v.getContext().startActivity(intent);
        }
    }

    private class DescriptionClickListener implements TextView.OnClickListener {

        private Card card;

        private DescriptionClickListener(Card card) {
            this.card = card;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), MovieActivity.class);
            intent.putExtra(ContextString.cardProfile, card.getProfile_image());
            intent.putExtra(ContextString.cardName, card.getName());
            intent.putExtra(ContextString.cardTime, card.getUpdated_time());
            intent.putExtra(ContextString.cardPicture, card.getPicture());
            intent.putExtra(ContextString.cardDescription, card.getDescription());
            intent.putExtra(ContextString.cardSource, card.getSource());

            v.getContext().startActivity(intent);
        }
    }
}
