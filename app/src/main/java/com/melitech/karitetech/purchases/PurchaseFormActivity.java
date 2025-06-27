package com.melitech.karitetech.purchases;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.melitech.karitetech.R;

import com.melitech.karitetech.data.local.PurchaseDao;
import com.melitech.karitetech.model.Purchase;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.repository.PurchaseRepository;
import com.melitech.karitetech.repository.BaseRepository;
import com.melitech.karitetech.utils.AmountFormatter;
import com.melitech.karitetech.utils.TextFormatter;
import com.melitech.karitetech.viewmodels.NetworkViewModel;
import com.melitech.karitetech.viewmodels.PurchaseViewModel;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;


public class PurchaseFormActivity extends AppCompatActivity {
    PurchaseDao achatDao;
    EditText price, weight, mixteAmount;
    Spinner qualitySpinner, paymentSpinner;
    boolean isMixte;
    TextView totalAmount;
    String especeAmount;
    String totalAmountValue;
    PurchaseRepository achatRepository;
    PurchaseFormActivity PurchaseFormActivity;

    Button btnSubmit;
    private boolean isConnected = true;
    Purchase newPurchase;
    PurchaseViewModel purchaseFormViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achat_form);
        achatDao = new PurchaseDao(this);
        purchaseFormViewModel = new ViewModelProvider(this).get(PurchaseViewModel.class);

        Toolbar toolbar = findViewById(R.id.myAppbar);
        btnSubmit = findViewById(R.id.btnSaveSold);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Effectuer un achat");
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
        qualitySpinner = findViewById(R.id.qualitySpinner);
        paymentSpinner = findViewById(R.id.paymentSpinner);
        mixteAmount = findViewById(R.id.mixteAmount);
        totalAmount = findViewById(R.id.totalAmount);
        isMixte = false;
        achatRepository = new PurchaseRepository(this);
        wathcesChange();
        getQualities();
        getPaymentMethods();
        formatEditText(price);
        formatEditText(mixteAmount);
        newPurchase = new Purchase();

        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                bottomsheetView();
            }
        });
    }


    public void getQualities() {
        hideKeyboard();
        String[] options = {"Sélectionner une qualité", "Bonne", "Moyenne", "Mauvaise"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qualitySpinner.setAdapter(adapter);
    }

    public void getPaymentMethods() {
        hideKeyboard();
        String[] options = {"Sélectionner un moyen de paiment", "Espèce", "Mixte", "Électronique", "Dépôt vente"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(adapter);
        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();

                if (selectedOption.equals("Mixte")) {
                    mixteAmount.setVisibility(View.VISIBLE);
                    isMixte = true;
                } else {
                    mixteAmount.setVisibility(View.GONE); // Cache le champ
                    isMixte = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mixteAmount.setVisibility(View.GONE);
            }
        });
    }


    public boolean validateForm() {
        String weight = this.weight.getText().toString();
        String price = this.price.getText().toString();
        if (weight.isEmpty()) {
            Toast.makeText(this, "Veuillez renseigner la quandité.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (price.isEmpty()) {
            Toast.makeText(this, "Veuillez renseigner le prix.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (paymentSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Veuillez sélectionner un moyen de paiement.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (qualitySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Veuillez sélectionner une qualité.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void saveOffer() {
        hideKeyboard();
       purchaseFormViewModel.addPurchase(newPurchase);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Purchase ajouté avec succès")
                .setPositiveButton("OK", (dialog, id) -> {
                   Intent intent = new Intent(PurchaseFormActivity.this, PurchaseActivity.class);
                   startActivity(intent);
                   finish();
                });
        builder.setTitle("Ajout");
        AlertDialog dialog = builder.create();
        dialog.show();
    }


  private void getDataToSave(){
        Farmer farmer = getIntent().getParcelableExtra("farmer");
        String weight = this.weight.getText().toString();
        String price = this.price.getText().toString();
        String quality = this.qualitySpinner.getSelectedItem().toString();
        String momo_amount =calculateMomoAmount(mixteAmount.getText().toString(), this.totalAmount.getText().toString(), isMixte);
        Purchase purchase = new Purchase();
        purchase.setAmount(momo_amount);
        purchase.setQuality(quality);
        purchase.setPhonePayment(TextFormatter.removeSpaces(farmer.getPhone()));
        purchase.setPrice(AmountFormatter.cleanCurrencyValue(price));
        purchase.setWeight(weight);
        purchase.setPaymentMethod(paymentSpinner.getSelectedItem().toString());
        purchase.setCash(AmountFormatter.cleanCurrencyValue(mixteAmount.getText().toString()));
        purchase.setFullname(farmer.getFullname());
        purchase.setPicture(farmer.getPicture());
        purchase.setPhone(TextFormatter.removeSpaces(farmer.getPhone()));
        purchase.setFarmerId(farmer.getRemoteId());
        purchase.setIsMixte(isMixte);
        newPurchase = purchase;
    }

    public void wathcesChange(){
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateResult();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        weight.addTextChangedListener(watcher);
        price.addTextChangedListener(watcher);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressLint("SetTextI18n")
    private void calculateResult() {
        String input1 = price.getText().toString();
        String input2 = weight.getText().toString();
        String cleanValue1 = AmountFormatter.cleanCurrencyValue(input1);
        try {
            if(input1.isEmpty() || input2.isEmpty()){
                totalAmount.setText("Résultat : 0");
                totalAmountValue = "0";
                return;
            }
            double number1 = Double.parseDouble(cleanValue1);
            double number2 = Double.parseDouble(input2);
            double sum = number1 * number2;
            totalAmount.setText(AmountFormatter.formatSansDecimales(sum));
            totalAmountValue = AmountFormatter.formatSansDecimales(sum);
        } catch (NumberFormatException e) {
            totalAmount.setText("Erreur de saisie");
            totalAmountValue = "0";
        }
    }

    private void formatEditText(EditText EditTextMontant){
        EditTextMontant.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    EditTextMontant.removeTextChangedListener(this);

                    String cleanString = s.toString()
                            .replaceAll("[^\\d]", ""); // Supprimer tout sauf les chiffres

                    try {
                        long parsed = Long.parseLong(cleanString);

                        // Format avec espace tous les 3 chiffres
                        NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);
                        String formatted = numberFormat.format(parsed) + " FCFA";

                        current = formatted;
                        EditTextMontant.setText(formatted);
                        EditTextMontant.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        // Erreur de parsing, remettre à vide
                        current = "";
                        EditTextMontant.setText("");
                    }

                    EditTextMontant.addTextChangedListener(this);
                }
            }
        });
    }


    @SuppressLint("SetTextI18n")
    private void bottomsheetView(){

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        Farmer farmer =  getIntent().getParcelableExtra("farmer");
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_confirm_achat, null);
        TextView farmerName = view.findViewById(R.id.farmerFullName);
        TextView  farmerPhone = view.findViewById(R.id.farmerAchatPhone);
        TextView activityFarmer = view.findViewById(R.id.activityFarmer);
        TextView weight = view.findViewById(R.id.achatWeight);
        TextView price = view.findViewById(R.id.price_per_kg);
        TextView quantity = view.findViewById(R.id.quality);
        TextView payment = view.findViewById(R.id.paymentMethod);
        TextView totalAmount = view.findViewById(R.id.amountTotal);
        TextView amountMixte = view.findViewById(R.id.amountMixte);
        TableRow isMixteRow = view.findViewById(R.id.isMixte);
        Button btnBuy = view.findViewById(R.id.btnBuy);
        ProgressBar progressBar = view.findViewById(R.id.purchaseProgressBar);
        if(isMixte){
            isMixteRow.setVisibility(View.VISIBLE);
            amountMixte.setText(this.mixteAmount.getText().toString());
            getCashAmount();
            amountMixte.setText(this.mixteAmount.getText().toString());
        }else {
            isMixteRow.setVisibility(View.GONE);
        }
        if(farmer!=null){
            farmerName.setText(farmer.getFullname());
            farmerPhone.setText(farmer.getPhone());
            activityFarmer.setText(farmer.getJob());
        }
        weight.setText(this.weight.getText().toString()+"Kg");
        price.setText(this.price.getText().toString());
        quantity.setText(this.qualitySpinner.getSelectedItem().toString());
        payment.setText(this.paymentSpinner.getSelectedItem().toString());
        totalAmount.setText(this.totalAmount.getText().toString());
        if(isMixte){
            amountMixte.setText(this.mixteAmount.getText().toString());
        }else {
            amountMixte.setText("0");
        }
        btnBuy.setOnClickListener(v -> {
           NetworkViewModel viewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
            viewModel.getNetworkStatus(this).observe(this, isConnected -> {
                progressBar.setVisibility(View.VISIBLE);
                getDataToSave();
                if (isConnected) {
                    if(farmer.getRemoteId()>0){
                        saveOnremotePurchase();
                        progressBar.setVisibility(View.GONE);
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseFormActivity.this);
                        builder.setMessage("Veuillez synchroniser vos producteurs avant de faire l'achat")
                                .setPositiveButton("OK", (dialog, id) -> {
                                    dialog.dismiss();
                                });
                        builder.setTitle("Ajout");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        progressBar.setVisibility(View.GONE);
                    }
                }else{
                    saveOffer();
                    progressBar.setVisibility(View.GONE);
                }

            });
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public String getCashAmount() {
        try {
            String totalText = AmountFormatter.cleanCurrencyValue(this.totalAmount.getText().toString());
            String mixteText = AmountFormatter.cleanCurrencyValue(this.mixteAmount.getText().toString());

            if (totalText.isEmpty()) totalText = "0";
            if (mixteText.isEmpty()) mixteText = "0";

            double totalAmount = Double.parseDouble(totalText);
            double montantMixte = Double.parseDouble(mixteText);
            double result = totalAmount - montantMixte;
            especeAmount = AmountFormatter.formatSansDecimales(result);
            return AmountFormatter.formatSansDecimales(result);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public void saveOnremotePurchase(){
        achatRepository.makePurchase(newPurchase, new BaseRepository.ApiCallback<Purchase>() {
            @Override
            public void onSuccess(ApiResponse<Purchase> response) {
                if(response.isStatus()){
                   // purchaseFormViewModel.addPurchase(response.getData());
                    AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseFormActivity.this);
                    builder.setTitle("Achat");
                    builder.setMessage("Achat effectué avec succès");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                }else{
                    Toast.makeText(PurchaseFormActivity.this, "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(String errorMessage) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseFormActivity.this);
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


    public static String calculateMomoAmount(String especeAmount, String totalAmountValue, boolean isMixte) {
        double espece = 0;
        double total = 0;

        try {
            espece = especeAmount != null && !especeAmount.isEmpty() ? Double.parseDouble(AmountFormatter.cleanCurrencyValue(especeAmount)) : 0;
            total = totalAmountValue != null && !totalAmountValue.isEmpty() ? Double.parseDouble(AmountFormatter.cleanCurrencyValue(totalAmountValue)) : 0;
        } catch (NumberFormatException e) {
            Log.e("PurchaseFormActivity", "Erreur de conversion", e);
            e.printStackTrace();
        }

        double result = isMixte ? (total - espece) : total;
        return String.valueOf(result);
    }

}


