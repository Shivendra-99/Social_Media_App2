package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.socialmediaapp.Dao.PostDao;
import com.example.socialmediaapp.model.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorPickerViewListener;

public class PostActivity extends AppCompatActivity {
    TextInputEditText editText;
    Button button;
    ImageView imageView,getVideo,text_color,text_background_color;
    Uri uri;
    int RC_IMAGE=123;
    int textColor,backgroundColor;
    String url;
    String videoUrl;
    ProgressBar progressBar;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ColorPickerView colorPickerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        PostDao dao=new PostDao();
        storageReference= FirebaseStorage.getInstance().getReference().child("upload");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("upload");
        progressBar=findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        imageView=findViewById(R.id.image_upload);
        editText=findViewById(R.id.EditText1);
        getVideo=findViewById(R.id.videoUpload);
        button=(Button)findViewById(R.id.button);
        colorPickerView=findViewById(R.id.text_color_picker);
        text_color=findViewById(R.id.text_color);
        text_background_color=findViewById(R.id.background_color);
        textColor=0;
        Log.v("TextColor",Integer.toString(textColor));
        backgroundColor=0;
        text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(PostActivity.this)
                        .setTitle("Please Select Text Color")
                        .setPreferenceName("My Color Picker Dialog")
                        .setPositiveButton("Select",new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                              editText.setTextColor(envelope.getColor());
                              textColor=envelope.getColor();
                              Log.v("TextColor",Integer.toString(textColor));
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              dialog.dismiss();
                            }
                        }).attachAlphaSlideBar(true)
                          .attachBrightnessSlideBar(true)
                          .setBottomSpace(12)
                          .show();
          }
        });
        text_background_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(PostActivity.this)
                        .setTitle("Please Select Text Color")
                        .setPreferenceName("My Color Picker Dialog")
                        .setPositiveButton("Select",new ColorEnvelopeListener() {
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                editText.setBackgroundColor(envelope.getColor());
                                backgroundColor=envelope.getColor();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).attachAlphaSlideBar(true)
                        .attachBrightnessSlideBar(true)
                        .setBottomSpace(12)
                        .show();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().isEmpty())
                {
                    Toast.makeText(PostActivity.this,"Please write something Before click post",Toast.LENGTH_LONG).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    button.setClickable(false);
                    dao.post(editText.getText().toString().trim(),url,videoUrl,textColor,backgroundColor);
                    editText.setText("");
                    Intent c=new Intent(PostActivity.this,MainActivity.class);
                    startActivity(c);
                    progressBar.setVisibility(View.INVISIBLE);
                    button.setClickable(true);
                    finish();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent b=new Intent();
               b.setType("image/*");
               b.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(b,RC_IMAGE);
            }
        });
        getVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,2);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.home,menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            uri=data.getData();
            upload();
        }
        if(requestCode==2 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri uri1=data.getData();
            upload1(uri1);
        }
    }
       private void upload1(Uri uri1) {
        StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri1));
        progressBar.setVisibility(View.VISIBLE);
        UploadTask uploadTask = reference.putFile(uri1);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url2 = uri.toString();
                        String uploadId = databaseReference.push().getKey();
                        databaseReference.child(uploadId).setValue(url2);
                        videoUrl=url2;
                        Toast.makeText(PostActivity.this,"Upload Done",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.d("Download Url Fail", e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.Home_from:
                Intent intent=new Intent(PostActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void upload()
    {
        if(uri!=null) {
            StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            UploadTask uploadTask = reference.putFile(uri);
            progressBar.setVisibility(View.VISIBLE);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url1 = uri.toString();
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(url1);
                             url=url1;
                             Toast.makeText(PostActivity.this,"Upload Done",Toast.LENGTH_LONG).show();
                             progressBar.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                            Log.d("Download Url Fail", e.getMessage());
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}