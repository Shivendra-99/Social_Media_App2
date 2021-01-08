package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
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
import android.widget.Toast;

import com.example.socialmediaapp.Dao.PostDao;
import com.example.socialmediaapp.model.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
public class PostActivity extends AppCompatActivity {
    EditText editText;
    Button button;
    ImageView imageView;
    Uri uri;
    int RC_IMAGE=123;
    String url;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        PostDao dao=new PostDao();
        storageReference= FirebaseStorage.getInstance().getReference().child("upload");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("upload");
        imageView=findViewById(R.id.image_upload);
        editText=(EditText)findViewById(R.id.EditText);
        button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().isEmpty())
                {
                    Toast.makeText(PostActivity.this,"Please write something Before click post",Toast.LENGTH_LONG).show();
                }
                else {

                    dao.post(editText.getText().toString().trim(),url);
                    editText.setText("");
                    Intent c=new Intent(PostActivity.this,MainActivity.class);
                    startActivity(c);
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
                }
            });
        }
    }
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}