package com.example.bhaumik.spendsmart;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Tab1Fragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Tab1Fragment";

    private EditText mEmailField;
    private EditText mPasswordField;
    private View mProgressView;
    private Button mSignIn;
    private Button mSignOut;
    private Button mVerifyEmail;
    private Button mDisconnect;
    private View mEmailPassword;
    private View mEmailPasswordField;
    private View mSigningButtons;

    private Boolean flag=false;


    private FirebaseAuth mAuth;
    /** Activity callback **/
    private ActivityCallback mCallback;
    private BaseActivityCallback mBaseCallback;
    //constants
    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout,container,false);




        mEmailField = view.findViewById(R.id.field_email);
        mPasswordField = view.findViewById(R.id.field_password);

        mProgressView = view.findViewById(R.id.create_account_progress);

        // Buttons
        mSignIn = view.findViewById(R.id.email_sign_in_button);
        mSignIn.setOnClickListener(this);
        mSignOut = view.findViewById(R.id.sign_out_button);
        mSignOut.setOnClickListener(this);
        mVerifyEmail=view.findViewById(R.id.verify_email_button);
        mVerifyEmail.setOnClickListener(this);
        mEmailPassword=view.findViewById(R.id.email_password_buttons);
        mEmailPasswordField = view.findViewById(R.id.email_password_fields);
        mSigningButtons=view.findViewById(R.id.signed_in_buttons);



        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        // [START initialize_auth]
        return view;
    }

    /*
       ----------------------------- Firebase setup ---------------------------------
    */



    //Fragment related Functions
    public static Tab1Fragment newInstance() {
        return new Tab1Fragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (ActivityCallback) context;
        mBaseCallback = (BaseActivityCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        mBaseCallback = null;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mBaseCallback.showProgressDialog();
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                           mCallback.changeStatusText();
                        }
                        mBaseCallback.hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        mVerifyEmail.setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        mVerifyEmail.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getContext(),
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]

                    }
                });
        // [END send_email_verification]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    public void updateUI(FirebaseUser user) {
        mBaseCallback.hideProgressDialog();
        if (user != null) {
            mCallback.changeStatusSuccess();
            mCallback.changeDetailText();

            mEmailPassword.setVisibility(View.GONE);
            mEmailPasswordField.setVisibility(View.GONE);
            mSigningButtons.setVisibility(View.VISIBLE);
            mCallback.googleSignInVisibility();
            //findViewById(R.id.disconnect_button).setVisibility(View.VISIBLE);
            mVerifyEmail.setEnabled(!user.isEmailVerified());
            if(user.isEmailVerified() || flag){
                final Intent intent = new Intent(getActivity(),Dashboard.class);
                startActivity(intent);
            }

        } else {
            mCallback.changeStatusFail();
            mCallback.changeDetailnull();

            mEmailPassword.setVisibility(View.VISIBLE);
            mEmailPasswordField.setVisibility(View.VISIBLE);
            mSigningButtons.setVisibility(View.GONE);
        }
    }





    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_sign_in_button) {
            if(servicesOK()){
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        } else if (i == R.id.sign_out_button) {
            if(flag){
                mCallback.googleSignOut();
                flag = false;
            }
            else{
                signOut();
            }
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        } else if (i == R.id.sign_in_button) {
            flag = true;
            mCallback.googleSignIn();
        } else if (i == R.id.disconnect_button) {
            mCallback.revokeAccess();
        }
    }


    public boolean servicesOK(){
        Log.d(TAG, "servicesOK: Checking Google Services.");

        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if(isAvailable == ConnectionResult.SUCCESS){
            //everything is ok and the user can make mapping requests
            Log.d(TAG, "servicesOK: Play Services is OK");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)){
            //an error occured, but it's resolvable
            Log.d(TAG, "servicesOK: an error occured, but it's resolvable.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), isAvailable, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getContext(), "Can't connect to mapping services", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

}