package com.example.bhaumik.spendsmart;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bhaumik.spendsmart.helper.UserDbFormat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


public class Tab2Fragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Tab2Fragment";

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNameField;
    private EditText mContactField;
    private View mProgressView;

    private Button mCreateButton;
    private Button mSignOut;
    private Button mVerifyEmail;
    private View mEmailPassword;
    private View mEmailPasswordField;
    private View mSigningButtons;
    private View mProfilePic;
    private String picturePath;
    private Uri selectedImage;
    private static int RESULT_LOAD_IMAGE = 1;



    private Boolean flag=false;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    /** Activity callback **/
    private ActivityCallback mCallback;
    private BaseActivityCallback mBaseCallback;
    private ImageView imageView;
    //Db Reference
    DatabaseReference userDb;
    String userid;
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout,container,false);
        mEmailField = view.findViewById(R.id.field_email);
        mPasswordField = view.findViewById(R.id.field_password);
        mNameField = view.findViewById(R.id.field_name);
        mContactField = view.findViewById(R.id.field_contact);
        mProgressView = view.findViewById(R.id.create_account_progress);

        // Buttons
        mCreateButton = view.findViewById(R.id.email_create_account_button);
        mCreateButton.setOnClickListener(this);
        mSignOut = view.findViewById(R.id.sign_out_button);
        mSignOut.setOnClickListener(this);
        mVerifyEmail=view.findViewById(R.id.verify_email_button);
        mVerifyEmail.setOnClickListener(this);
        mEmailPassword=view.findViewById(R.id.email_password_buttons);
        mEmailPasswordField = view.findViewById(R.id.email_password_fields);
        mSigningButtons=view.findViewById(R.id.signed_in_buttons);
        mProfilePic=view.findViewById(R.id.profile_pic);
        imageView = view.findViewById(R.id.imgView);



        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            if(mAuth.getCurrentUser().getUid()!=null) {
                userid = mAuth.getCurrentUser().getUid();
            }
        }
        userDb= FirebaseDatabase.getInstance().getReference("users");
        // [END initialize_auth]

        Button buttonLoadImage = view.findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        return view;
    }

    private void setUserInDB(String picturePath)
    {

        Log.d(TAG, "setUserInDB: Finally here "+ picturePath+Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        String user_name=mNameField.getText().toString().trim();
        String user_email=mEmailField.getText().toString().trim();
        String user_contact=mContactField.getText().toString().trim();
        String user_id= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String user_profile = picturePath;

        if(!TextUtils.isEmpty(user_id))
        {
            UserDbFormat udm=new UserDbFormat(user_id ,user_profile,user_name,user_email,user_contact);
            String id=userDb.push().getKey();
            Log.d(TAG, "setUserInDB: "+id);
            userDb.child(user_id).setValue(udm);
        }

        mContactField.setText(null);
        mEmailField.setText(null);
        mNameField.setText(null);
        mPasswordField.setText(null);


    }

    public static Tab2Fragment newInstance() {
        return new Tab2Fragment();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: In here !!!!!!!!!!!!");


        if (EasyPermissions.hasPermissions(getActivity(), galleryPermissions)) {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                selectedImage = data.getData();
                Log.d(TAG, "onActivityResult: After selecting a pic"+selectedImage);
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                ContentResolver resolver = getContext().getContentResolver();
                Cursor cursor = resolver.query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();

                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            }

        } else {
            EasyPermissions.requestPermissions(this, "Access for storage",
                    101, galleryPermissions);
        }



    }

    //User Authentication Functions
    private void createAccount(String email, String password, final String picturePath) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mBaseCallback.showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, picturePath);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null, null);
                        }

                        // [START_EXCLUDE]
                        mBaseCallback.hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]


    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null,null);
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
                            signOut();

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

    public void updateUI(FirebaseUser user, String picturePath) {
        Log.d(TAG, "updateUI: Atleast here it should show up");
        mBaseCallback.hideProgressDialog();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mNameField.getText().toString())
                    .setPhotoUri(selectedImage)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            }
                        }
                    });
            Log.d(TAG, "updateUI: set in DB");
            setUserInDB(picturePath);
            mCallback.changeStatusSuccess();
            mCallback.changeDetailText();

            mEmailPassword.setVisibility(View.GONE);
            mEmailPasswordField.setVisibility(View.GONE);
            mProfilePic.setVisibility(View.GONE);
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
            mProfilePic.setVisibility(View.VISIBLE);
            mSigningButtons.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Hopefully selected image shows up here"+ picturePath);
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString(),picturePath);
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

}