package com.sih2020.kunal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

    String email;
    EditText e3_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        e3_password = (EditText)findViewById(R.id.editText3);

        Intent myIntent = getIntent();
        if(myIntent!=null){
            email = myIntent.getStringExtra("email");
        }

    }

    public void goToNamePicActivity(View v){

        if(e3_password.getText().toString().length() > 6){
            Intent myIntent = new Intent(PasswordActivity.this, NameActivity.class);
            myIntent.putExtra("email", email);
            myIntent.putExtra("password", e3_password.getText().toString());
            startActivity(myIntent);
            finish();
        }
        else{
            Toast.makeText(this, "Password length should be more than 6 characters.", Toast.LENGTH_SHORT).show();
        }

    }
}
