package com.sih2020.kunal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText e4_email;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        e4_email = (EditText)findViewById(R.id.editText4);
        dialog = new ProgressDialog(this);

    }

    public void goToPasswordActivity(View v){

        dialog.setMessage("Checking Email Address");
        dialog.show();

        auth.fetchProvidersForEmail(e4_email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                        if(task.isSuccessful()){
                            boolean check = !task.getResult().getProviders().isEmpty();
                            dialog.dismiss();

                            if(!check){

                                Intent myIntent = new Intent(RegisterActivity.this, PasswordActivity.class);
                                myIntent.putExtra("email", e4_email.getText().toString());
                                startActivity(myIntent);
                                finish();

                            }else{
                                Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

    }

}
