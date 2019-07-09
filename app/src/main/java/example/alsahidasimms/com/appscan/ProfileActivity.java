package example.alsahidasimms.com.appscan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {


    //firebase auth obj
    private FirebaseAuth firebaseAuth;
    //view obj
    private TextView textViewUserEmail;
    private Button buttonLogout;

    private DatabaseReference databaseReference;
    private EditText editTextName, editTextAddress;
    private Button buttonSave;

    private Button buttonSelect, buttonUpload;
    private ImageView imageView;
    private static final int PICK_IMAGE_REQUEST =234;

    private Uri filepath;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //initializing firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            //this means use isnt logged in
            //close this activity
            finish();
            //start login activity
            startActivity(new Intent(this,LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextName = (EditText) findViewById(R.id.editTextName);
        buttonSave = (Button) findViewById(R.id.buttonSave);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
       //display logged in user name
        textViewUserEmail.setText("Welcome " + user.getEmail());

        //adding listener to button
        buttonLogout.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference();

        imageView = (ImageView) findViewById(R.id.imageView);
        buttonSelect = (Button) findViewById(R.id.buttonSelect);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);

        buttonUpload.setOnClickListener(this);
        buttonSelect.setOnClickListener(this);
    }

    private void saveUserInformation(){
        String name = editTextName.getText().toString().trim();
        String add = editTextAddress.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name, add);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(TextUtils.isEmpty(name)){
            //email is empty
            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
            //Return will stop function from executing further
            return;
        }

        if(TextUtils.isEmpty(add)){
            //email is empty
            Toast.makeText(this, "Please Enter Your Address", Toast.LENGTH_SHORT).show();
            //Return will stop function from executing further
            return;
        }
        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(this, "Information Saved...",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filepath = data.getData();// uploads file to firebase
            //image view for bitmap object

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showFileChosen(){
        Intent intent = new Intent();
        intent.setType("image/*"); //upload all image types
        intent.setAction(Intent.ACTION_GET_CONTENT); //action
        startActivityForResult(Intent.createChooser(intent, "Select An Image"),PICK_IMAGE_REQUEST);
    }

    private void uploadFile(){

        //if user has selected any file upload file to firebase storage
        if (filepath !=null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference profileRef = storageReference.child("images/profile.jpg");
            //image will be stored with profile.jpg name

            profileRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"File Uploaded",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculate progress of upload // calculate percentage
                            double progress = (100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage(((int) progress) + "% Uploaded...");
                        }
                    });
        }else{
            //if it is null display error toast
        }
    }


    @Override
    public void onClick(View view) {
        //if logout is pressed
        if (view == buttonLogout){
            //signout user
            firebaseAuth.signOut();
            //close activity
            finish();
            //start login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view == buttonSave){
            saveUserInformation();
        }
        if(view == buttonSelect){
            showFileChosen();
        }else if(view == buttonUpload){
            //upload file
            uploadFile();
        }
    }
}
