package com.funplux.moneywiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText signupEmail, signupPassword, signupConfirmPassword;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById(R.id.signup_confirm_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String confirmPassword = signupConfirmPassword.getText().toString().trim();

                // Validate input
                if (TextUtils.isEmpty(email)) {
                    signupEmail.setError("Email is required");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signupEmail.setError("Please enter a valid email");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    signupPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    signupPassword.setError("Password must be at least 6 characters");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    signupConfirmPassword.setError("Passwords do not match");
                    return;
                }

                // Show loading state
                signupButton.setEnabled(false);
                signupButton.setText("Creating Account...");

                // Create user account
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    // Create user document in Firestore
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", email);
                                    userData.put("createdAt", System.currentTimeMillis());

                                    db.collection("users").document(user.getUid())
                                        .set(userData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, 
                                                        "Account created successfully", 
                                                        Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(SignUpActivity.this, 
                                                        MainActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignUpActivity.this,
                                                        "Error creating user profile: " + 
                                                        task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                }
                            } else {
                                String errorMessage = task.getException() != null ? 
                                    task.getException().getMessage() : "Sign up failed";
                                Toast.makeText(SignUpActivity.this, errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                            }
                            // Reset button state
                            signupButton.setEnabled(true);
                            signupButton.setText("Sign Up");
                        }
                    });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}