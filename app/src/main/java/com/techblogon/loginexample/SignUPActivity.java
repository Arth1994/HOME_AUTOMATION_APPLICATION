package com.techblogon.loginexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUPActivity extends Activity
{
	EditText editTextUserName,editTextPassword,editTextConfirmPassword,editTextPhoneNumber;
	Button btnCreateAccount;
    EditText editTextEmail;
	LoginDataBaseAdapter loginDataBaseAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		
		// get Instance  of Database Adapter
		loginDataBaseAdapter=new LoginDataBaseAdapter(this);
		loginDataBaseAdapter=loginDataBaseAdapter.open();
		
		// Get Refferences of Views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextUserName=(EditText)findViewById(R.id.editTextUserName);
        editTextPhoneNumber=(EditText)findViewById(R.id.editTextPhoneNumber);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword=(EditText)findViewById(R.id.editTextConfirmPassword);
		
		btnCreateAccount=(Button)findViewById(R.id.buttonCreateAccount);
		btnCreateAccount.setOnClickListener(new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			String email=editTextEmail.getText().toString();
            String userName=editTextUserName.getText().toString();
            String phoneNumber=editTextPhoneNumber.getText().toString();
            String password=editTextPassword.getText().toString();
            String confirmPassword=editTextConfirmPassword.getText().toString();
          
			
			// check if any of the fields are vaccant
            if(userName.equals("")||phoneNumber.equals("")||password.equals("")||confirmPassword.equals(""))
            {
                Toast.makeText(getApplicationContext(), "Empty Fields", Toast.LENGTH_LONG).show();
                return;
            }
            if (!isValidEmail(email)) {
                //editTextEmail.setError("");
                editTextEmail.setError("Invalid Email");
            }
            if (!isValidName(userName)) {
                //editTextEmail.setError("");
                editTextUserName.setError("Invalid Name");
            }
            if(!isValidPhone(phoneNumber))
            {
                //editTextPhoneNumber.setError("");
                editTextPhoneNumber.setError("Invalid Number");
            }
            // check if both password matches
			if(!password.equals(confirmPassword))
			{
                editTextPassword.setError("Passwords not same");
                if(!isValidPassword(password))
                {
                    editTextPassword.setError("Length should be greater than 6.");
                }
            }
			else
			{
			    // Save the Data in Database
			    loginDataBaseAdapter.insertEntry(userName,password,phoneNumber);
			    Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();
			}
		}

            private boolean isValidName(String userName) {

                String name="^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$";
                Pattern pt=Pattern.compile(name);
                Matcher ma=pt.matcher(userName);
                return  ma.matches();


            }

            private boolean isValidPhone(String phoneNumber)
            {
                  String phone="^[0-9]{10}$";
                  Pattern p=Pattern.compile(phone);
                  Matcher m=p.matcher(phoneNumber);
                  return  m.matches();
            }

            private boolean isValidEmail(String email) {
				String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

				Pattern p = Pattern.compile(EMAIL_PATTERN);
				Matcher m = p.matcher(email);
				return m.matches();
			}
		});
}

    private boolean isValidPassword(String password) {

        if (password != null && password.length() > 6) {
            return true;
        }
        return false;
    }

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		loginDataBaseAdapter.close();
	}
}
