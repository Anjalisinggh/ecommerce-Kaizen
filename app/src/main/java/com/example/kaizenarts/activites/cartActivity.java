package com.example.kaizenarts.activites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.MyCartAdapter;
import com.example.kaizenarts.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class cartActivity extends AppCompatActivity {

    private TextView overAllAmount;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<MyCartModel> cartModelList;
    private MyCartAdapter cartAdapter;

    // Buy Now Button
    private Button buyNow;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize FirebaseAuth and Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Set up the toolbar
        toolbar = findViewById(R.id.my_cart_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> finish());

        // Initialize UI components
        overAllAmount = findViewById(R.id.textView3);
        buyNow = findViewById(R.id.buy_now);
        recyclerView = findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this, cartModelList);
        recyclerView.setAdapter(cartAdapter);

        // Fetch cart items from Firestore
        fetchCartItems();

        // Buy Now Button Click Listener
        buyNow.setOnClickListener(view -> {
            if (!cartModelList.isEmpty()) {
                Intent intent = new Intent(cartActivity.this, AddressActivity.class);
                startActivity(intent);
            } else {
                Log.e("CartActivity", "Cart is empty, cannot proceed to address selection.");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register broadcast receiver for total amount updates
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("MyTotalAmount"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister the broadcast receiver to prevent memory leaks
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private void fetchCartItems() {
        if (auth.getCurrentUser() == null) {
            Log.e("CartActivity", "User not logged in");
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        firestore.collection("AddToCart").document(userId).collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        cartModelList.clear();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                            if (myCartModel != null) {
                                cartModelList.add(myCartModel);
                            }
                        }
                        cartAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error fetching cart items", task.getException());
                    }
                });
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (overAllAmount != null) {
                int totalBill = intent.getIntExtra("totalAmount", 0);
                overAllAmount.setText("Total Amount: " + totalBill + " Rs ");
            } else {
                Log.e("cartActivity", "TextView overAllAmount is null");
            }
        }
    };
}
