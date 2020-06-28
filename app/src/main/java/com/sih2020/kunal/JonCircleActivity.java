package com.sih2020.kunal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sih2020.kunal.CreateUser;

public class JonCircleActivity extends AppCompatActivity {

    Pinview pinview;
    DatabaseReference reference,currentReference;
    FirebaseUser user;
    FirebaseAuth auth;
    String current_user_id, join_user_id;
    DatabaseReference circleReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jon_circle);
        pinview = (Pinview)findViewById(R.id.pinview);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        current_user_id = user.getUid();

    }

    public void submitButtonClick(View v){


        // 1)     to check if input code is present or noy in database
        //2)      if code is present find that user and create a node

        Query query = reference.orderByChild("code").equalTo(pinview.getValue());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    CreateUser createUser = null;
                    for (DataSnapshot childDes : dataSnapshot.getChildren())
                    {
                        //CreateUser createUser = new CreateUser();
                        createUser = childDes.getValue(CreateUser.class);
                        join_user_id = createUser.userid;

                        circleReference = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(join_user_id).child("CircleMembers");





                    CircleJoin circleJoin = new CircleJoin(current_user_id);
                    CircleJoin circleJoin1 = new CircleJoin(join_user_id);

                    circleReference.child(user.getUid()).setValue(circleJoin)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"User joined circle successfully",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Circle code is invalid",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
