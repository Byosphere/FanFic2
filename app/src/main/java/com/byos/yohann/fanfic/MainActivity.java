package com.byos.yohann.fanfic;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.byos.yohann.fanfic.fragments.ExploreFragment;
import com.byos.yohann.fanfic.fragments.ParamFragment;
import com.byos.yohann.fanfic.fragments.ProfileFragment;
import com.byos.yohann.fanfic.fragments.StoryListFragment;
import com.byos.yohann.fanfic.fragments.StoryWriteFragment;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    public static final String USERID = "user_id";
    public static final String USEREMAIL = "user_mail";
    public static final String USERNAME = "user_name";
    public static final String USERPASS = "user_pass";
    public static final String USERFILE = "user_file";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences(USERFILE, Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt(USERID, 0);

        if(userId == 0) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction().add(R.id.fragment_container, getFragment(0)).commit();
            }

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().getItem(0).setChecked(true);
            setHeaderView();
        }
    }

    private void setHeaderView() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hv = navigationView.getHeaderView(0);

        SharedPreferences sharedPreferences = getSharedPreferences(USERFILE, Context.MODE_PRIVATE);

        TextView sideUserName = (TextView) hv.findViewById(R.id.user_name_sidebar);
        sideUserName.setText(sharedPreferences.getString(USERNAME, USERNAME));
        TextView sideUserEmail = (TextView) hv.findViewById(R.id.user_email_sidebar);
        sideUserEmail.setText(sharedPreferences.getString(USEREMAIL, USEREMAIL));

    }

    private Fragment getFragment(int cf) {

        Fragment fragment;
        if (cf == R.id.nav_read) {

            fragment = new StoryListFragment();

        } else if (cf == R.id.nav_write) {

            fragment = new StoryWriteFragment();

        } else if (cf == R.id.nav_explore) {

            fragment = new ExploreFragment();

        } else if (cf == R.id.nav_profile) {

            fragment = new ProfileFragment();

        } else if (cf == R.id.nav_param) {

            fragment = new ParamFragment();

        } else if (cf == R.id.nav_deco) {

            getSharedPreferences(USERFILE, Context.MODE_PRIVATE).edit().clear().commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            fragment = new StoryListFragment();
            finish();

        } else {

            fragment = new StoryListFragment();
        }

        return fragment;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else if (getFragmentManager().getBackStackEntryCount() > 0 ){

            getFragmentManager().popBackStack();

        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        } else if(id == R.id.action_search) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();
        item.setChecked(true);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, getFragment(id)).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
