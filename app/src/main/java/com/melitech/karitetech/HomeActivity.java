package com.melitech.karitetech;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.melitech.karitetech.purchases.PurchaseActivity;
import com.melitech.karitetech.purchases.SearchActivity;
import com.melitech.karitetech.data.local.PurchaseDao;
import com.melitech.karitetech.data.local.CertificationDao;
import com.melitech.karitetech.data.local.FarmerDao;
import com.melitech.karitetech.data.local.OfferDao;
import com.melitech.karitetech.data.local.PackagingDao;
import com.melitech.karitetech.data.local.ParcDao;
import com.melitech.karitetech.data.local.ScellerDao;
import com.melitech.karitetech.data.local.UserDao;
import com.melitech.karitetech.farmers.FarmerActivity;
import com.melitech.karitetech.farmers.FarmerFormActivity;
import com.melitech.karitetech.model.User;
import com.melitech.karitetech.offers.OfferActivity;
import com.melitech.karitetech.offers.OfferFormActivity;
import com.melitech.karitetech.parcs.ParcActivity;
import com.melitech.karitetech.parcs.ParcFormActivity;
import com.melitech.karitetech.ui.fragments.AddBtnFragment;
import com.melitech.karitetech.ui.fragments.MenuFragment;
import com.melitech.karitetech.utils.SyncWorker;
import com.melitech.karitetech.viewmodels.FarmerViewModel;
import com.melitech.karitetech.viewmodels.HomeViewModel;
import com.melitech.karitetech.viewmodels.NetworkViewModel;

public class HomeActivity extends AppCompatActivity  {
        UserDao userDao;
        FarmerDao farmerDao;
        CertificationDao certificationDao;
        PackagingDao packagingDao;
        OfferDao offerDao;
        PurchaseDao achatDao;
        ParcDao parcDao;
        ScellerDao scellerDao;
        TextView fullname,coopName,networkState,logoutBtn;
        private BroadcastReceiver localReceiver;
        private int notSyncFarmerCount;
        private int notSynOfferCount;
        private int notSyncAchatCount;
        private int notSyncParcCount;
        Button btnSync;
        private HomeViewModel homeViewModel;
        private FarmerViewModel farmerViewModel;
        private NetworkViewModel networkViewModel;




    FragmentManager fragmentManager = getSupportFragmentManager();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        certificationDao = new CertificationDao(this);
        packagingDao = new PackagingDao(this);
        farmerDao = new FarmerDao(this);
        offerDao = new OfferDao(this);
        achatDao = new PurchaseDao(this);
        parcDao = new ParcDao(this);
        scellerDao = new ScellerDao(this);
        userDao = new UserDao(this);
        btnSync = findViewById(R.id.syncBtn);
        networkViewModel  = new ViewModelProvider(this).get(NetworkViewModel.class);
        enqueueSyncWorker(this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        SynchronizeToRemote synchronizeData = new SynchronizeToRemote(this,homeViewModel);
        homeViewModel.refreshAllCounts();
        updateNetworkUI();
        homeViewModel.getFarmerCount().observe(this, new androidx.lifecycle.Observer<Integer>() {
            @Override
            public void onChanged(Integer farmerCount) {
                notSyncFarmerCount = farmerCount;
                maybeLoadMenuFragments();
            }
        });
        homeViewModel.getOfferCount().observe(this, new androidx.lifecycle.Observer<Integer>() {
            @Override
            public void onChanged(Integer offerCount) {
                notSynOfferCount = offerCount;
                maybeLoadMenuFragments();
            }
        });
        homeViewModel.getAchatCount().observe(this, new androidx.lifecycle.Observer<Integer>() {
            @Override
            public void onChanged(Integer achatCount) {
                notSyncAchatCount = achatCount;
                maybeLoadMenuFragments();
            }
        });
        homeViewModel.getParcCount().observe(this, new androidx.lifecycle.Observer<Integer>() {
            @Override
            public void onChanged(Integer parcCount) {
                notSyncParcCount = parcCount;
                maybeLoadMenuFragments();
            }
        });

        updateSyncButtonState();

        userDao = new UserDao(this);
        fullname = findViewById(R.id.userName);
        networkState = findViewById(R.id.networkState);
        logoutBtn = findViewById(R.id.logoutBtn);
        loadAddBtnFragment();
        getConnectedUser();
        synchronizeData.saveRemoteFarmersOnLocal();
        btnSync.setOnClickListener(v -> {
            synchronizeData.startSync();
        });
    }

    private void updateNetworkUI() {
        networkViewModel.getNetworkStatus(this).observe(this, isConnected -> {
            int iconRes = isConnected ? R.drawable.online : R.drawable.offline;
            networkState.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
            networkState.setText(isConnected ? "Online" : "Offline");
            btnSync.setVisibility(isConnected ? View.VISIBLE : View.GONE);
           logoutBtn.setVisibility(isConnected ? View.VISIBLE : View.GONE);
            getIconColor(isConnected);
            updateSyncButtonState();
        });
    }


    private void updateSyncButtonState() {
        boolean enabled = hasDataToSync();
        btnSync.setEnabled(enabled);
        btnSync.setAlpha(enabled ? 1.0f : 0.5f);
    }


