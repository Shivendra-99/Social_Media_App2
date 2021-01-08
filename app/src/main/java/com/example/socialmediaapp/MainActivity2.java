package com.example.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.socialmediaapp.Dao.DaosData;
import com.example.socialmediaapp.model.user;
import com.firebase.ui.auth.data.remote.FacebookSignInHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.annotations.NotNull;

import static com.example.socialmediaapp.R.string.default_web_client_id;
public class MainActivity2 extends AppCompatActivity {
    FirebaseAuth mfirebaseAuth;
    final static int RC_SIGN_IN=123;
    SignInButton signInButton;
    String ur;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        signInButton=(SignInButton) findViewById(R.id.sign_button);
        mfirebaseAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account=task.getResult(ApiException.class);
            firebaseAuthwithgoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w("Firebase Login failed",e);
        }
    }
    @Override
    public void onStart()
    {
     super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mfirebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void firebaseAuthwithgoogle(String idToken) {
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        //   signInButton.setEnabled(false);
        mfirebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity2.this,"Sign is completed",Toast.LENGTH_LONG).show();
                    FirebaseUser user=mfirebaseAuth.getCurrentUser();
                    updateUI(user);
                }
                else
                {
                    Log.w("tag", "signInWithCredential:failure", task.getException());

                    Toast.makeText(MainActivity2.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }
    private void updateUI(@NotNull FirebaseUser currentUser) {
        if(currentUser!=null) {
            user user1=new user(currentUser.getUid(),currentUser.getDisplayName(),currentUser.getPhotoUrl().toString());
            DaosData daosData=new DaosData();
            daosData.addUser(user1);
            Intent b = new Intent(MainActivity2.this, MainActivity.class);
            b.putExtra("username",currentUser);
            startActivity(b);
            finish();
        }
        else
        {

        }
    }
}