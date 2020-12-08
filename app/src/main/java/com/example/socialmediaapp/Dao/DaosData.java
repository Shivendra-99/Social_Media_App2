package com.example.socialmediaapp.Dao;

import android.net.wifi.hotspot2.pps.Credential;

import androidx.room.Dao;

import com.example.socialmediaapp.model.user;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.Document;
import com.google.firestore.v1.StructuredQuery;

import java.util.Collections;

import kotlin.collections.UCollectionsKt;

@Dao
public class DaosData {
    FirebaseFirestore db=FirebaseFirestore.getInstance();
   CollectionReference collections=db.collection("user");
    public void addUser(user user)
    {
        collections.document(user.getUserui()).set(user);
    }
    public Task<DocumentSnapshot> getUserbyId(String uid)
    {
        return collections.document(uid).get();
    }
}
