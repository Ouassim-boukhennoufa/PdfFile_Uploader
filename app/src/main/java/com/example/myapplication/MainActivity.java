package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    TextView tex;
    EditText text;
    ImageButton ad,up; Button sel;
    ProgressDialog progress;
    FirebaseStorage store;
    FirebaseDatabase fire;
    Uri pdfUri;
    String url;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("");
        tex=(TextView)findViewById(R.id.select);
        ad=(ImageButton) findViewById(R.id.add);
        up=(ImageButton) findViewById(R.id.upload);
        sel=(Button) findViewById(R.id.see);
        text=(EditText)findViewById(R.id.check);
        sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    Intent intent=new Intent(MainActivity.this,Upload.class);
                    startActivity(intent);
                }else
                    Toast.makeText(MainActivity.this,"Check your internet connexion",Toast.LENGTH_LONG).show();
            }
        });
        store=FirebaseStorage.getInstance();
        fire=FirebaseDatabase.getInstance();
        ad.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selectPDF();
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    if (pdfUri != null)
                        uploadfile(pdfUri);
                    else
                        Toast.makeText(MainActivity.this, "Please select a file", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(MainActivity.this, "Check your internet connexion", Toast.LENGTH_LONG).show();

            }
        });
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        if(requestCode==9&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectPDF();
        }
        else{
            Toast.makeText(MainActivity.this,"Please provide permission",Toast.LENGTH_LONG).show();
        }
    }

    public void selectPDF(){
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,04);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 04 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            tex.setText("File selected");
        } else {
            Toast.makeText(MainActivity.this, "Please select a file", Toast.LENGTH_LONG).show();
        }
    }
    public void uploadfile(final Uri pdfUri) {
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setTitle("Uploading File");
        progress.setProgress(0);
        progress.setCancelable(false);
        progress.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
        progress.show();
        if (!text.getText().toString().isEmpty()) {
            final String filename=text.getText().toString();
            final String n = text.getText().toString()+".pdf";
            final StorageReference storageReference = store.getReference();
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Uploads");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.child(filename).exists()){
                        Log.d("URRRL",String.valueOf(pdfUri));
                        storageReference.child("Uploads").child(n).putFile(pdfUri)
                           .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   storageReference.child("Uploads").child(n).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                       @Override
                                       public void onSuccess(Uri uri) {
                                           String urll=uri.toString();
                                           Log.d("urrl",urll);
                                           reference.child(filename).setValue(urll);
                                           progress.cancel();
                                           Toast.makeText(MainActivity.this, "File  succesfully uploaded", Toast.LENGTH_LONG).show();

                                       }
                                   });
                               }
                           })
                                .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "File not succesfully uploaded", Toast.LENGTH_LONG).show();
                                progress.dismiss();

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                int current = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progress.setProgress(current);
                            }
                        });
                    }
                    else{
                        Toast.makeText(MainActivity.this, filename + " already exists,change file name", Toast.LENGTH_LONG).show();
                        progress.cancel();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
            text.setError("Field required");
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
