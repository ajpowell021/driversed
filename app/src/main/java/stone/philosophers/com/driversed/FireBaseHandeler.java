package stone.philosophers.com.driversed;

import android.util.Log;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shadow4 on 4/8/2017.
 * Handles all firebase database related things
 *
 *
 */

public class FireBaseHandeler {
    private String TAG = "FireBaseHandeler";
    private FirebaseAuth fba;
    private String studentsDBName = "students";
    private String tripDBName = "trips";
    private boolean studentIsDownloaded = false;
    private Student[] studentsList = null;
    private Trip[] tripList = null;
    private boolean tripIsDownloaded = false;
    private CustomStudentListener customStudentListener;
    private CustomTripListener customTripListener;
    public boolean isStudentIsDownloaded() {
        return studentIsDownloaded;
    }
    public boolean isTripIsDownloaded() {
        return tripIsDownloaded;
    }


    public FireBaseHandeler(FirebaseAuth fba) {
        this.fba = fba;
        this.customStudentListener = null;
        this.customTripListener = null;
        startStudentListener();
        startTripListener();
    }

    public void setCustomStudentListener(CustomStudentListener customStudentListener) {
        this.customStudentListener = customStudentListener;
    }

    public void setCustomTripListener(CustomTripListener customTripListener) {
        this.customTripListener = customTripListener;
    }

    public void test() {
        Log.d(TAG, "Trying to upload to firebase");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("debug");

        myRef.child(System.nanoTime() + "").setValue("Hello wordl!");

    }

    public void addStudent(Student s) {
        Log.d(TAG, "Trying to upload student to firebase");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(studentsDBName);

        myRef.child(System.nanoTime() + "").setValue(s);
    }

    public void addTrip(Trip t) {
        Log.d(TAG, "Trying to upload student to firebase");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(tripDBName);

        myRef.child(System.nanoTime() + "").setValue(t);
    }

    private void startStudentListener() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(studentsDBName);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Student> tempStudentList = new ArrayList<Student>();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    //Log.d(TAG,student.getName());
                    tempStudentList.add(student);
                }
                studentIsDownloaded = true;
                studentsList = tempStudentList.toArray(new Student[tempStudentList.size()]);

                if (customStudentListener != null) {
                    customStudentListener.onStudentsLoaded(studentsList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startTripListener() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(tripDBName);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Trip> tempStudentList = new ArrayList<Trip>();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    Trip trip = studentSnapshot.getValue(Trip.class);
                    //Log.d(TAG,student.getName());
                    tempStudentList.add(trip);
                }
                tripIsDownloaded = true;
                tripList = tempStudentList.toArray(new Trip[tempStudentList.size()]);

                if (customTripListener != null) {
                    customTripListener.onTripsLoaded(tripList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Student[] getStudents() {
        Student output[] = null;

        output = studentsList;

        return output;
    }

    public Student getStudentFromEmail(String email) {
        Student output = null;

        for (Student s: studentsList){
            if (s.getEmailAddress().equals(email)) {
                output = s;
            }
        }
        return output;
    }

    public Student[] getStudents(String teacher){
        ArrayList<Student> outputList = new ArrayList<Student>();

        for (Student s: studentsList){
            if (s.getTeacher().equals(teacher)){
                outputList.add(s);
            }
        }

        return outputList.toArray(new Student[outputList.size()]);
    }

    public Trip[] getTripList(String email){
        ArrayList<Trip> outputList = new ArrayList<Trip>();

        for (Trip t: tripList){
            if (t.getStudentEmail().equals(email)){
                outputList.add(t);
            }
        }

        return outputList.toArray(new Trip[outputList.size()]);
    }

    public interface CustomStudentListener {
        public void onStudentsLoaded(Student[] students);
    }

    public interface CustomTripListener {
        public void onTripsLoaded(Trip[] trips);
    }
}
