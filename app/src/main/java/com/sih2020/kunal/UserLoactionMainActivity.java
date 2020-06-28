package com.sih2020.kunal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserLoactionMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    FirebaseAuth auth;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLng;
    FirebaseUser user;
    String current_user_name;
    String current_user_email;
    String current_user_imageUrl;
    TextView t1_currentName, t2_currentEmail;
    ImageView i1;
    int markerCount = 0;
    String newUser;
    String lat, lng;
    String code;


    private GoogleMap nMap;
    DatabaseReference databaseReference, databaseReferenceLoc, databaseReferenceAdd, db;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.Open, R.string.Close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/

        View header = navigationView.getHeaderView(0);
        t1_currentName = header.findViewById(R.id.title_text);
        t2_currentEmail = header.findViewById(R.id.email_text);
        i1 = header.findViewById(R.id.imageView);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current_user_name = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                current_user_email = dataSnapshot.child(user.getUid()).child("email").getValue(String.class);
                current_user_imageUrl = dataSnapshot.child(user.getUid()).child("imageUrl").getValue(String.class);
                code = dataSnapshot.child(user.getUid()).child("code").getValue(String.class);
                t1_currentName.setText(current_user_name);
                t2_currentEmail.setText(current_user_email);

                Picasso.get().load(current_user_imageUrl).into(i1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReferenceAdd = databaseReference.child(user.getUid()).child("CircleMembers");

        databaseReferenceAdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                try {

                    CircleJoin circleJoin = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        circleJoin = snapshot.getValue(CircleJoin.class);
                        newUser = circleJoin.circlememberid;
                        db = FirebaseDatabase.getInstance().getReference().child("Users").child(newUser);
                        db.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                lat = dataSnapshot1.child("lat").getValue(String.class);
                                lng = dataSnapshot1.child("lng").getValue(String.class);
                                String name = dataSnapshot1.child("name").getValue(String.class);

                                Double latitude = Double.parseDouble(lat);
                                Double longitude = Double.parseDouble(lng);
                                //Toast.makeText(UserLoactionMainActivity.this, "" + name, Toast.LENGTH_SHORT).show();
                                LatLng latLng = new LatLng(latitude, longitude);

                                nMap.addMarker(new MarkerOptions()
                                        .flat(true)
                                        .position(latLng)
                                        .title(name)
                                );
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_location_main, menu);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        /*LatLng latLng = new LatLng
        latLng = new LatLng(location.getLatitude(),location.getLongitude());
        nMap.animateCamera(CameraUpdateFactory.newLatLng(latLng,5));*/
        nMap.setMaxZoomPreference(20);
        //nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "As of now nothing!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_signOut) {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                auth.signOut();
                Intent intent = new Intent(UserLoactionMainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        } else if (id == R.id.nav_inviteMembers) {

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT,"My code is : "+ code + ". Please connect in my circle.");
            startActivity(i.createChooser(i,"Share using :"));

        }
        else if (id == R.id.nav_joinCircle) {
            Intent myIntent = new Intent(UserLoactionMainActivity.this,JonCircleActivity.class);
            startActivity(myIntent);

        }
        else if (id == R.id.nav_joinedCircle) {

        }
        else if (id == R.id.nav_shareLocation) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT,"My location is : "+"http://www.google.com/maps/@0"+latLng.latitude+","+latLng.longitude+",17s");
            startActivity(i.createChooser(i,"Share using :"));
        }
        else if (id == R.id.nav_myCircle) {
            if(FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("CircleMembers") == null){
                Toast.makeText(this, "Please Join First", Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(this, "Cool", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(UserLoactionMainActivity.this,MyCircleActivity.class);
                startActivity(myIntent);

            }


        }

        return false;
        }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(client,request,this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Marker marker = null;

        if(location == null)
        {
            Toast.makeText(getApplicationContext(),"Could not get Location",Toast.LENGTH_SHORT).show();
        }
        else {
            latLng = new LatLng(location.getLatitude(),location.getLongitude());
            databaseReferenceLoc = databaseReference.child(user.getUid());
            String lt = Double.toString(location.getLatitude());
            String lg = Double.toString(location.getLongitude());

            databaseReferenceLoc.child("lat").setValue(lt);
            databaseReferenceLoc.child("lng").setValue(lg);

            //nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));

            if(markerCount > 0){
                nMap.clear();
                if(marker!=null){
                  marker.remove();
                }
            }

            marker = nMap.addMarker(new MarkerOptions().position(latLng).title("Your position"));
            //loadMarkererIcon(marker);
            markerCount++;
        }
    }

    private void loadMarkererIcon(final Marker marker) {

        Glide.with(this).load("http://www.myiconfinder.com/uploads/iconsets/256-256-a5485b563efc4511e0cd8bd04ad0fe9e.png")
                .asBitmap().fitCenter().into(new SimpleTarget<Bitmap>(){

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(resource);
                marker.setIcon(icon);
            }
        });

    }


}




