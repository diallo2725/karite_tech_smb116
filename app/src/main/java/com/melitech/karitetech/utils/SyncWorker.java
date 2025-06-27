package com.melitech.karitetech.utils;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


import androidx.core.app.NotificationCompat;
import com.melitech.karitetech.helpers.SettingSyncHelper;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncWorker extends Worker {

    private static final String CHANNEL_ID = "sync_channel";
    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;


    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setForegroundAsync(
                        createForegroundInfo("Synchronisation en cours...")); // createForegroundInfo);
            }
        } catch (Exception e) {
            return Result.failure();
        }
        CountDownLatch latch = new CountDownLatch(3);
        AtomicBoolean hasError = new AtomicBoolean(false);

        SettingSyncHelper settingSyncHelper = new SettingSyncHelper(getApplicationContext());

        settingSyncHelper.syncCertifications(new SettingSyncHelper.SettingCallback() {
            @Override
            public void onSuccess(String message) {
                updateProgress(33);
                completeNotification("Certifications OK");
                latch.countDown();
            }

            @Override
            public void onFailure(String errorMessage) {
                updateProgress(33);
                completeNotification("Erreur Certifications");
                hasError.set(true);
                latch.countDown();
            }
        });

        settingSyncHelper.syncPackaging(new SettingSyncHelper.SettingCallback() {
            @Override
            public void onSuccess(String message) {
                updateProgress(66);
                completeNotification("Packaging OK");
                latch.countDown();
            }

            @Override
            public void onFailure(String errorMessage) {
                updateProgress(66);
                completeNotification("Erreur Packaging");
                hasError.set(true);
                latch.countDown();
            }
        });

        settingSyncHelper.syncScellers(new SettingSyncHelper.SettingCallback() {
            @Override
            public void onSuccess(String message) {
                updateProgress(100);
                completeNotification("Scellers OK");
                latch.countDown();
            }

            @Override
            public void onFailure(String errorMessage) {
                updateProgress(100);
                completeNotification("Erreur Scellers");
                hasError.set(true);
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            return Result.retry();
        }

        return hasError.get() ? Result.retry() : Result.success();
    }




    private void createNotification(String title, String message) {
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Synchronisation",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setProgress(100, 0, false);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void updateProgress(int progress) {
        notificationBuilder.setProgress(100, progress, false)
                .setContentText("Synchronisation : " + progress + "%");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void completeNotification(String syncMessage) {
        notificationBuilder.setContentText(syncMessage)
                .setProgress(0, 0, false); // Enlever la barre de progression
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private ForegroundInfo createForegroundInfo(String message) {
        createNotification("Synchronisation", message);

        return new ForegroundInfo(
                NOTIFICATION_ID,
                notificationBuilder.build(),
                FOREGROUND_SERVICE_TYPE_DATA_SYNC
        );
    }
}



