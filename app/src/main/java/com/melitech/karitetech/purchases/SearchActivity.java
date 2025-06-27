package com.melitech.karitetech.purchases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.melitech.karitetech.R;
import com.melitech.karitetech.data.local.FarmerDao;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.utils.ImageUtils;
import com.melitech.karitetech.utils.TextFormatter;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    EditText searchFarmer;
    FarmerDao farmerDao;
    Button btnSearch;
    ImageView farmerPhoto;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        btnSearch = findViewById(R.id.searchFarmerBtn);
        searchFarmer = findViewById(R.id.searchFarmer);
        farmerDao = new FarmerDao(this);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.brown_color));
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Effectuer une recherche");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        PhoneFormatter();
        btnSearch.setOnClickListener(v -> {
            searchFarmer();
        });
    }

    @SuppressLint("WrongViewCast")
    public void searchFarmer(){
        searchFarmer = findViewById(R.id.searchFarmer);
        String phoneNumber = TextFormatter.removeSpaces(this.searchFarmer.getText().toString());
        Farmer farmer = FarmerDao.getFarmerByPhoneNumber(phoneNumber);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(farmer != null){
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
            Button btnBuy = view.findViewById(R.id.btnBuy);
            Button btnCancel = view.findViewById(R.id.btnCancel);
            TextView farmerName = view.findViewById(R.id.farmerName);
            TextView  farmerPhone = view.findViewById(R.id.farmerPhone);
            TextView activityFarmer = view.findViewById(R.id.activityFarmer);
            ImageView farmerPhoto = view.findViewById(R.id.imageFarmer);
            farmerName.setText(farmer.getFullname());
            farmerPhone.setText(farmer.getPhone());
            activityFarmer.setText(farmer.getJob());
            if(farmer.getPicture() != null){
              ImageUtils.loadImageIfExists(farmer.getPicture(), farmerPhoto, 200, 200);
            }
            btnBuy.setOnClickListener(v -> {
                Intent intent = new Intent(this, PurchaseFormActivity.class);
                intent.putExtra("farmer",farmer);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            });
            btnCancel.setOnClickListener(v -> {
                finish();
            });
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        }else{
            builder.setMessage("Aucun producteur trouvé")
                    .setPositiveButton("OK", (dialog, id) -> {
                        dialog.dismiss();
                    });
            builder.setTitle("Ajout");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    private void PhoneFormatter(){
        searchFarmer.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private int lastStartLocation;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastStartLocation = start;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;

                String original = s.toString().replaceAll("\\s", ""); // Supprime les espaces
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < original.length(); i++) {
                    if (i > 0 && i % 2 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(original.charAt(i));
                }
                searchFarmer.removeTextChangedListener(this);
                searchFarmer.setText(formatted.toString());
                searchFarmer.setSelection(Math.min(formatted.length(), formatted.length()));
                searchFarmer.addTextChangedListener(this);
                isFormatting = false;
                if (formatted.length() == 14) {
                    @SuppressLint("ServiceCast")
                    InputMethodManager imm = (InputMethodManager) searchFarmer.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchFarmer.getWindowToken(), 0);
                    }
                }
            }
        });
    }
}
