package example.alsahidasimms.com.appscan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //profile activity here
            //before starting another activity, finsih the current
            finish();
            startActivity( new Intent(getApplicationContext(),ProfileActivity.class));
        }
        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonSignup);
        editTextEmail =  (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);

   }
   private void registerUser(){
       String email = editTextEmail.getText().toString().trim();
       String password = editTextPassword.getText().toString().trim();

       if(TextUtils.isEmpty(email)){
           //email is empty
           Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
           //Return will stop function from executing further
           return;
       }
       if(TextUtils.isEmpty(password)){
           //password is empty
           Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
           //Return will stop function from executing further
           return;
       }
       //if credentials are okay
       //display progress bar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                                finish();
                                startActivity( new Intent(getApplicationContext(),ProfileActivity.class));
                            //  Toast.makeText(MainActivity.this, "Registered Successfully",Toast.LENGTH_SHORT).show();

                        }else{
                                Toast.makeText(MainActivity.this, "Registration Unsuccessfully... Please Try Again",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
   }
//https://www.youtube.com/watch?v=tJVBXCNtUuk
    //can do validation on user login section.. if user email !recognised.. TOAST, you do not have an account

    @Override
    public void onClick(View view) {
        if(view == buttonRegister){
            registerUser();
        }
        if (view == textViewSignin){
            //will open login activity here
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
