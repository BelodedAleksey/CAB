package beloded.alexey.com.cab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerLoginRegisterActivity extends AppCompatActivity {

    private Button CustomerLoginButton, CustomerRegisterButton;
    private TextView CustomerRegisterLink, CustomerStatus;
    private EditText EmailCustomer, PasswordCustomer;
    private FirebaseAuth mAuth;
    private DatabaseReference CustomerDatabaseRef;
    private String onlineCustomerID;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);
        CustomerLoginButton = findViewById(R.id.customer_login_btn);
        CustomerRegisterButton = findViewById(R.id.customer_register_btn);
        CustomerRegisterLink = findViewById(R.id.register_customer_link);
        CustomerStatus = findViewById(R.id.customer_status);
        EmailCustomer = findViewById(R.id.email_customer);
        PasswordCustomer = findViewById(R.id.password_customer);

        CustomerRegisterButton.setVisibility(View.INVISIBLE);
        CustomerRegisterButton.setEnabled(false);
        CustomerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerLoginButton.setVisibility(View.INVISIBLE);
                CustomerRegisterLink.setVisibility(View.INVISIBLE);
                CustomerStatus.setText("Register Customer");

                CustomerRegisterButton.setVisibility(View.VISIBLE);
                CustomerRegisterButton.setEnabled(true);
            }
        });

        CustomerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();

                RegisterCustomer(email, password);
            }
        });

        CustomerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();

                SignInCustomer(email, password);
            }
        });
    }

    private void SignInCustomer(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please Write Email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please Write Password...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Customer Login");
            loadingBar.setMessage("Please wait, while we are checking your credentials...");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Customer Logged In Successfully...", Toast.LENGTH_SHORT).show();

                                Intent customerIntent = new Intent(CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                                startActivity(customerIntent);
                            } else {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Login Unsuccessful, Please try Again...", Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }

    private void RegisterCustomer(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please Write Email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please Write Password...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Customer Registration");
            loadingBar.setMessage("Please wait, while we are register your data...");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                onlineCustomerID = mAuth.getCurrentUser().getUid();
                                CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child("Customers").child(onlineCustomerID);
                                CustomerDatabaseRef.setValue(true);
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Customer Register Successfully...", Toast.LENGTH_SHORT).show();

                                Intent customerIntent = new Intent(CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                                startActivity(customerIntent);
                            } else {
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Registration Unsuccessful, Please try Again...", Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }
}

