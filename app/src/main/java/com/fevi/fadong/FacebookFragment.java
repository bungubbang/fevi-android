package com.fevi.fadong;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
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
    public static final String API_URL = "http://fe-vi.com/api/card?category=";

    private int currentPage = 0;
    FaAdapter faAdapter;
    String menu_title;
    List<Card> cards = new ArrayList<>();

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
        menu_title = getResources().getStringArray(R.array.menu_array)[i];

        ListView itemListView = (ListView) rootView.findViewById(R.id.fa_item);

        try {
            cards = (List<Card>) new ApiCall().execute(menu_title, currentPage).get();
            faAdapter = new FaAdapter(getActivity(), R.layout.fragment_facebook, cards);
            itemListView.setAdapter(faAdapter);
            itemListView.setOnScrollListener(new EndlessScrollListener(5));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    public class ApiCall extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String menu = (String) params[0];
            int page = (int) params[1];
            List<Card> cards = parseToCard(getJsonObject(menu, page));
            return cards;
        }


    }

    private JSONObject getJsonObject(String menu, int page) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + menu + "&page=" + String.valueOf(page)).openConnection();
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
        if(object == null) {
            return cards;
        }

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


    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 3;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                try {
                    cards.addAll((List<Card>) new ApiCall().execute(menu_title, currentPage).get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                faAdapter.notifyDataSetChanged();
                loading = true;

            }
        }
    }
}


