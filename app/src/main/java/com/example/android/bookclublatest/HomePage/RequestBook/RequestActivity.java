package com.example.android.bookclublatest.HomePage.RequestBook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookclublatest.Authentication.Login.LoginActivity;
import com.example.android.bookclublatest.HomePage.HomePageActivity;
import com.example.android.bookclublatest.IssueBookDetailActivity;
import com.example.android.bookclublatest.Member.RequestedBooks.RequestBooksActivity;
import com.example.android.bookclublatest.R;
import com.example.android.bookclublatest.SharedPref.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class RequestActivity extends AppCompatActivity implements RequestPageContract.View
{

    @BindView(R.id.request_book_name)
    EditText book;
    @BindView(R.id.request_author_name)
    EditText author;
    @BindView(R.id.request_publication_name)
    EditText publication;
    @BindView(R.id.additional_info)
    EditText additional_info;
    @BindView(R.id.return_home)
    ImageView home;
    @BindView(R.id.textView26)
    TextView title;
    @BindView(R.id.myCoordinatorLayout2)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.request_button)
    Button submit;

    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

    RequestPageContract.Presenter presenter;
    SharedPref sharedPref;
    android.os.Handler Handler = new Handler();
    final static String[] months={"Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_book);
        presenter= new RequestPagePresenter(this);
        ButterKnife.bind(this);
        sharedPref = new SharedPref(this);

        Calendar calendar2=Calendar.getInstance(TimeZone.getDefault());
        final String current=calendar2.get(Calendar.DAY_OF_MONTH) + " " + months[(calendar2.get(Calendar.MONTH))]
                + "," + calendar2.get(Calendar.YEAR);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                    if (firebaseUser.isEmailVerified()) {
                        if (book.getText().toString().isEmpty()) {
                            book.setError("Please fill !");
                            book.requestFocus();
                        } else if (author.getText().toString().isEmpty()) {
                            author.setError("Please fill !");
                            author.requestFocus();
                        } else {
                            presenter.submit(book.getText().toString().trim(), author.getText().toString().trim(), publication.getText().toString().trim(), additional_info.getText().toString().trim(), sharedPref.getEmail(), current, current);
                        }
                    } else
                        showSnackBar();

            }
        });
        title.setText("Request Book");
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showSnackBar()
    {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Verify your e-mail id.", Snackbar.LENGTH_INDEFINITE)
                .setAction(" Verify now", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMail();
                    }
                });
        snackbar.setActionTextColor(Color.parseColor("#CEA100"));
        View view = snackbar.getView();
        TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#CEA100"));
        view.setBackgroundColor(Color.parseColor("#FFE588"));
        snackbar.show();
    }

    private void sendMail()
    {
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RequestActivity.this, "Verification Mail Has been Sent, It may Take few minutes to verify.", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toasty.error(RequestActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT,true).show();
                    }
                });
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccess(String s)
    {
        Toasty.success(this, "SuccessFully Requested For The Book", Toast.LENGTH_LONG,false).show();
        book.setText("");
        author.setText("");
        publication.setText("");
        additional_info.setText("");

        Handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(RequestActivity.this, HomePageActivity.class));
            }
        },700);
    }
}
