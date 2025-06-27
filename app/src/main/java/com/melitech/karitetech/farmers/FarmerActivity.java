package com.melitech.karitetech.farmers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.melitech.karitetech.R;
import com.melitech.karitetech.data.local.FarmerDao;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.repository.FarmerRepository;
import com.melitech.karitetech.viewmodels.FarmerViewModel;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FarmerActivity extends AppCompatActivity {
     FarmerDao farmerDao;
    FloatingActionButton floatingActionButton;
    FarmerAdapter farmerAdapter;
    private ActivityResultLauncher<Intent> formActivityLauncher;
    private FarmerViewModel farmerViewModel;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer);
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Liste des producteurs");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.brown_color));
        farmerDao = new FarmerDao(this);

        farmerViewModel = new ViewModelProvider(this).get(FarmerViewModel.class);
        farmerViewModel.getFarmersLiveData().observe(this, this::setAdapterView);

        farmerAdapter = new FarmerAdapter(this, new ArrayList<>(), new OnFarmerActionListener() {
            @Override
            public void onEditFarmer(Farmer farmer) {
                Intent intent = new Intent(FarmerActivity.this, FarmerFormActivity.class);
                intent.putExtra("farmer", farmer);
                startActivity(intent);
            }

            @Override
            public void onDeleteFarmer(Farmer farmer) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FarmerActivity.this);
                builder.setMessage("Voulez-vous vraiment supprimer ce producteur ?")
                        .setPositiveButton("Oui", (dialog, id) -> {
                            farmerViewModel.deleteFarmer(farmer);
                            Toast.makeText(FarmerActivity.this, "Producteur supprimé", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Non", (dialog, id) -> dialog.dismiss());
                builder.setTitle("Suppression");
                builder.show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.farmerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(farmerAdapter);

        floatingActionButton = findViewById(R.id.addFamrer);
        floatingActionButton.setOnClickListener(v -> gotoFarmerForm());
    }

    @Override
    protected void onResume() {
        super.onResume();
        farmerViewModel.loadFarmers();
    }



   public void setAdapterView(List<Farmer> farmers) {
       if (farmerAdapter != null) {
           farmerAdapter.updateData(farmers);
       }
   }

    public void  gotoFarmerForm(){
        Intent intent = new Intent(this, FarmerFormActivity.class);
        startActivity(intent);
    }



}


