package com.sih2020.kunal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyCircleActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    FirebaseAuth auth;
    FirebaseUser user;
    CreateUser createUser;
    ArrayList<CreateUser> namelist;
    DatabaseReference reference,userReference;
    String circlememberid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_circle);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        namelist = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        if(FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("CircleMembers") == null){
            Toast.makeText(this, "Please Join First", Toast.LENGTH_SHORT).show();
        }else{
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("CircleMembers");
        }


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                namelist.clear();

                if(dataSnapshot.exists()){
                    for (DataSnapshot des : dataSnapshot.getChildren())
                    {
                        circlememberid = des.child("circlememberid").getValue(String.class);

                        userReference.child(circlememberid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        CreateUser createUser = dataSnapshot.getValue(CreateUser.class);
                                        namelist.add(createUser);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                        Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

        adapter = new MembersAdapter(namelist,getApplicationContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
