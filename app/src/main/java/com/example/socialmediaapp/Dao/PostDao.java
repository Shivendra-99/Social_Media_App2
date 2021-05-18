package com.example.socialmediaapp.Dao;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Dao;

import com.example.socialmediaapp.MainActivity;
import com.example.socialmediaapp.MainActivity2;
import com.example.socialmediaapp.model.PostModel;
import com.example.socialmediaapp.model.user;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

@Dao
public class PostDao {
    PostModel  postModel=new PostModel();
   FirebaseFirestore db=FirebaseFirestore.getInstance();
   CollectionReference collectionReference=db.collection("Posts");
   user u;
    public CollectionReference getCollectionReference() {
        return collectionReference;
    }
    public void post(String text,String upload_image,String upload_video,int text_color,int backgroundColor) {
     DaosData i=new DaosData();
        String authProvider= FirebaseAuth.getInstance().getCurrentUser().getUid();
     Log.d("Auth provider",authProvider);
     if(!authProvider.isEmpty()) {
         final Task<DocumentSnapshot> documentSnapshotTask = i.getUserbyId(authProvider).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 u = documentSnapshot.toObject(user.class);
                 long currentTime = System.currentTimeMillis();
                 PostModel model = new PostModel(text,postModel.getLikes(), u, currentTime,upload_image,upload_video,text_color,backgroundColor);
                 collectionReference.document().set(model);
             }
         });
     }
   }
   public Task<DocumentSnapshot> getPostLiked(String postId)
   {
       return collectionReference.document(postId).get();
   }
   public void updateLikes(String postId)
   {
      String authProvider= FirebaseAuth.getInstance().getCurrentUser().getUid();
             getPostLiked(postId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               PostModel post = documentSnapshot.toObject(PostModel.class);
               if(post.getLikes()!=null) {
                   boolean isliked = post.getLikes().contains(authProvider);
                   Log.d("user updatlike", authProvider);
                   if (isliked) {
                       post.getLikes().remove(authProvider);
                   } else {
                       Log.d("Yes I moved in add", "auth");
                       post.getLikes().add(authProvider);
                   }
                   collectionReference.document(postId).set(post);
               }
               else
               {
                   post.getLikes().add(authProvider);
                   collectionReference.document(postId).set(post);
               }
           }
       });
   }
   public void deletePost(String postId)
   {
       String authProvider= FirebaseAuth.getInstance().getCurrentUser().getUid();
       getCollectionReference().document(postId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
              Log.d("Post is deleted","deleted done");
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Log.d("Post is deleted",e.getMessage());
           }
       });
   }
}