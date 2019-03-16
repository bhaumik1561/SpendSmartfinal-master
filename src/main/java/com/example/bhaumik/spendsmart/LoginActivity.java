package com.example.bhaumik.spendsmart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends BaseActivity implements ActivityCallback,View.OnClickListener {
    private static final String TAG = "EmailPassword";
    private static final int RC_SIGN_IN = 9001;

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private Boolean flag = false;
    Button login, register;

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    FirebaseUser user;

    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        login = (Button) findViewById(R.id.button);
        register = (Button) findViewById(R.id.button2);
        login.setOnClickListener(this);
        register.setOnClickListener(this);


        //TabLayout - Login or Register
        //  mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        // mViewPager = (ViewPager) findViewById(R.id.container);
        // setupViewPager(mViewPager);

        //   TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        // tabLayout.setupWithViewPager(mViewPager);

        //tabLayout.getTabAt(0).setText("Login");
        // tabLayout.getTabAt(1).setText("Register");

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        setupFirebaseAuth();
        // [END initialize_auth]
    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    //check for extras from FCM
                    if (getIntent().getExtras() != null) {
                        Log.d(TAG, "initFCM: found intent extras: " + getIntent().getExtras().toString());
                        for (String key : getIntent().getExtras().keySet()) {
                            Object value = getIntent().getExtras().get(key);
                            Log.d(TAG, "initFCM: Key: " + key + " Value: " + value);
                        }
                        String data = getIntent().getStringExtra("data");
                        Log.d(TAG, "initFCM: data: " + data);
                    }
                    startActivity(intent);
                    finish();


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void changeStatusText() {
        mStatusTextView.setText(R.string.auth_failed);
    }

    @Override
    public void logout() {
        replaceFragment(Tab1Fragment.newInstance());
    }

    /// Private methods

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            flag = true;
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]


    public void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    // [START signin]
    @Override
    public void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        flag = true;
    }
    // [END signin]

    @Override
    public void googleSignOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void googleSignInVisibility() {
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
    }

    @Override
    public void changeStatusSuccess() {
        user = mAuth.getCurrentUser();
        mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                user.getEmail(), user.isEmailVerified()));
    }

    @Override
    public void changeDetailText() {
        mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
    }

    @Override
    public void changeStatusFail() {
        mStatusTextView.setText(R.string.signed_out);
    }

    @Override
    public void changeDetailnull() {
        mDetailTextView.setText(null);
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //findViewById(R.id.disconnect_button).setVisibility(View.VISIBLE);

            Log.d(TAG, "updateUI: Time to go na!!!!");
            if (user.isEmailVerified() || flag) {
                Log.d(TAG, "updateUI: I should go to Dashboard na!!!!");
                final Intent intent = new Intent(this, Dashboard.class);
                startActivity(intent);
            }

        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);
        }
    }

    //TabLayout
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment());
        adapter.addFragment(new Tab2Fragment());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();


        switch (i) {
            case R.id.button:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_layout, new Tab1Fragment())
                        .commit();
                Button button = (Button) v;



                break;


            case R.id.button2:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_layout, new Tab2Fragment())
                        .commit();
                break;


            default:


        }
    }
}
