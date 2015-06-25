package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.fevi.fadong.adapter.FaAdapter;
import com.fevi.fadong.adapter.dto.Card;
import com.fevi.fadong.domain.Member;
import com.fevi.fadong.support.ContextString;
import com.fevi.fadong.support.FadongHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1000742 on 15. 1. 5..
 */
public class FacebookFragment extends Fragment {

    public static final String ARG_MENU_NUMBER = "menu_number";
    public static final String CARDS_API_URL = "http://fe-vi.com/api/card?category=";
    public static final String CARD_API_URL = "http://fe-vi.com/api/card?id=";
    private static final String CHECK_STATS_URL = "http://www.appinkorea.co.kr/fevi/stats.php";

    private int currentPage = 0;
    FaAdapter faAdapter;
    String menu_title;
    List<Card> cards = new ArrayList<>();
    View rootView;
    private boolean loading = true;

    public FacebookFragment() { }

    public static Fragment newInstance(int position) {
        Fragment fragment = new FacebookFragment();
        Bundle args = new Bundle();
        args.putInt(FacebookFragment.ARG_MENU_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshProfile(rootView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fadong_main, container, false);

        refreshProfile(rootView);

        int i = getArguments().getInt(ARG_MENU_NUMBER);
        menu_title = getResources().getStringArray(R.array.menu_array)[i];

        ListView itemListView = (ListView) rootView.findViewById(R.id.fa_item);

        new CallCards().execute(menu_title, currentPage);
        faAdapter = new FaAdapter(getActivity(), R.layout.fragment_facebook, cards);
        itemListView.setAdapter(faAdapter);
        itemListView.setOnScrollListener(new EndlessScrollListener(5));

        checkVid();
        return rootView;
    }

    private void refreshProfile(View rootView) {
        SharedPreferences preferences = rootView.getContext().getSharedPreferences(getResources().getString(R.string.loginPref), rootView.getContext().MODE_PRIVATE);
        String id = preferences.getString("id", null);
        if(id != null) {
            new CheckProfile(rootView).execute(id);
        }
    }

    public class CheckProfile extends AsyncTask <String, Void, String> {

        private View view;
        private Member member;

        public CheckProfile(View view) {
            this.view = view;
            this.member = new Member();
        }

        @Override
        protected String doInBackground(String... params) {
            this.member.setId(params[0]);
            return new FadongHttpClient().sendLogin(CHECK_STATS_URL, member.getParameter());
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Uri uri = Uri.parse("?" + result);
            Integer exp = Integer.valueOf(uri.getQueryParameter("exp"));
            Integer next_exp = Integer.valueOf(uri.getQueryParameter("next_exp"));

            if(exp.equals(next_exp)) {
                Activity activity = (Activity) rootView.getContext();
                Intent intent = new Intent(activity, LevelUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            }

            TextView barName = (TextView) view.findViewById(R.id.profile_bar_name);
            barName.setText(member.getId());
            TextView barRuby = (TextView) view.findViewById(R.id.profile_bar_ruby);
            barRuby.setText("Lev : " + uri.getQueryParameter("level") + ", Exp : " + uri.getQueryParameter("exp") + " / " + uri.getQueryParameter("next_exp") + ", Ruby : " + uri.getQueryParameter("rubi"));
        }
    }

    public class CallCards extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String menu = (String) params[0];
            int page = (int) params[1];
            return parseToCard(getJsonObject(CARDS_API_URL + menu + "&page=" + String.valueOf(page)));
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            List<Card> result = (List<Card>) o;
            cards.addAll(result);
            faAdapter.notifyDataSetChanged();
            loading = true;
        }
    }

    private JSONObject getJsonObject(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
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
                new CallCards().execute(menu_title, currentPage);
            }
        }
    }

    private void checkVid() {
        Activity activity = (Activity) rootView.getContext();
        Intent intent = activity.getIntent();
        String vid = intent.getStringExtra("vid");
        if(vid != null && !vid.isEmpty()) {
            new CallCard().execute(vid);
        }
    }

    public class CallCard extends AsyncTask {

        String vid;

        @Override
        protected Object doInBackground(Object[] params) {
            vid = (String) params[0];
            return parseToCard(getJsonObject(CARD_API_URL + vid));
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Activity activity = (Activity) rootView.getContext();
            List<Card> cardList = (List<Card>) o;
            if(!cardList.isEmpty()) {
                Intent movieIntent = new Intent(activity, MovieActivity.class);

                Card card = cardList.get(0);
                movieIntent.putExtra(ContextString.cardId, card.getId());
                movieIntent.putExtra(ContextString.cardProfile, card.getProfile_image());
                movieIntent.putExtra(ContextString.cardName, card.getName());
                movieIntent.putExtra(ContextString.cardTime, card.getUpdated_time());
                movieIntent.putExtra(ContextString.cardPicture, card.getPicture());
                movieIntent.putExtra(ContextString.cardDescription, card.getDescription());
                movieIntent.putExtra(ContextString.cardSource, card.getSource());

                activity.startActivity(movieIntent);
            } else {
                Log.e("fadong", "invalid vid");
            }
        }
    }
}


