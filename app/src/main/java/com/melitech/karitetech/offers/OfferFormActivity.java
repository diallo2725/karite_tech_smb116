package com.melitech.karitetech.offers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.melitech.karitetech.R;
import com.melitech.karitetech.data.local.CertificationDao;
import com.melitech.karitetech.data.local.OfferDao;
import com.melitech.karitetech.data.local.PackagingDao;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Certification;
import com.melitech.karitetech.model.Packaging;
import com.melitech.karitetech.model.RequestOffer;
import com.melitech.karitetech.repository.BaseRepository;
import com.melitech.karitetech.repository.OfferRepository;
import com.melitech.karitetech.viewmodels.NetworkViewModel;
import com.melitech.karitetech.viewmodels.OffreViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class OfferFormActivity extends AppCompatActivity {
    OfferDao offerDao;
    EditText quantity, price, weight;
    Spinner certifcationSpinner,packagingSpinner;
    long certficiaitonId,packagingId;
    OffreViewModel offreViewModel;

    Button btnSubmit;
    RequestOffer offer;
    private boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_form);
        offreViewModel = new ViewModelProvider(this).get(OffreViewModel.class);
        offerDao = new OfferDao(this);
        Toolbar toolbar = findViewById(R.id.myAppbar);
        btnSubmit = findViewById(R.id.btnSaveOffer);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Ajout d'une offre");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.brown_color)); // même couleur que AppBar
        // Optionnel : bouton retour
        weight = findViewById(R.id.weight);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        certifcationSpinner = findViewById(R.id.certificationSpinner);
        packagingSpinner = findViewById(R.id.packagingSpinner);

        offer = getIntent().getParcelableExtra("offer");
        getUpdateData(offer);
        getCertifications();
        getPackaging();

        btnSubmit.setOnClickListener(v -> {
            if (offer == null) {
                if(!validateForm())return;
                hideKeyboard();
                saveOffer();
            } else {
                updateOffer(offer);
            }
        });
   }

    public void getCertifications(){
        String[] options = {};
        List<Certification> certifications = CertificationDao.getCertifications();
        if (!certifications.isEmpty()) {
            options = new String[certifications.size()];
            for (int i = 0; i < certifications.size(); i++) {
                options[i] = certifications.get(i).getName();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        certifcationSpinner.setAdapter(adapter);
        if (offer != null ) {
            Certification certification = CertificationDao.findById(String.valueOf(offer.getCertification_id()));
            int position = Arrays.asList(options).indexOf(certification.getName());
            if (position >= 0) {
                certifcationSpinner.setSelection(position);
                certficiaitonId = offer.getCertification_id();
            }
        }
    }

    public void getPackaging(){
        String[] options = {};
        List<Packaging> packgings = PackagingDao.getPackagings();
        if (!packgings.isEmpty()) {
            options = new String[packgings.size()];
            for (int i = 0; i < packgings.size(); i++) {
                options[i] = packgings.get(i).getName();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        packagingSpinner.setAdapter(adapter);

        if (offer != null ) {
            Packaging packaging = PackagingDao.findById(String.valueOf(offer.getPacking_id()));
            int position = Arrays.asList(options).indexOf(packaging.getName());
            if (position >= 0) {
                packagingSpinner.setSelection(position);
                packagingId = offer.getPacking_id();
            }
        }
    }

    public boolean validateForm(){
        String weight = this.weight.getText().toString();
        String price = this.price.getText().toString();
        String quantity = this.quantity.getText().toString();

        if (weight.isEmpty() ){
            Toast.makeText(this, "Veuillez renseigner la quandité.", Toast.LENGTH_SHORT).show();
            return false;
        }else if( price.isEmpty()){
            Toast.makeText(this, "Veuillez renseigner le prix.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if( quantity.isEmpty()){
            Toast.makeText(this, "Veuillez renseigner le nombre de colis.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

   public void saveOffer(){
       NetworkViewModel viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
       String weight = this.weight.getText().toString();
       String price = this.price.getText().toString();
       String quantity = this.quantity.getText().toString();
       String certification = this.certifcationSpinner.getSelectedItem().toString();
       String packaging = this.packagingSpinner.getSelectedItem().toString();
       Certification cert = CertificationDao.findCertificaiton(certification);
       Packaging pack = PackagingDao.findPackaging(packaging);
        RequestOffer offer = new RequestOffer();
        offer.setWeight(Double.parseDouble(weight));
        offer.setPrice(Double.parseDouble(price));
        offer.setPackingCount(Double.parseDouble(quantity));
        offer.setCertification_id(cert.getRemoteId());
        offer.setPacking_id(pack.getRemoteId());

       viewModel.getNetworkStatus(this).observe(this, isConnected -> {
           if (isConnected) {
               saveOfferOnRemote(offer);
           }else{
               offreViewModel.addOffer(offer);
               AlertDialog.Builder builder = new AlertDialog.Builder(OfferFormActivity.this);
               builder.setMessage("Offre enregistrée en local")
                       .setPositiveButton("OK", (dialog, id) -> {
                           finish();
                       });
               builder.setTitle("Ajout");
               AlertDialog dialog = builder.create();
               dialog.show();
           }
       });

        }

   public void updateOffer(RequestOffer offer){
       String certification = this.certifcationSpinner.getSelectedItem().toString();
       String packaging = this.packagingSpinner.getSelectedItem().toString();
       Certification cert = CertificationDao.findCertificaiton(certification);
       Packaging pack = PackagingDao.findPackaging(packaging);
            offer.setPackingCount(Double.parseDouble(this.quantity.getText().toString()));
            offer.setPrice(Double.parseDouble(this.price.getText().toString()));
            offer.setWeight(Double.parseDouble(this.weight.getText().toString()));
            offer.setCertification_id(cert.getRemoteId());
            offer.setPacking_id(pack.getRemoteId());
           offreViewModel.updateOffer(offer);
       AlertDialog.Builder builder = new AlertDialog.Builder(OfferFormActivity.this);
           builder.setMessage("Offre enregistrée en local")
                   .setPositiveButton("OK", (dialog, id) -> {
                       finish();
                   });
           builder.setTitle("Modification");
           AlertDialog dialog = builder.create();
           dialog.show();
        }

private void getUpdateData(RequestOffer offer){
          if(offer != null){
              weight.setText(String.valueOf(offer.getWeight()));
              price.setText(String.valueOf(offer.getPrice()));
              quantity.setText(String.valueOf(offer.getPackingCount()));
              certficiaitonId = offer.getCertification_id();
              packagingId = offer.getPacking_id();
          }
   }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void saveOfferOnRemote(RequestOffer offer) {
        OfferRepository offerRepository = new OfferRepository(this);
        offerRepository.createOffer(offer,new BaseRepository.ApiCallback<RequestOffer>() {
            @Override
            public void onSuccess(ApiResponse<RequestOffer> response) {
                if (response.isStatus()) {
                    RequestOffer responseOffer = response.getData();
                    OfferDao.insertLocalOffer(responseOffer);
                    AlertDialog.Builder builder = new AlertDialog.Builder(OfferFormActivity.this);
                    builder.setMessage("Offre enregistrée")
                            .setPositiveButton("OK", (dialog, id) -> {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            });
                    builder.setTitle("Ajout");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OfferFormActivity.this);
                builder.setMessage(errorMessage)
                        .setTitle("Error")
                        .setPositiveButton("OK", (dialog, id) -> {
                            dialog.dismiss();
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        }
    }