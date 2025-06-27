package com.melitech.karitetech.offers;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.melitech.karitetech.R;
import com.melitech.karitetech.data.local.CertificationDao;
import com.melitech.karitetech.data.local.OfferDao;
import com.melitech.karitetech.data.local.PackagingDao;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Certification;
import com.melitech.karitetech.model.Packaging;
import com.melitech.karitetech.model.RequestOffer;
import com.melitech.karitetech.repository.OfferRepository;
import com.melitech.karitetech.viewmodels.NetworkViewModel;
import com.melitech.karitetech.viewmodels.OffreViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OfferActivity extends AppCompatActivity {
     CertificationDao certificationDao;
     PackagingDao packagingDao;
     OfferDao offerDao;
    OffreViewModel offreViewModel;


    OfferAdapter offerAdapter;

    private BroadcastReceiver localReceiver;

    private ActivityResultLauncher<Intent> formActivityLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_product_activity);
        certificationDao = new CertificationDao(this);
        packagingDao = new PackagingDao(this);
        offerDao = new OfferDao(this);
        NetworkViewModel viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);


        FloatingActionButton floatingActionButton = findViewById(R.id.addOffer);
        floatingActionButton.setOnClickListener(v -> gotoOfferForm());

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Liste des offres");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.brown_color));
        getCertifications();
        getPackages();


        viewModel.getNetworkStatus(this).observe(this, isConnected -> {
            if (isConnected) {
                saveRemoteOffersOnLocal();
            }
        });
        offreViewModel = new ViewModelProvider(this).get(OffreViewModel.class);
        offreViewModel.getOffersLiveData().observe(this, this::setAdapterView);
        offerAdapter = new OfferAdapter(OfferActivity.this, new ArrayList<>(),new OnOfferActionListener(){
            @Override
            public void onEditOffer(RequestOffer offer) {
                Intent intent = new Intent(OfferActivity.this, OfferFormActivity.class);
                intent.putExtra("offer",offer);
                startActivity(intent);
            }

            @Override
            public void onDeleteOffer(RequestOffer farmer) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OfferActivity.this);
                builder.setMessage("Voulez-vous vraiment supprimer cette offre ?")
                        .setPositiveButton("Oui", (dialog, id) -> {
                            offreViewModel.deleteOffer(farmer);
                            Toast.makeText(OfferActivity.this, "Offre supprimée avec succès", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Non", (dialog, id) -> {
                            dialog.dismiss();
                        });
                builder.setTitle("Suppression");
                AlertDialog dialog = builder.create();
                dialog.show();
            }


            @Override
            public void onScanOffer(RequestOffer offer) {
                ScanOptions options = new ScanOptions();
                options.setPrompt("Scanner un QR Code");
                options.setBeepEnabled(true);
                options.setOrientationLocked(false);
                options.setCaptureActivity(CaptureActivity.class); // facultatif
                qrLauncher.launch(options);
            }

        });

        RecyclerView recyclerView = findViewById(R.id.offerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(offerAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        offreViewModel.loadOffers();
    }

    protected void getCertifications(){
        List<Certification> certifications = CertificationDao.getCertifications();
        for(Certification certification: certifications){
            Log.d("Certification: ", certification.getName());
        }
    }

    protected void getPackages(){
        List<Packaging> packagings = PackagingDao.getPackagings();
        for(Packaging packging: packagings){
            Log.d("Certification: ", packging.getName());
        }
    }

    private final ActivityResultLauncher<ScanOptions> qrLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String qrValue = result.getContents();
                    boolean exist = OfferDao.checkSellerExist(qrValue);
                    if(exist){
                        AlertDialog.Builder builder = new AlertDialog.Builder(OfferActivity.this);
                        builder.setMessage("Qrcode ajouté avec succès")
                                .setTitle("Scanner")
                        .setPositiveButton("OK", (dialog, id) -> {
                            dialog.dismiss();
                            builder.show();
                        });
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OfferActivity.this);
                        builder.setMessage("Qrcode non trouvée").setPositiveButton("OK", (dialog, id) -> {
                            dialog.dismiss();
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }
            });



    public void setAdapterView(List<RequestOffer> offers) {
        if (offerAdapter != null) {
            offerAdapter.updateData(offers);
        }
    }



    public void  gotoOfferForm(){
        Intent intent = new Intent(this, OfferFormActivity.class);
        startActivity(intent);
    }


    private void saveRemoteOffersOnLocal(){
        OfferRepository offerRepository = new OfferRepository(this);
        offerRepository.getOffers(new OfferRepository.ApiCallback(){

            @Override
            public void onSuccess(ApiResponse response) {
                if(response.isStatus()){
                    List<RequestOffer> offers = (List<RequestOffer>) response.getData();
                    OfferDao.saveRemoteOffersOnLocal(offers);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(OfferActivity.this);
                builder.setMessage(response.getMessage())
                        .setTitle("Succès")
                        .setPositiveButton("OK", (dialog, id) -> {
                            dialog.dismiss();
                        });
            }
            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
}