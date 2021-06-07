package com.example.transit.ManagerMain;

import android.content.Intent;

import com.example.transit.GeneralChatPage;
import com.example.transit.MainActivity;
import com.example.transit.ManagerDriverInfo.ManagerDriverInfoPage;
import com.example.transit.ManagerRequestList.ManagerRequstListPage;
import com.example.transit.R;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ManagerPage extends AppCompatActivity implements ManagerMainContract.MVPview{
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigation;
    private String userName;

    private EditText jobInProgress;
    private EditText jobPending;
    private ManagerMainPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_page);
        presenter = new ManagerMainPresenter(this);
        jobInProgress = findViewById(R.id.jobInProgress);
        jobPending = findViewById(R.id.jobPending);

        userName = getIntent().getStringExtra("username");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        linkActivity();
        getJobSituationInProgress();
        getJobSituationPending();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void linkActivity() {
        navigation = findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_check_request:
                        Intent requestList = new Intent(ManagerPage.this, ManagerRequstListPage.class);
                        startActivity(requestList);
                        break;
                    case R.id.nav_check_driver:
                        Intent driverInfo = new Intent(ManagerPage.this, ManagerDriverInfoPage.class);
                        startActivity(driverInfo);
                        break;
                    case R.id.nav_manager_chat:
                        Intent intent = new Intent( ManagerPage.this, GeneralChatPage.class );
                        intent.putExtra("userName",userName);
                        intent.putExtra("type","manager");
                        startActivityForResult(intent,1);
                        break;
                    case R.id.M_SignOut:
                        Intent mainPage = new Intent( ManagerPage.this, MainActivity.class );
                        mainPage.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        startActivity( mainPage );
                }
                return false;
            }
        });

    }

    private void getJobSituationInProgress() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/ManagerPage/ManagerJobInProgressSituationPage";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jobInProgress.setText("Job in Progress: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getJobSituationPending() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + getString(R.string.ip_address) + ":8080/ManagerPage/ManagerJobPendingSituationPage";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        jobPending.setText("Job Pending: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}