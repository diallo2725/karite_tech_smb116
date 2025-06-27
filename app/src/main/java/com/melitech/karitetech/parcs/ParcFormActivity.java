package com.melitech.karitetech.parcs;

import static com.melitech.karitetech.utils.ImageUtils.compressAndPrepareFile;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.melitech.karitetech.R;
import com.melitech.karitetech.data.local.ParcDao;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Parc;
import com.melitech.karitetech.repository.ParcRepository;
import com.melitech.karitetech.viewmodels.NetworkViewModel;
import com.melitech.karitetech.viewmodels.ParcViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import android.Manifest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;


public class ParcFormActivity extends AppCompatActivity {
    ParcDao parcDao;
    private MapView map;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    EditText longeur, largeur, longitude, latitude;
    TextView btnGetPosition;
    Spinner qualitySpinner, paymentSpinner;
    boolean isMixte;
    String filePath;


    Button btnSubmit;



    ParcViewModel parcViewModel;
    NetworkViewModel networkViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_parc_form);
        parcDao = new ParcDao(this);
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        networkViewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
        parcViewModel = new ViewModelProvider(this).get(ParcViewModel.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();




        Toolbar toolbar = findViewById(R.id.myAppbar);
        btnSubmit = findViewById(R.id.btnSaveParc);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Enregistrer un parc");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.brown_color)); // même couleur que AppBar
        // Optionnel : bouton retour
        btnGetPosition = findViewById(R.id.btnGetPosition);
        longeur = findViewById(R.id.longeur);
        largeur = findViewById(R.id.largeur);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        isMixte = false;

        btnGetPosition.setOnClickListener(v -> {
            if(!validateForm()) return;
            double largeurConv = Double.parseDouble(this.largeur.getText().toString());
            double longeurConv = Double.parseDouble(this.longeur.getText().toString());
            double longitudeConv = Double.parseDouble(this.longitude.getText().toString());
            double latitudeConv = Double.parseDouble(this.latitude.getText().toString());
            List<GeoPoint> corners = getRectanglePoints(
                    latitudeConv,
                    longitudeConv, largeurConv, longeurConv);
            Polygon polygon = new Polygon();
            polygon.setPoints(corners);
            polygon.setStrokeColor(Color.RED);
            polygon.setFillColor(0x2200FF00);
            map.getOverlays().add(polygon);
            map.invalidate();
        });

        btnSubmit.setOnClickListener(v -> {
            if(!validateForm()) return;
            saveOffer();
            finish();
        });
    }


    public boolean validateForm() {
        if (longeur.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez renseigner la longeur.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (largeur.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez renseigner la largeur.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(longitude.getText().toString().isEmpty()){
            Toast.makeText(this, "Veuillez renseigner la longitude.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(latitude.getText().toString().isEmpty()){
            Toast.makeText(this, "Veuillez renseigner la latitude.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void saveOffer() {
        hideKeyboard();
        takeMapSnapshot(map);
        Parc parc = new Parc();
        parc.setLongitude(longitude.getText().toString());
        parc.setLatitude(latitude.getText().toString());
        parc.setLongeur(longeur.getText().toString());
        parc.setLargeur(largeur.getText().toString());
        parc.setPhoto(filePath);

        networkViewModel.getNetworkStatus(this).observe(this, isConnected -> {
            if (isConnected) {
              boolean isSuccess =  saveParcOnRemote(parc);
                if(isSuccess){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Parc enregistré en local")
                            .setPositiveButton("OK", (dialog, id) -> {
                                finish();
                            });
                    builder.setTitle("Ajout de parc");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Une erreur est survenue lors de l'enregistrement du parc")
                            .setPositiveButton("OK", (dialog, id) -> {
                                finish();
                            });
                    builder.setTitle("Ajout de parc");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }else{
                saveOnLocal(parc);
            }

        });


    }


    private void saveOnLocal(Parc parc) {
        parcViewModel.addParc(parc);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Parc enregistré en local")
                .setPositiveButton("OK", (dialog, id) -> {
                    finish();
                });
        builder.setTitle("Ajout de parc");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                longitude.setText(String.valueOf(location.getLongitude()));
                latitude.setText(String.valueOf(location.getLatitude()));
                showLocationOnMap(location);
            } else {
                Toast.makeText(this, "Impossible de récupérer la position", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLocationOnMap(Location location) {
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        IMapController mapController = map.getController();
        mapController.setZoom(18.0);
        mapController.setCenter(startPoint);
        Marker marker = new Marker(map);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Vous êtes ici");
        map.getOverlays().add(marker);
        map.invalidate();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takeMapSnapshot(MapView mapView) {
        clearMapOverlays(mapView);
        Bitmap bitmap = Bitmap.createBitmap(mapView.getWidth(), mapView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mapView.draw(canvas);  // Capture réelle de la vue

        try {
            File file = new File(getExternalFilesDir(null), "capture_champ.jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            filePath = file.getAbsolutePath();
            Toast.makeText(this, "Capture enregistrée : " + filePath, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<GeoPoint> getRectanglePoints(double lat, double lon, double length, double width) {
        final double R = 6378137; // rayon de la Terre

        double deltaLat = (length / 2) / R * (180 / Math.PI);
        double deltaLon = (width / 2) / (R * Math.cos(Math.toRadians(lat))) * (180 / Math.PI);

        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(lat + deltaLat, lon - deltaLon));
        points.add(new GeoPoint(lat + deltaLat, lon + deltaLon));
        points.add(new GeoPoint(lat - deltaLat, lon + deltaLon));
        points.add(new GeoPoint(lat - deltaLat, lon - deltaLon));
        points.add(new GeoPoint(lat + deltaLat, lon - deltaLon)); // Ferme le polygone

        return points;
    }

    private void clearMapOverlays(MapView mapView) {
        mapView.getOverlays().clear();
        mapView.invalidate();
    }


    public boolean saveParcOnRemote(Parc parc) {
        if (parc == null) return false;
        MultipartBody.Part picturePart = compressAndPrepareFile("picture", filePath);
        ParcRepository parcRepository = new ParcRepository(this);
        parcRepository.addParc(parc, new ParcRepository.AddParcCallback() {
            @Override
            public boolean onSuccess(ApiResponse<Parc> response) {
                if(response.isStatus()){
                   parcViewModel.addParc(parc);
                }
                return response.isStatus();
            }
            @Override
            public boolean onFailure(String message) {
                return false;
            }
        },picturePart,this);
        return true;
    }


}