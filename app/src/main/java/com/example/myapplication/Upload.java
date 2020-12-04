package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Upload extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter myAdapter;
    DatabaseReference databaseReference;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().setTitle("");
        recyclerView = (RecyclerView) findViewById(R.id.recyc);
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads");
        if(isNetworkConnected()){
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                String fileName = snapshot.getKey();
                String url = snapshot.getValue(String.class);
                ((MyAdapter) recyclerView.getAdapter()).update(fileName, url);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(Upload.this));
        myAdapter = new MyAdapter(recyclerView, Upload.this, new ArrayList<String>(), new ArrayList<String>());
        recyclerView.setAdapter(myAdapter);
    }
        else
        Toast.makeText(Upload.this,"Check your internt connexion",Toast.LENGTH_LONG).show();
    }

  /*  private void show() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(Upload.this);
        alert.setMessage("Click on file name to download it")
        .setCancelable(true)
        .setPositiveButton("Undo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog aler=alert.create();
        aler.getWindow().setBackgroundDrawableResource(R.color.colorAccent);
        aler.show();
    }*/
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}