package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmediaapp.Dao.PostDao;
import com.example.socialmediaapp.model.PostModel;
import com.example.socialmediaapp.model.user;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
public class MainActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    RecylerAdopter recylerAdopter;
    boolean dark=true;
    PostDao mpost=new PostDao();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent b=new Intent();
        String username=b.getStringExtra("username");
        recyclerView=(RecyclerView)findViewById(R.id.cycle);
        floatingActionButton=(FloatingActionButton) findViewById(R.id.floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b=new Intent(MainActivity.this,PostActivity.class);
                startActivity(b);
            }
        });
        updateRecycle();
    }
    private void updateRecycle() {
        CollectionReference collectionReference=mpost.getCollectionReference();
        Query query=collectionReference.orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostModel> options=new FirestoreRecyclerOptions.Builder<PostModel>().setQuery(query,PostModel.class).build();
        recylerAdopter=new RecylerAdopter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recylerAdopter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are sure to Delete this post");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recylerAdopter.deleteItem(viewHolder.getAdapterPosition());
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         Toast.makeText(MainActivity.this,"You Clicked No",Toast.LENGTH_LONG).show();
                       recyclerView.setAdapter(recylerAdopter);
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        }).attachToRecyclerView(recyclerView);
        recylerAdopter.setOnitemClickListener(new RecylerAdopter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                mpost.updateLikes(documentSnapshot.getId());
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        recylerAdopter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        recylerAdopter.stopListening();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater k=getMenuInflater();
        k.inflate(R.menu.sign_out,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.sign_ button:
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Do you want to sign out");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,"You Clicked No",Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                return true;
            case R.id.drak_mode:
                if(dark) {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    dark=false;
                    Toast.makeText(this,"Dark Mode Turn ON",Toast.LENGTH_LONG).show();
                }
                else
                {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    dark=true;
                    Toast.makeText(this,"Dark Mode Turn OFF",Toast.LENGTH_LONG).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}