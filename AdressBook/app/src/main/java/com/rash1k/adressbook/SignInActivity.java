package com.rash1k.adressbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rash1k.adressbook.Utils.Utils;
import com.rash1k.adressbook.models.User;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    //    region Widgets
    private static final String TAG = "MainActivity";

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    private EditText mEditTextEmailField;
    private EditText mEditTextPasswordField;
//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sig_in);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mEditTextEmailField = (EditText) findViewById(R.id.field_email);
        mEditTextPasswordField = (EditText) findViewById(R.id.field_password);

        findViewById(R.id.button_sign_in).setOnClickListener(this);
        findViewById(R.id.button_sign_up).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            Log.d(TAG, "onStart: Current User: " + true);
        }
    }

    private void onAuthSuccess(FirebaseUser currentUser) {
        String userName = userNameFromEmail(currentUser.getEmail());

//        Запись пользователя
        writeNewUser(currentUser.getUid(), userName, currentUser.getEmail());

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void writeNewUser(String uid, String userName, String email) {
        User user = new User(userName, email);
        mDatabaseReference.child("users").child(uid).setValue(user);
    }

    private String userNameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(mEditTextEmailField.getText().toString())) {
            mEditTextEmailField.setError("Required");
            result = false;
        }

        if (TextUtils.isEmpty(mEditTextPasswordField.getText().toString())) {
            mEditTextPasswordField.setError("Required");
            result = false;
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        if (!Utils.isNetworkAvailableAndConnected(this)) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.button_sign_in:
                sigIn();
                break;
            case R.id.button_sign_up:
                signUp();
        }
    }


    private void sigIn() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEditTextEmailField.getText().toString();
        String password = mEditTextPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "sigIn: onComplete: " + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "SigIn Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void signUp() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEditTextEmailField.getText().toString();
        String password = mEditTextPasswordField.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "sigUp: onComplete: " + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignInActivity.this, "Sig Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
