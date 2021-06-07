package com.example.transit;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CusServicePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private boolean result;
    private Button b1;
    private EditText edit;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_service_page);
        Toolbar toolbar = findViewById(R.id.toolbar_Customer_Service);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout_Customer_Service);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.DP_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userName = getIntent().getStringExtra("username");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.customer_service_nav_drawer, menu);
        return true;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        if (id == R.id.CSP_chat) {
            Toast.makeText(CusServicePage.this, "Chat", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( this, GeneralChatPage.class );
            intent.putExtra("userName",userName);
            intent.putExtra("type","customerservice");
            this.startActivityForResult(intent,1);
        } else if (id == R.id.CSP_SignOut) {
            Intent page = new Intent(this, MainActivity.class);
            startActivity(page);
            return true;

        } else if (id == R.id.CSP_shipping) {
            Intent page = new Intent(this, CusService_Handling_error.class);
            page.putExtra("username",userName);
            startActivity(page);
            return true;
        } else if (id == R.id.CSP_help) {
            Intent page = new Intent(this, CuService_Help.class);
            page.putExtra("username",userName);
            startActivity(page);
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

}
