package com.fevi.fadong;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fevi.fadong.adapter.FaAdapter;
import com.fevi.fadong.adapter.dto.Card;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by 1000742 on 15. 1. 5..
 */
public class FacebookFragment extends Fragment {

    public static final String ARG_MENU_NUMBER = "menu_number";
    public static final String API_URL = "http://10.202.30.133:8080/api/card?category=";

    public FacebookFragment() { }

    public static Fragment newInstance(int position) {
        Fragment fragment = new FacebookFragment();
        Bundle args = new Bundle();
        args.putInt(FacebookFragment.ARG_MENU_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fadong_main, container, false);
        int i = getArguments().getInt(ARG_MENU_NUMBER);
        String menu_title = getResources().getStringArray(R.array.menu_array)[i];

        ListView itemListView = (ListView) rootView.findViewById(R.id.fa_item);

        try {
            List<Card> cards = (List<Card>) new ApiCall().execute(menu_title).get();
            FaAdapter faAdapter = new FaAdapter(getActivity(), R.layout.fragment_facebook, cards);
            itemListView.setAdapter(faAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    public class ApiCall extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String menu = (String) params[0];
            List<Card> cards = parseToCard(getJsonObject(menu));
            return cards;
        }


    }

    private JSONObject getJsonObject(String menu) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + menu).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            InputStream inputStream = connection.getInputStream();
            byte[] contents = new byte[1024];

            int bytesRead=0;
            StringBuilder sb = new StringBuilder();
            while( (bytesRead = inputStream.read(contents)) != -1){
                sb.append(new String(contents, 0, bytesRead));
            }

            return new JSONObject(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Card> parseToCard(JSONObject object) {
        List<Card> cards = new ArrayList<>();
        try {
            JSONArray content = object.getJSONArray("content");
            for (int i = 0; i < content.length(); i++) {
                JSONObject jsonObject = content.getJSONObject(i);
                Card card = new Card();
                card.setId(jsonObject.getString("id"));
                card.setSource(jsonObject.getString("source"));
                card.setPicture(jsonObject.getString("picture"));
                card.setDescription(jsonObject.getString("description"));
                card.setName(jsonObject.getString("name"));
                card.setCategory(jsonObject.getString("category"));
                card.setProfile_image(jsonObject.getString("profile_image"));
                card.setUpdated_time(jsonObject.getString("updated_time"));
                card.setCreated_time(jsonObject.getString("created_time"));
                cards.add(card);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cards;
    }
}


