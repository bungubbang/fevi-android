package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.fevi.fadong.adapter.FaAdapter;
import com.fevi.fadong.adapter.dto.Card;
import com.fevi.fadong.domain.Member;
import com.fevi.fadong.support.ContextString;
import com.fevi.fadong.support.FadongHttpClient;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by 1000742 on 15. 1. 5..
 */
public class FacebookFragment extends Fragment {

    public static final String ARG_MENU_NUMBER = "menu_number";
    public static final String CARDS_API_URL = "http://munsangdong.cafe24.com/api/card?category=";
    public static final String CARD_API_URL = "http://munsangdong.cafe24.com/api/card?id=";
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
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fadong_main, container, false);

        refreshProfile(rootView);

        SharedPreferences preferences = rootView.getContext().getSharedPreferences(getResources().getString(R.string.loginPref), rootView.getContext().MODE_PRIVATE);
        final String id = preferences.getString("id", null);
        final String password = preferences.getString("password", null);

        RoundCornerProgressBar progressBar = (RoundCornerProgressBar) rootView.findViewById(R.id.progress);
        progressBar.setProgressColor(Color.parseColor("#56d2c2"));
        progressBar.setBackgroundLayoutColor(Color.parseColor("#DCDCDC"));

        final Activity activity = (Activity) rootView.getContext();

        int i = getArguments().getInt(ARG_MENU_NUMBER);
        menu_title = getResources().getStringArray(R.array.menu_array)[i];

        CardView noticeButton = (CardView) rootView.findViewById(R.id.notice);
        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), WebviewActivity.class);
                intent.putExtra("url", "http://www.appinkorea.co.kr/fevi/notice.php?id=" + id + "&password=" + password);
                activity.startActivity(intent);
            }
        });

        CardView eventButton = (CardView) rootView.findViewById(R.id.event);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), WebviewActivity.class);
                intent.putExtra("url", "http://www.appinkorea.co.kr/fevi/event.php?id=" + id + "&password=" + password);
                activity.startActivity(intent);
            }
        });

        CardView rubyButton = (CardView) rootView.findViewById(R.id.ruby);
        rubyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), WebviewActivity.class);
                intent.putExtra("url", "http://www.appinkorea.co.kr/fevi/rubi.php?id=" + id + "&password=" + password);
                activity.startActivity(intent);
            }
        });

        CardView munsangButton = (CardView) rootView.findViewById(R.id.munsang);
        munsangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(rootView.getContext(), WebviewActivity.class);
                intent.putExtra("url", "http://www.appinkorea.co.kr/fevi/ms.php?id=" + id + "&password=" + password);
                activity.startActivity(intent);
            }
        });

        ListView itemListView = (ListView) rootView.findViewById(R.id.fa_item);

        faAdapter = new FaAdapter(getActivity(), R.layout.fragment_facebook, this.cards);
        itemListView.setAdapter(faAdapter);
        itemListView.setOnScrollListener(new EndlessScrollListener(5));

        String cacheKey = menu_title + "-" + String.valueOf(currentPage);
        List<Card> loadCacheCard = loadCacheCard(cacheKey);
        cards.addAll(loadCacheCard);
        faAdapter.notifyDataSetChanged();

        checkVid();


        App application = (App)((Activity) rootView.getContext()).getApplication();
        Tracker tracker = application.getDefaultTracker();

        tracker.setScreenName(menu_title);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Fragment")
                .setAction("show")
                .build());


        return rootView;
    }

    private void refreshProfile(View rootView) {
        SharedPreferences preferences = rootView.getContext().getSharedPreferences(getResources().getString(R.string.loginPref), rootView.getContext().MODE_PRIVATE);
        String id = preferences.getString("id", null);
        String password = preferences.getString("password", "");
        if(id != null) {
            new CheckProfile(rootView).execute(id, password);
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
            this.member.setPassword(params[1]);
            return new FadongHttpClient().sendLogin(CHECK_STATS_URL, member.getParameter());
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.isEmpty()) {
                return;
            }
            Uri uri = Uri.parse("?" + result);
            Integer exp = Integer.valueOf(uri.getQueryParameter("exp"));
            Integer next_exp = Integer.valueOf(uri.getQueryParameter("next_exp"));

            if(exp.equals(next_exp)) {
                Activity activity = (Activity) rootView.getContext();
                Intent intent = new Intent(activity, WebviewActivity.class);
                intent.putExtra("url", "http://appinkorea.co.kr/fevi/level_up.php?id=" + member.getId() + "&password=" + member.getPassword());
                activity.startActivity(intent);
            }

            TextView barRuby = (TextView) view.findViewById(R.id.profile_bar_ruby);
            barRuby.setText("Lev : " + uri.getQueryParameter("level") + " ( " + uri.getQueryParameter("exp") + " / " + uri.getQueryParameter("next_exp") + " ), Ruby : " + uri.getQueryParameter("rubi"));

            RoundCornerProgressBar progressBar = (RoundCornerProgressBar) view.findViewById(R.id.progress);
            progressBar.setMax(Float.parseFloat(uri.getQueryParameter("next_exp")));
            progressBar.setProgress(Float.parseFloat(uri.getQueryParameter("exp")));
        }
    }

    public class CallCards extends AsyncTask<String, Void, List<Card>> {

        @Override
        protected void onPostExecute(List<Card> cards) {
            super.onPostExecute(cards);

            loading = true;
        }

        @Override
        protected List<Card> doInBackground(String... param) {
            Log.d("mundong", "call api : " + CARDS_API_URL + param[0] + "&page=" + param[1]);
            List<Card> cards = parseToCard(getJsonObject(CARDS_API_URL + param[0] + "&page=" + param[1]));
            return cards;
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
                List<Card> loadCacheCard = loadCacheCard(menu_title + "-" + String.valueOf(currentPage));
                cards.addAll(loadCacheCard);
                faAdapter.notifyDataSetChanged();
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
    private List<Card> loadCacheCard(String param) {
        String[] params = param.split("-");
        AsyncTask<String, Void, List<Card>> execute = new CallCards().execute(params[0], params[1]);
        try {
            return execute.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }
}


