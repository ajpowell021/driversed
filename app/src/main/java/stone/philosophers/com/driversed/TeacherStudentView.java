package stone.philosophers.com.driversed;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TeacherStudentView extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    //protected Button mapButton;

    private String TAG = "StudentLanding";
    private ListView tripListView;
    private Context context = this;
    private TextView totalDayHours;
    private TextView totalNightHours;
    private static boolean through = false;

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
        mainToolbar.setBackgroundColor(Color.parseColor("#03a9f4"));

        setSupportActionBar(mainToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //mapButton = (Button) findViewById(R.id.mapButton);
        totalDayHours = (TextView) findViewById(R.id.student_day_hours);
        totalNightHours = (TextView) findViewById(R.id.student_night_hours);

        tripListView = (ListView) findViewById(R.id.tripListView);

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
        final String studentEmail = "student@umflint.edu";

        final FireBaseHandeler db = new FireBaseHandeler(mFirebaseAuth);

        db.setCustomStudentListener(new FireBaseHandeler.CustomStudentListener() {
            @Override
            public void onStudentsLoaded(Student[] students) {
                String totalDayHoursText = "";
                Student currentStudent = db.getStudentFromEmail(studentEmail);
                if(through == true){
                    Double newDayHours = currentStudent.getHoursDriven() + 7;
                    totalDayHoursText = newDayHours + "/30 Day Hours";
                }
                else{
                    totalDayHoursText = currentStudent.getHoursDriven() + "/30 Day Hours";
                    through = true;
                }
                String totalNightHoursText = currentStudent.getNightHoursDriven() + "/10 Night Hours";
                totalDayHours.setText(totalDayHoursText);
                totalNightHours.setText(totalNightHoursText);
            }
        });

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
                    formattedTrip = getTimeFromMilli(startTime) + getString(R.string.time_to) + getTimeFromMilli(endTime)
                            + "     " + String.format("%.2f", milesDriven) + R.string.miles;
                    tripDisplayNameList.add(i, formattedTrip);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        TeacherStudentView.this,
                        android.R.layout.simple_list_item_1,
                        tripDisplayNameList
                );

                tripListView.setAdapter(arrayAdapter);
                tripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final Dialog tripDialog = new Dialog(context);
                        tripDialog.setContentView(R.layout.view_trip_dialog_view);
                        TextView studentNameTextView = (TextView) tripDialog.findViewById(R.id.student_name_text);
                        TextView timeTextView = (TextView) tripDialog.findViewById(R.id.start_end_time);
                        TextView milesTextView = (TextView) tripDialog.findViewById(R.id.miles_driven_text);
                        studentNameTextView.setText(tripArray[i].getStudentName());
                        timeTextView.setText(getTimeFromMilli(tripArray[i].getStartTime()) + getString(R.string.time_to) + getTimeFromMilli(tripArray[i].getEndTime()));
                        milesTextView.setText(String.format("%.2f" ,tripArray[i].getTotalMilesDriven()) + " " + getString(R.string.miles_driven_hint));
                        tripDialog.show();
                    }
                });
            }
        });

    }

    // I don't think this is used anymore.
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

    public String getTimeFromMilli(Long milliseconds){
        String formattedTime;

        formattedTime = DateFormat.getTimeInstance().format(milliseconds);

        return formattedTime;
    }
}