    private boolean hasDataToSync() {
        return notSyncFarmerCount > 0 || notSynOfferCount > 0
                || notSyncAchatCount > 0 || notSyncParcCount > 0;
    }


    private void enqueueSyncWorker(Context context) {
        OneTimeWorkRequest syncWork = new OneTimeWorkRequest.Builder(SyncWorker.class).build();
        WorkManager.getInstance(context).enqueue(syncWork);
    }

    @Override
    protected void onResume() {
        super.onResume();
        homeViewModel.refreshAllCounts();
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver, new IntentFilter("SYNC_COMPLETED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
    }

    public void getConnectedUser() {
        User user = UserDao.getUser();
        if (user != null) {
           fullname.setText(user.getUsername());
        } else {
            Log.e("HomeActivity", "User not found");
        }
    }

    private void getIconColor(boolean isConnected){
        Drawable icon = ContextCompat.getDrawable(this, isConnected ? R.drawable.online : R.drawable.offline);
        if (icon != null) {
            icon = DrawableCompat.wrap(icon);
            DrawableCompat.setTint(icon, ContextCompat.getColor(this,isConnected ?R.color.brown_color:R.color.red_color)); // ou une autre couleur
            networkState.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        }
    }



    public void loadMenuFragments(int farmerCount, int offerCount, int achatCount, int parcCount) {
        int greenColor = ContextCompat.getColor(this, R.color.green_color_1);
        int yelow = ContextCompat.getColor(this, R.color.yellow_color_2);
        int brown = ContextCompat.getColor(this, R.color.brown_color_op_50);
        int orangeColor = ContextCompat.getColor(this, R.color.orange_color);
        int greenTextColor = ContextCompat.getColor(this, R.color.green_color);

        MenuFragment farmerMenu = MenuFragment.newInstance(
                "Producteurs\n", R.drawable.growth, greenColor, greenTextColor, farmerCount);
        farmerMenu.setTargetActivity(FarmerActivity.class);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.farmersFragmentContainer, farmerMenu)
                .commit();

        MenuFragment offerMenu = MenuFragment.newInstance(
                "Offres de produits\n", R.drawable.offers, yelow, greenTextColor, offerCount);
        offerMenu.setTargetActivity(OfferActivity.class);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.offersFragmentContainer, offerMenu)
                .commit();

        MenuFragment achatMenu = MenuFragment.newInstance(
                "Achat d'amandes\n", R.drawable.trade, brown, R.color.brown_color, achatCount);
        achatMenu.setTargetActivity(PurchaseActivity.class);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.achatDamandeFragmentContainer, achatMenu)
                .commit();

        MenuFragment parcMenu = MenuFragment.newInstance(
                "Parcs à bois\n", R.drawable.fields, orangeColor, R.color.brown_color,parcCount );
        parcMenu.setTargetActivity(ParcActivity.class);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.parcAboisFragmentContainer, parcMenu)
                .commit();
    }


    private void maybeLoadMenuFragments() {
        if(notSyncAchatCount >= 0 || notSyncFarmerCount >= 0 || notSynOfferCount >= 0 || notSyncParcCount >= 0){
            loadMenuFragments(notSyncFarmerCount, notSynOfferCount, notSyncAchatCount, notSyncParcCount);
        }
    }



    public void loadAddBtnFragment(){
       AddBtnFragment ajouterProdFragment = AddBtnFragment.newInstance(
               "Ajouter un producteur",
               R.drawable.ic_person,
               new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(HomeActivity.this, FarmerFormActivity.class);
                       startActivity(intent);
                   }
               });

       getSupportFragmentManager()
               .beginTransaction()
               .add(R.id.addFarmerBtn, ajouterProdFragment)
               .commit();

       AddBtnFragment ajouterOfferFragment = AddBtnFragment.newInstance(
               "Ajouter une offre",
               R.drawable.ic_offer,
               new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                      Intent intent = new Intent(HomeActivity.this, OfferFormActivity.class);
                      startActivity(intent);
                   }
               });

       getSupportFragmentManager()
               .beginTransaction()
               .add(R.id.addOfferBtn, ajouterOfferFragment)
               .commit();


       AddBtnFragment ajouterAchatragment = AddBtnFragment.newInstance(
               "Effectuer un achat",
               R.drawable.ic_search_farmer_24,
               new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                       startActivity(intent);
                   }
               });

       getSupportFragmentManager()
               .beginTransaction()
               .add(R.id.addAchatDemandeBtn, ajouterAchatragment)
               .commit();

       AddBtnFragment ajouterParcfragment = AddBtnFragment.newInstance(
               "Ajouter un parc à bois",
               R.drawable.parc_a_bois,
               new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(HomeActivity.this, ParcFormActivity.class);
                       startActivity(intent);}
               });

       getSupportFragmentManager()
               .beginTransaction()
               .add(R.id.addBtnParcAbois, ajouterParcfragment)
               .commit();
   }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION_TAG", "Permission accordée");
            } else {
                // Permission refusée
                Toast.makeText(this, "Permission de notification refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra("success", false);
            if (success) {
                notSyncFarmerCount = FarmerDao.getLocalFarmers().size();
            }
        }
    };







}

