package com.melitech.karitetech.helpers;

import android.content.Context;

import com.melitech.karitetech.data.local.CertificationDao;
import com.melitech.karitetech.data.local.PackagingDao;
import com.melitech.karitetech.data.local.ScellerDao;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.Certification;
import com.melitech.karitetech.model.Packaging;
import com.melitech.karitetech.model.Sceller;
import com.melitech.karitetech.repository.SettingRepository;

import java.util.List;

public class SettingSyncHelper {
    private Context context;
    public SettingSyncHelper(Context context) {
        this.context = context;
    }

    public void syncCertifications(SettingCallback callback) {
        SettingRepository settingRepository = new SettingRepository(context);
        settingRepository.getCertifications(new SettingRepository.ApiCallback() {

            public void onSuccess(ApiResponse response) {
                    if(response.isStatus()){
                        CertificationDao.deleteAllCertifications();
                        List<Certification> certifications = (List<Certification>) response.getData();
                        CertificationDao.insertCertification(certifications);
                        callback.onSuccess("Certifications synchronisées.");
                    }
            }
            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure("Erreur de synchronisation des certfications.");
            }
        });
    }

    public void syncPackaging(SettingCallback callback) {
        SettingRepository settingRepository = new SettingRepository(context);
        settingRepository.getPackaging(new SettingRepository.ApiCallback() {

            public void onSuccess(ApiResponse response) {
                    if(response.isStatus()){
                        PackagingDao.deleteAllPackagings();
                        List<Packaging> packagings = (List<Packaging>) response.getData();
                        PackagingDao.insertPackaging(packagings);
                        callback.onSuccess("Packaging synchronisées.");
                    }
            }
            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure("Erreur de synchronisation des packaging.");
            }
        });
    }

    public void syncScellers(SettingCallback callback) {
        SettingRepository settingRepository = new SettingRepository(context);
        settingRepository.getScellers(new SettingRepository.ApiCallback() {

            public void onSuccess(ApiResponse response) {
                    if(response.isStatus()){
                        List<Sceller> scellers = (List<Sceller>) response.getData();
                        ScellerDao.insertSceller(scellers);
                        callback.onSuccess("scellers synchronisés.");
                    }
            }
            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure("Erreur de synchronisation des scellers.");
            }
        });
    }

    public interface SettingCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }


}
