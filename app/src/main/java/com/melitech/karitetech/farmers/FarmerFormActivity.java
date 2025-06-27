package com.melitech.karitetech.farmers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.melitech.karitetech.R;
import com.melitech.karitetech.data.local.FarmerDao;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Farmer;
import com.melitech.karitetech.repository.FarmerRepository;
import com.melitech.karitetech.utils.DateConverter;
import com.melitech.karitetech.utils.TextFormatter;
import com.melitech.karitetech.viewmodels.FarmerViewModel;
import com.melitech.karitetech.viewmodels.NetworkViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FarmerFormActivity extends AppCompatActivity {
    FarmerDao farmerDao;
    EditText name, lastname, date_of_birth,locality,phone;
    String farmerSexe,farmerActivity;
    Spinner famerActivitySpinner;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView photoFarmer;
    private Uri imageUri;
    File photoFile;
    String currentPath;
    Button takePhoto, btnSubmit;
    Farmer farmer;
    RadioButton male, female;
    private BroadcastReceiver localReceiver;
    FarmerViewModel farmerViewModel;
    NetworkViewModel networkViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_form);
        farmerViewModel = new ViewModelProvider(this).get(FarmerViewModel.class);
        networkViewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, new IntentFilter("NETWORK_STATUS"));
        farmerDao = new FarmerDao(this);
        photoFarmer = findViewById(R.id.farmerPhoto);
        Toolbar toolbar = findViewById(R.id.myAppbar);
        takePhoto = findViewById(R.id.btnTakePhoto);
        btnSubmit = findViewById(R.id.btnsaveFarmer);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Ajout d'un producteur");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.brown_color)); // même couleur que AppBar

        name = findViewById(R.id.name);
        lastname = findViewById(R.id.lastname);
        date_of_birth = findViewById(R.id.date_of_birth);
        locality = findViewById(R.id.locality);
        phone = findViewById(R.id.farmerPhone);
        famerActivitySpinner = findViewById(R.id.farmerActivity);
        male = findViewById(R.id.sexeHomme);
        female = findViewById(R.id.sexeFemme);
        farmer = getIntent().getParcelableExtra("farmer");


        showCalendarDialog();
        getFarmerSexe();
        getFarmerActivity();
        PhoneFormatter();


        takePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);

            } else {
                dispatchTakePictureIntent();
            }
        });
        btnSubmit.setOnClickListener(v -> {
            if (farmer == null && validateForm()) {
                try {
                    saveFarmer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                assert farmer != null;
                updateFarmer(farmer);
            }
        });

        if (farmer != null) {
            name.setText(farmer.getFullname().split(" ")[0]);
            lastname.setText(farmer.getFullname().split(" ")[1]);
            if (farmer.getDate_of_birth() != null) {
                date_of_birth.setText(DateConverter.DayMonthYear(farmer.getDate_of_birth()));
            }
            phone.setText(farmer.getPhone());
            locality.setText(farmer.getLocality());
            if (farmer.getPicture() != null) {
                photoFarmer.setImageURI(Uri.parse(farmer.getPicture()));
            }
            String sex = farmer.getSexe();
            if (sex.equals("M")) {
                male.setChecked(true);
            } else {
                female.setChecked(true);
            }
           farmerActivity = farmer.getJob();
           Objects.requireNonNull(getSupportActionBar()).setTitle("Modification d'un producteur");
       }

   }

   public void showCalendarDialog(){
       date_of_birth.setOnClickListener(v -> {
           final Calendar calendar = Calendar.getInstance();
           int year = calendar.get(Calendar.YEAR);
           int month = calendar.get(Calendar.MONTH);
           int day = calendar.get(Calendar.DAY_OF_MONTH);

           DatePickerDialog datePickerDialog = new DatePickerDialog(
                   this,
                   (view, year1, month1, dayOfMonth) -> {
                       // Formate la date sélectionnée
                       @SuppressLint("DefaultLocale") String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                       date_of_birth.setText(selectedDate);
                   },
                   year, month, day
           );
           datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
           datePickerDialog.show();
       });
   }


   public void  getFarmerSexe(){
       @SuppressLint("WrongViewCast") RadioGroup radioGroupSexe = findViewById(R.id.sexe);
       radioGroupSexe.setOnCheckedChangeListener((group, checkedId) -> {
           if (checkedId == R.id.sexeFemme) {
               farmerSexe = "Femme";
           } else if (checkedId == R.id.sexeHomme) {
               farmerSexe = "Homme";
           }
       });
   }


    public void getFarmerActivity(){
        String[] options = {"Choisir l'activité du producteur", "Transformateur(trice)", "Collecteur(trice)", "Les deux activités"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        famerActivitySpinner.setAdapter(adapter);

        if (farmer != null && farmer.getJob() != null) {
            int position = Arrays.asList(options).indexOf(farmer.getJob());
            if (position >= 0) {
                famerActivitySpinner.setSelection(position);
            }
        }
    }



    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                imageUri = FileProvider.getUriForFile(
                        this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile
                );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Impossible d'ouvrir la caméra", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoFarmer.setImageURI(imageUri);
        }
    }


    private void PhoneFormatter(){
        phone.addTextChangedListener(new TextWatcher() {
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
                phone.removeTextChangedListener(this);
                phone.setText(formatted.toString());
                phone.setSelection(Math.min(formatted.length(), formatted.length()));
                phone.addTextChangedListener(this);
                isFormatting = false;
                if (formatted.length() == 14) {
                    @SuppressLint("ServiceCast")
                    InputMethodManager imm = (InputMethodManager) phone.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(phone.getWindowToken(), 0);
                    }
                }
            }
        });
    }

    public boolean validateForm(){
        String name = this.name.getText().toString();
        String lastname = this.lastname.getText().toString();
        String date_of_birth = this.date_of_birth.getText().toString();
        String phone = this.phone.getText().toString();
        if (name.isEmpty() ){
            Toast.makeText(this, "Veuillez renseigner le nom du producteur.", Toast.LENGTH_SHORT).show();
            return false;
        }else if( lastname.isEmpty()){
            Toast.makeText(this, "Veuillez renseigner les prénoms du producteur.", Toast.LENGTH_SHORT).show();
            return false;
        }else if( date_of_birth.isEmpty()){
            Toast.makeText(this, "Veuillez renseigner la date de naissance du producteur.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(phone.isEmpty()){
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

   public void saveFarmer() throws IOException {
        String phoneNumber = TextFormatter.removeSpaces(this.phone.getText().toString());
        boolean isExist = FarmerDao.checkExistFarmer(phoneNumber);
        if(isExist){
            AlertDialog.Builder builder = new AlertDialog.Builder(FarmerFormActivity.this);
            builder.setMessage("Un producteur existe déjà avec ce numéro de téléphone")
                    .setPositiveButton("OK", (dialog, id) -> {
                        dialog.dismiss();
                    });
            builder.setTitle("Vérification");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String name = this.name.getText().toString();
        String lastname = this.lastname.getText().toString();
        String date_of_birth = this.date_of_birth.getText().toString();
        String phone = this.phone.getText().toString();
        String farmer_activity = this.famerActivitySpinner.getSelectedItem().toString();
        String locality = this.locality.getText().toString();
        Farmer farmer = new Farmer();
        farmer.setFullname(name+" "+lastname);
        farmer.setDate_of_birth(DateConverter.convertDate(date_of_birth));
        farmer.setPhone(TextFormatter.removeSpaces(phone));
        farmer.setJob(farmer_activity);
        farmer.setLocality(locality);
        farmer.setSexe(Objects.equals(farmerSexe, "Homme") ? "M" : "F");
        if (currentPath != null) {
            farmer.setPicture(currentPath);
        }
        handleFarmerSaving(farmer); // handleFarmerSaving
    }


    public void updateFarmer(Farmer farmer){
        String name = this.name.getText().toString();
        String lastname = this.lastname.getText().toString();
        String date_of_birth = this.date_of_birth.getText().toString();
        String phone = this.phone.getText().toString();
        String farmer_activity = this.famerActivitySpinner.getSelectedItem().toString();
        farmer.setFullname(name+" "+lastname);
        if(!date_of_birth.isEmpty()){
            farmer.setDate_of_birth(DateConverter.convertDate(date_of_birth));
        }
        farmer.setPhone(phone.trim());
        farmer.setJob(farmer_activity);
        farmer.setSexe(Objects.equals(farmerSexe, "Homme") ? "M" : "F");
        if (currentPath != null) {
            farmer.setPicture(currentPath);
        }
         farmerViewModel.updateFarmer(farmer);
        Toast.makeText(this, "Informations modifiées avec succès", Toast.LENGTH_SHORT).show();
        finish();
    }


    public boolean saveFarmersOnRemote(Farmer farmer) {
        if (farmer == null) return false;
        MultipartBody.Part picturePart = compressAndPrepareFile("picture", currentPath);
        FarmerRepository farmerRepository = new FarmerRepository(this);
        farmerRepository.addFarmer(farmer, picturePart, new FarmerRepository.AddFarmerCallback() {
            @Override
            public boolean onSuccess(ApiResponse<Farmer> response) {
                if(response.isStatus()){
                   farmerViewModel.addFarmer(response.getData(),true); //
                }
                return response.isStatus();
            }
            @Override
            public boolean onFailure(String message) {
                return false;
            }
        },this);
        return true;
    }
    private MultipartBody.Part compressAndPrepareFile(String key, String filePath) {
        if (filePath == null) return null;
        File file = new File(filePath);
        if (!file.exists()) return null;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos); // 70% qualité
            byte[] compressedImage = bos.toByteArray();
            RequestBody requestFile = RequestBody.create(
                    compressedImage,
                    MediaType.parse("image/jpeg")
            );
            return MultipartBody.Part.createFormData(key, file.getName(), requestFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    private void handleFarmerSaving(Farmer farmer) {

        networkViewModel.getNetworkStatus(this).observe(this, isConnected -> {
            if (isConnected) {
            boolean success = saveFarmersOnRemote(farmer);
            if (success) {
                showSuccessDialog("Producteur enregistré sur le serveur", true);
            } else {
                showErrorDialog("Une erreur s'est produite lors de l'enregistrement du producteur");
            }
        } else {
             farmerViewModel.addFarmer(farmer,false);
            showSuccessDialog("Producteur enregistré en local", true);
        }
        });
    }


    private void showSuccessDialog(String message, boolean notifyUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FarmerFormActivity.this);
        builder.setTitle("Succès");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, id) -> {
            if (notifyUpdate) {
                Intent intent = new Intent("FARMER_UPDATED");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
            setResult(RESULT_OK);
            finish();
        });
        builder.create().show();
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FarmerFormActivity.this);
        builder.setTitle("Erreur");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", (dialog, id) -> finish());
        builder.create().show();
    }

}