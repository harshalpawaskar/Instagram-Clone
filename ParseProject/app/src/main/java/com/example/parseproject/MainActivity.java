package com.example.parseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener{

    EditText userNameEditText;
    EditText passwordEditText;
    Button loginOrSignUpButton;
    ConstraintLayout constraintLayout;
    ImageView logoImageView;
    String username = "",password = "";
    Intent intent;

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if(i==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN)
        {
            if(loginOrSignUpButton.getText().equals("LOGIN"))
                Login();
            else
                SignUp();
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.constraintLayout || view.getId()==R.id.logoImageView)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    public void Login()
    {
        ParseUser.logInInBackground(userNameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user!=null)
                {
                    Toast.makeText(getApplicationContext(),"You have successfully Logged In!!!",Toast.LENGTH_SHORT).show();
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void SignUp()
    {
        ParseUser user = new ParseUser();
        user.setUsername(userNameEditText.getText().toString());
        user.setPassword(passwordEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                {
                    Toast.makeText(getApplicationContext(),"Account Created Successfully!!!",Toast.LENGTH_SHORT).show();
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void LoginOrSignUp(View view)
    {
        if(username.equals("") || password.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Username or Password Field Empty!!!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(loginOrSignUpButton.getText().equals("LOGIN"))
            Login();
        else
            SignUp();
    }

    public void switchText(View view)
    {
        TextView textView = (TextView) view;

        if(textView.getText().equals("or, Login"))
        {
            loginOrSignUpButton.setText("LOGIN");
            textView.setText("or, Sign Up");
        }
        else
        {
            loginOrSignUpButton.setText("SIGN UP");
            textView.setText("or, Login");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Instagram");

        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginOrSignUpButton = (Button) findViewById(R.id.loginOrSignUpButton);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        logoImageView = (ImageView) findViewById(R.id.logoImageView);
        intent = new Intent(getApplicationContext(),UsersList.class);

        logoImageView.setOnClickListener(this);
        constraintLayout.setOnClickListener(this);
        passwordEditText.setOnKeyListener(this);

        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                username = userNameEditText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = passwordEditText.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(ParseUser.getCurrentUser()!=null)
            startActivity(intent);

        /*ParseObject score = ParseObject.create("Score");
        score.put("username","Raj");
        score.put("score",65);
        score.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                    Log.i("Parse Request","Successful");
                else
                    e.printStackTrace();
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");

        query.getInBackground("pECjBDgOWX", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null && object!=null)
                {
                    Log.i("UserName",object.getString("username"));
                    Log.i("Score",Integer.toString(object.getInt("score")));
                }
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");

        query.whereGreaterThanOrEqualTo("score",50);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects.size()>0)
                    for(ParseObject object:objects)
                    {
                        object.put("score",object.getInt("score")+20);
                        object.saveInBackground();
                        Log.i("UserName",object.getString("username"));
                        Log.i("Score",Integer.toString(object.getInt("score")));
                    }
            }
        });

        ParseUser user = new ParseUser();
        user.setUsername("Rick");
        user.setPassword("Password");

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Log.i("Success","Signed Up");
                else
                    e.printStackTrace();
            }
        });

        ParseUser.logInInBackground("Rick", "Password", new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user!=null)
                    Log.i("Success","Logged In");
                else
                    e.printStackTrace();
            }
        });*/

        //ParseUser.logOut();

        if(ParseUser.getCurrentUser()!=null)
            Log.i("Currently Logged In",ParseUser.getCurrentUser().getUsername());
        else
            Log.i("No Luck","No One Logged In");

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

    @Override
    public void onBackPressed() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        super.onBackPressed();
    }
}