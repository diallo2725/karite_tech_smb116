package com.melitech.karitetech.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtils {

    public static Bitmap getOptimizedBitmap(String path, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Matrix matrix = new Matrix();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap; // Pas besoin de rotation
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        } catch (IOException e) {
            e.printStackTrace();
            return bitmap; // En cas d'erreur EXIF, retourner le bitmap normal
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static void loadImageIfExists(String imagePath, ImageView imageView, int reqWidth, int reqHeight) {
        if (imagePath == null) return;
        File file = new File(imagePath);
        if (file.exists()) {
            Bitmap bitmap = getOptimizedBitmap(file.getAbsolutePath(), reqWidth, reqHeight);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e("ImageError", "Échec de décodage de l'image : " + file.getAbsolutePath());
            }
        } else {
            Log.e("PhotoError", "Fichier introuvable : " + file.getAbsolutePath());
        }
    }


    public static MultipartBody.Part compressAndPrepareFile(String key, String filePath) {
        if (filePath == null) return null;

        File file = new File(filePath);
        if (!file.exists()) return null;

        try {
            // Charger l'image dans un Bitmap
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            // Compresser dans un ByteArrayOutputStream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos); // 70% qualité

            byte[] compressedImage = bos.toByteArray();

            // Créer un RequestBody avec l'image compressée
            RequestBody requestFile = RequestBody.create(
                    compressedImage,
                    MediaType.parse("image/jpeg")
            );

            // Retourner MultipartBody.Part
            return MultipartBody.Part.createFormData(key, file.getName(), requestFile);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
