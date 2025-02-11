package com.example.kaizenarts.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.kaizenarts.R;
import com.example.kaizenarts.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Fragment homeFragment;
    private FirebaseAuth auth;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove conflicting ActionBar settings
        // No need for getSupportActionBar() modifications if using NoActionBar theme

        // Load the HomeFragment
        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
    }

    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            auth.signOut(); // Sign out the user
            Intent intent = new Intent(MainActivity.this, loginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Close MainActivity
        } else if (id == R.id.menu_my_cart) {
            startActivity(new Intent(MainActivity.this, com.example.kaizenarts.activites.cartActivity.class));
        }
        return true;
    }
}
