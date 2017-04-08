package stone.philosophers.com.driversed;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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


    public FireBaseHandeler(FirebaseAuth fba){
        this.fba = fba;
    }

    public void test(){
        Log.d(TAG,"Trying to upload to firebase");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("debug");

        myRef.child(System.nanoTime()+"").setValue("Hello wordl!");

    }

    public void addStudent(Student s){
        Log.d(TAG,"Trying to upload student to firebase");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(studentsDBName);

        myRef.child(System.nanoTime()+"").setValue(s);

    }
}
