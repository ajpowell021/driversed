package stone.philosophers.com.driversed;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StudentLanding extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private Button endDriveButton;
    private String TAG = "StudentLanding";
    private ListView tripListView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_landing);

        // Find UI Elements
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        endDriveButton = (Button) findViewById(R.id.finishDriveButton);

        setSupportActionBar(mainToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // On click listeners
        endDriveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentLanding.this, UploadDrive.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        tripListView = (ListView) findViewById(R.id.tripListView);


        //TODO remove this test code
        Log.d(TAG,"trying to upload to firebase");
        FireBaseHandeler fbh = new FireBaseHandeler(mFirebaseAuth);

        // This gets the trips from firebase.
        fillTripArrayForStudent();


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

    private void fillTripArrayForStudent() {
        final String studentEmail = mFirebaseUser.getEmail();

        final FireBaseHandeler db = new FireBaseHandeler(mFirebaseAuth);

        db.setCustomTripListener(new FireBaseHandeler.CustomTripListener() {
            @Override
            public void onTripsLoaded(Trip[] trips) {
                final Trip[] tripArray = db.getTripList(studentEmail);

                ArrayList<String> tripDisplayNameList = new ArrayList<String>();
                long startTime;
                long endTime;
                double milesDriven;
                String formattedTrip;

                for(int i = 0; i < tripArray.length; i++) {
                    startTime = tripArray[i].getStartTime();
                    endTime = tripArray[i].getEndTime();
                    milesDriven = tripArray[i].getTotalMilesDriven();
                    formattedTrip = convertTime(startTime) + getString(R.string.time_to) + convertTime(endTime) + "     " + milesDriven + " miles";
                    tripDisplayNameList.add(i, formattedTrip);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        StudentLanding.this,
                        android.R.layout.simple_list_item_1,
                        tripDisplayNameList
                );

                tripListView.setAdapter(arrayAdapter);
                tripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        AlertDialog tripDialog = new AlertDialog.Builder(StudentLanding.this).create();
                        tripDialog.setTitle(R.string.trip_information_title);
                        tripDialog.setMessage(tripArray[i].getStudentName());
                        tripDialog.show();
                    }
                });
            }
        });

    }

    private String convertTime(Long time){

        String amPm = "am";
        int hours = (int)(time / 100);
        if(hours == 0){
            hours = 12;
            amPm = "am";
        }
        if(hours > 12){
            amPm = "pm";
        }
        int minutes = (int)(time - (hours * 100));
        if(hours > 12) {
            hours = hours - 12;
        }
        DecimalFormat formatter = new DecimalFormat("00");
        String formattedMinutes = formatter.format(minutes);
        String formattedTime = hours + ":" + formattedMinutes + amPm;
        return formattedTime;
    }
}
