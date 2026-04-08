package com.savemachine.app.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.savemachine.app.R;
import com.savemachine.app.model.LoginRequest;
import com.savemachine.app.model.LoginResponse;
import com.savemachine.app.model.MessageResponse;
import com.savemachine.app.model.RegisterRequest;
import com.savemachine.app.network.ApiClient;
import com.savemachine.app.ui.machines.MachinesActivity;
import com.savemachine.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnAction;
    private TextView tvToggle, tvToggleLabel;
    private ProgressBar progressBar;
    private SessionManager session;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (session.isLoggedIn()) {
            goToMachines();
            return;
        }

        setContentView(R.layout.activity_login);

        etName       = findViewById(R.id.etName);
        etEmail      = findViewById(R.id.etEmail);
        etPassword   = findViewById(R.id.etPassword);
        btnAction    = findViewById(R.id.btnAction);
        tvToggle     = findViewById(R.id.tvToggle);
        tvToggleLabel = findViewById(R.id.tvToggleLabel);
        progressBar  = findViewById(R.id.progressBar);

        btnAction.setOnClickListener(v -> {
            if (isLoginMode) doLogin();
            else doRegister();
        });

        tvToggle.setOnClickListener(v -> toggleMode());
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            etName.setVisibility(View.GONE);
            btnAction.setText("Entrar");
            tvToggleLabel.setText("Não tem conta?");
            tvToggle.setText("Cadastre-se");
        } else {
            etName.setVisibility(View.VISIBLE);
            btnAction.setText("Cadastrar");
            tvToggleLabel.setText("Já tem conta?");
            tvToggle.setText("Entrar");
        }
    }

    private void doLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        ApiClient.getService().login(new LoginRequest(email, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse body = response.body();
                            session.saveSession(body.token, body.name, body.email);
                            goToMachines();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(LoginActivity.this, "Erro de conexão. Verifique o servidor.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void doRegister() {
        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);
        ApiClient.getService().register(new RegisterRequest(name, email, password))
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Conta criada! Faça login.", Toast.LENGTH_SHORT).show();
                            toggleMode();
                        } else {
                            Toast.makeText(LoginActivity.this, "Erro ao cadastrar. Email já em uso?", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(LoginActivity.this, "Erro de conexão.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMachines() {
        startActivity(new Intent(this, MachinesActivity.class));
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnAction.setEnabled(!loading);
    }
}
