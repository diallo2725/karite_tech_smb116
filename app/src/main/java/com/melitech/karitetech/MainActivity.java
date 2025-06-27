package com.melitech.karitetech;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.melitech.karitetech.data.local.CertificationDao;
import com.melitech.karitetech.data.local.PackagingDao;
import com.melitech.karitetech.data.local.ScellerDao;
import com.melitech.karitetech.data.local.UserDao;
import com.melitech.karitetech.model.ApiResponse;
import com.melitech.karitetech.model.RequestLogin;
import com.melitech.karitetech.model.LoginResponse;
import com.melitech.karitetech.model.User;
import com.melitech.karitetech.repository.LoginRepository;
import com.melitech.karitetech.utils.SessionManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity{
    @SuppressLint("ClickableViewAccessibility")
    Button loginButton;
    ProgressBar progressBar;
    UserDao userDao;
    ScellerDao scellerDao;
    PackagingDao packagingDao;
    CertificationDao certificationDao;
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        SessionManager sessionManager = new SessionManager(MainActivity.this);

       /* if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }*/
        userDao = new UserDao(this);
        scellerDao = new ScellerDao(this);
        packagingDao = new PackagingDao(this);
        certificationDao = new CertificationDao(this);
        EditText passwordEditText = findViewById(R.id.editTextText2);
        EditText usernameEditText = findViewById(R.id.editTextText);
         loginButton = findViewById(R.id.btnLogin);
         progressBar = findViewById(R.id.progressBar);
        final boolean[] passwordVisible = {false};
        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight()
                        - passwordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    passwordVisible[0] = !passwordVisible[0];
                    if (passwordVisible[0]) {
                        // Affiche le mot de passe
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_lock, 0, R.drawable.ic_eye_off, 0);
                    } else {
                        // Cache le mot de passe
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_lock, 0, R.drawable.ic_eye_show, 0);
                    }
                    // Remet le curseur à la fin
                    passwordEditText.setSelection(passwordEditText.getText().length());
                    return true; // Consomme l'événement
                }
            }

            return false;
        });
        loginButton.setOnClickListener(this::login);
        passwordEditText.setText("dia123" );
        usernameEditText.setText("a.diallo" );
    }

    public void login(View v) {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        showLoading(true);
        LoginRepository loginRepository = new LoginRepository();
        EditText usernameEditText = findViewById(R.id.editTextText);
        EditText passwordEditText = findViewById(R.id.editTextText2);

        RequestLogin requestLogin = new RequestLogin();
        requestLogin.setUsername(usernameEditText.getText().toString());
        requestLogin.setPassword(passwordEditText.getText().toString());

        loginRepository.login(new LoginRepository.ApiCallback() {
            @Override
            public void onSuccess(ApiResponse<LoginResponse> response) {
                showLoading(true);
                String token = response.getData().getToken();
                SessionManager sessionManager = new SessionManager(MainActivity.this);
                sessionManager.saveToken(token);
                UserDao.deleteUser();
                saveUserInfoLocal(response.getData().getUser());
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(MainActivity.this, "Synchronisation en cours...", Toast.LENGTH_SHORT).show();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }, 1000); // 1 seconde
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    String message = "";
                   Log.e("MainActivity", errorMessage);
                    if(Objects.equals(errorMessage, "Unauthorized")){
                        message = "le nom d'utilisateur ou le mot de passe n'est pas correcte";
                    }else{
                        message = errorMessage;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(message)
                            .setTitle("Error")
                            .setPositiveButton("OK", (dialog, id) -> {
                                dialog.dismiss();
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
            }

        }, requestLogin);
    }


    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            loginButton.setAlpha(0.5f); // effet grisé
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            loginButton.setAlpha(1f);
        }
    }

    private void saveUserInfoLocal(User user) {
        UserDao.insertUser(
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getPhone()
        );
    };


}
