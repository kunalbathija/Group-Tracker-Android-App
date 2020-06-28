package com.sih2020.kunal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class InviteCodeActivity extends AppCompatActivity {

    String email,password,name,date,isSharing,code,userId;
    Uri imageUri;
    TextView t1;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);


        Intent myIntent = getIntent();
        if(myIntent!=null){

            name = myIntent.getStringExtra("name");
            email = myIntent.getStringExtra("email");
            password = myIntent.getStringExtra("password");
            code = myIntent.getStringExtra("code");
            isSharing = myIntent.getStringExtra("isSharing");
            imageUri = myIntent.getParcelableExtra("imageUri");

        }


        t1 = (TextView)findViewById(R.id.textView);
        t1.setText(code);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("User_Images");


    }

    public void registerUser(View v){

        progressDialog.setMessage("Registering");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            user = FirebaseAuth.getInstance().getCurrentUser();
                            userId = user.getUid();

                            CreateUser createUser = new CreateUser(name, email, password, code, "false", "na", "na", "na",user.getUid());



                            reference.child(userId).setValue(createUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                StorageReference sr = storageReference.child(user.getUid() + ".jpg");
                                                sr.putFile(imageUri)
                                                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    String download_image_path = task.getResult().getDownloadUrl().toString();
                                                                    reference.child(user.getUid()).child("imageUri").setValue(download_image_path)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getApplicationContext(), "Processing", Toast.LENGTH_SHORT).show();
                                                                                        sendVerificationEmail();

                                                                                        Intent myIntent = new Intent(InviteCodeActivity.this, MainActivity.class);
                                                                                        startActivity(myIntent);
                                                                                    } else {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getApplicationContext(), "An error occured while creating account", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Toast.makeText(InviteCodeActivity.this, "Could not register user.", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                        }
                    }
                });


    }
    public void sendVerificationEmail(){
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"Email sent for verfication",Toast.LENGTH_SHORT).show();
                                            finish();
                                            auth.signOut();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Could not send Email",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

    }

}
