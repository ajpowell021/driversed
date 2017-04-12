package stone.philosophers.com.driversed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


// After a drive has been completed, upload the info to the database.
// Also uploads an image if wanted.

public class UploadDrive extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorage;

    private AlertDialog imageUploadDialog;
    private ProgressDialog uploadProgressDialog;
    private Button selectImageButton;
    private Button uploadTripButton;
    private Button cancelButton;
    private double milesDrivenFromMap;
    private long startTime;
    private long endTime;
    private MapsActivity mapsActivity;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_drive);

        milesDrivenFromMap = mapsActivity.milesFromMap;
        startTime = mapsActivity.mStartTime;
        endTime = mapsActivity.mEndTime;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        uploadProgressDialog = new ProgressDialog(this);

        final FireBaseHandeler db = new FireBaseHandeler(mFirebaseAuth);
        db.setCustomStudentListener(new FireBaseHandeler.CustomStudentListener() {
                                        @Override
                                        public void onStudentsLoaded(Student[] students) {
                                            EditText studentNameEditText = (EditText) findViewById(R.id.studentEditText);
                                            EditText teacherNameEditText = (EditText) findViewById(R.id.teacherEditText);
                                            EditText milesDrivenEditText = (EditText) findViewById(R.id.mileEditText);
                                            Student loggedInStudent = db.getStudentFromEmail(mFirebaseUser.getEmail());
                                            studentNameEditText.setText(loggedInStudent.getName());
                                            teacherNameEditText.setText(loggedInStudent.getTeacher());
                                            String formattedMilesFromMap = Double.toString(milesDrivenFromMap);
                                            milesDrivenEditText.setText(formattedMilesFromMap + " Miles");
                                            EditText startEditText = (EditText) findViewById(R.id.startTimeEditText);
                                            EditText endEditText = (EditText) findViewById(R.id.endTimeEditText);
                                            String newStartTime = getTimeFromMilli(startTime);
                                            startEditText.setText(newStartTime);
                                            String newEndTime = getTimeFromMilli(endTime);
                                            endEditText.setText(newEndTime);

                                        }
                                    });

        // Find UI Elements
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        uploadTripButton = (Button) findViewById(R.id.uploadTripButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadDrive.this, StudentLanding.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        uploadTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText studentNameEditText = (EditText) findViewById(R.id.studentEditText);
                EditText teacherNameEditText = (EditText) findViewById(R.id.teacherEditText);
                EditText milesDrivenEditText = (EditText) findViewById(R.id.mileEditText);
                EditText startTimeEditText = (EditText) findViewById(R.id.startTimeEditText);
                EditText endTimeEditText = (EditText) findViewById(R.id.endTimeEditText);

                String studentName = studentNameEditText.getText().toString();
                String teacherName = teacherNameEditText.getText().toString();
                String milesDrivenString = milesDrivenEditText.getText().toString();
                String startTimeString = startTimeEditText.getText().toString();
                String endTimeString = endTimeEditText.getText().toString();
                String email =  mFirebaseUser.getEmail();

                if (studentName.isEmpty() || teacherName.isEmpty() || milesDrivenString.isEmpty() || startTimeString.isEmpty() || endTimeString.isEmpty()) {
                    Toast.makeText(UploadDrive.this, getString(R.string.add_trip_error), Toast.LENGTH_LONG).show();
                }
                else {
                    double milesDriven = milesDrivenFromMap;
                    long startDriveTime = startTime;
                    long endDriveTime = endTime;

                    Trip tripToAdd = new Trip(studentName, teacherName, startDriveTime, endDriveTime, milesDriven, email);

                    final FireBaseHandeler db = new FireBaseHandeler(mFirebaseAuth);
                    db.addTrip(tripToAdd);

                    db.setCustomStudentListener(new FireBaseHandeler.CustomStudentListener() {
                        @Override
                        public void onStudentsLoaded(Student[] students) {
                            long totalTime = endTime - startTime;
                            final double hours   = (int) ((totalTime / (1000*60*60)) % 24) + 7;
                            Student student = db.getStudentFromEmail(mFirebaseUser.getEmail());
                            student.setHoursDriven(hours);
                        }
                    });

                    Intent intent = new Intent(UploadDrive.this, StudentLanding.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public String getTimeFromMilli(Long milliseconds){
        String formattedTime;

        formattedTime = DateFormat.getTimeInstance().format(milliseconds);

        return formattedTime;
    }
}


