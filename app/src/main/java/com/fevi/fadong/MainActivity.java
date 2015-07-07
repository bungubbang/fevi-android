package com.fevi.fadong;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fevi.fadong.adapter.MenuListAdapter;
import com.fevi.fadong.adapter.dto.MenuList;
import com.fevi.fadong.domain.Member;
import com.fevi.fadong.support.MemberService;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.common.collect.Lists;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final String VERSION_CHECK_URL = "http://munsangdong.cafe24.com/version";

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout leftDrawer;
    private Member member;

    SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CheckVersion().execute();

        loginPreferences = getSharedPreferences(getResources().getString(R.string.loginPref), MODE_PRIVATE);
        checkLogin();
        mTitle = "Fadong";

        String[] menus = getResources().getStringArray(R.array.menu_array);
        mPlanetTitles = menus;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        leftDrawer = (LinearLayout) findViewById(R.id.left_drawer);


        List<MenuList> menuLists = Lists.newArrayList(
                new MenuList(getResources().getDrawable(R.drawable.appbar_star_invincible_color), menus[0]),
                new MenuList(getResources().getDrawable(R.drawable.appbar_flag_bear_color), menus[1]),
                new MenuList(getResources().getDrawable(R.drawable.appbar_hardware_headset_color), menus[2]),
                new MenuList(getResources().getDrawable(R.drawable.appbar_bike_color), menus[3]),
                new MenuList(getResources().getDrawable(R.drawable.appbar_controller_snes_color), menus[4]));

        MenuListAdapter menuListAdapter = new MenuListAdapter(this, R.layout.drawer_list_item, menuLists);
        // Set the adapter for the list view
        mDrawerList.setAdapter(menuListAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.fadong_logout:
                MemberService.logout(this);
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        SharedPreferences.Editor loginPreEdit = loginPreferences.edit();
        loginPreEdit.putString("id", "");
        loginPreEdit.putString("password", "");
        loginPreEdit.putBoolean("isAutoLogin", false);

        loginPreEdit.apply();

        Intent loginIntent = new Intent(this, LoginActivity.class);
        this.startActivity(loginIntent);
        this.finish();
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {

        Fragment fragment = FacebookFragment.newInstance(position);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(leftDrawer);

        App application = (App) getApplication();
        Tracker tracker = application.getDefaultTracker();

        tracker.setScreenName("Facebook Fragment");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Fragment")
                .setAction("click")
                .setLabel(mPlanetTitles[position])
                .build());
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void checkLogin() {
        Intent intent = getIntent();
        member = (Member) intent.getSerializableExtra("member");
        if(member == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            this.startActivity(loginIntent);
            this.finish();
        }
    }

    class CheckVersion extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

                HttpURLConnection connection = (HttpURLConnection) new URL(VERSION_CHECK_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                InputStream inputStream = connection.getInputStream();
                byte[] contents = new byte[1024];

                int bytesRead=0;
                StringBuilder sb = new StringBuilder();
                while( (bytesRead = inputStream.read(contents)) != -1){
                    sb.append(new String(contents, 0, bytesRead));
                }

                JSONObject result = new JSONObject(sb.toString());
                String requestVersion = result.getString("version");
                boolean forceUpdate = result.getBoolean("forceUpdate");
                if(!versionName.equals(requestVersion)) {
                    final AlertDialog.Builder builder;
                    if(forceUpdate) {
                        builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("새로운 버젼이 출시 되었습니다.")
                                .setMessage("필수 업데이트 버젼입니다. 마켓으로 이동합니다.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        moveMarket();
                                    }
                                });
                    } else {
                        builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("새로운 버젼이 출시 되었습니다.")
                                .setMessage("마켓으로 이동하시겠습니까?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        moveMarket();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null);
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                }

            } catch (PackageManager.NameNotFoundException | IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void moveMarket() {

    }
}
