package com.melitech.karitetech.parcs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.melitech.karitetech.data.local.ParcDao;
import com.melitech.karitetech.model.Parc;
import com.melitech.karitetech.model.RequestOffer;
import com.melitech.karitetech.viewmodels.ParcViewModel;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParcActivity extends AppCompatActivity {
     ParcDao parcDao;
    private ExecutorService executorService;
    private Handler mainHandler;
    ParcAdapter parcAdapter;
    ParcViewModel parcViewModel;

    private ActivityResultLauncher<Intent> formActivityLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parc_activity);
        parcDao = new ParcDao(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(getMainLooper());//

        FloatingActionButton floatingActionButton = findViewById(R.id.addParc);
        floatingActionButton.setOnClickListener(v -> gotoParcForm());

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Liste des parcs à bois");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.brown_color));

        parcViewModel = new ViewModelProvider(this).get(ParcViewModel.class);
        List<Parc> parcs = parcViewModel.getParcsLiveData().getValue();
        parcViewModel.getParcsLiveData().observe(this, this::setAdapterView);

        parcAdapter = new ParcAdapter(this, parcs, parc -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ParcActivity.this);
            builder.setTitle("Confirmation");
            builder.setMessage("Voulez-vous vraiment supprimer cette offre?");
            builder.setPositiveButton("Oui", (dialog, which) -> {
               // parcViewModel.deleteParc(parc);
            });
            builder.setNegativeButton("Non", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        RecyclerView recyclerView = findViewById(R.id.parcRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(parcAdapter);

    }

    public void setAdapterView(List<Parc> parcs){
        RecyclerView recyclerView = findViewById(R.id.parcRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        parcAdapter = new ParcAdapter(this, parcs, parc -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ParcActivity.this);
            builder.setTitle("Confirmation");
            builder.setMessage("Voulez-vous vraiment supprimer cette offre?");
            builder.setPositiveButton("Oui", (dialog, which) -> {
                executorService.execute(() -> {
                    List<Parc> updateParcs = ParcDao.getAllParcs();
                    mainHandler.post(() -> {});
                });
            });
            builder.setNegativeButton("Non", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        recyclerView.setAdapter(parcAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        parcViewModel.loadParcs();
    }


    public void  gotoParcForm(){
        Intent intent = new Intent(this, ParcFormActivity.class);
         startActivity(intent );
    }
}
