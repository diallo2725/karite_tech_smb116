package com.melitech.karitetech.purchases;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.melitech.karitetech.R;
import com.melitech.karitetech.data.local.PurchaseDao;

import com.melitech.karitetech.model.Purchase;
import com.melitech.karitetech.model.RequestOffer;
import com.melitech.karitetech.viewmodels.PurchaseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PurchaseActivity extends AppCompatActivity {

     PurchaseDao achatDao;
    private ExecutorService executorService;
    private Handler mainHandler;
    PurchaseAdapter purchaseAdapter;
    PurchaseViewModel purchaseViewModel;
    RecyclerView recyclerView;

    private ActivityResultLauncher<Intent> formActivityLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achat_activity);
        achatDao = new PurchaseDao(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(getMainLooper());//
        purchaseViewModel = new ViewModelProvider(this).get(PurchaseViewModel.class);

        FloatingActionButton floatingActionButton = findViewById(R.id.addAchat);
        floatingActionButton.setOnClickListener(v -> gotoPurchaseForm());

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Liste des achats");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.brown_color));
        purchaseViewModel.getPurchasesLiveData().observe(this, this::setAdapterView);
        purchaseAdapter = new PurchaseAdapter(this, new ArrayList<>(), purchase -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseActivity.this);
            builder.setTitle("Confirmation");
            builder.setMessage("Voulez-vous vraiment supprimer cet achat ?");
            builder.setPositiveButton("Oui", (dialog, which) -> {
                // Appel via ViewModel pour supprimer
                purchaseViewModel.deletePurchase(purchase);
            });
            builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        recyclerView = findViewById(R.id.purchaseRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(purchaseAdapter);
    }

    public void setAdapterView(List<Purchase> purchases){
        if (purchaseAdapter == null) {
            recyclerView.setAdapter(purchaseAdapter);
        } else {
            // Si déjà initialisé, on met juste à jour la liste
            purchaseAdapter.updateData(purchases);
        }
    }


    public void  gotoPurchaseForm(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        purchaseViewModel.loadPurchases();
    }

}
