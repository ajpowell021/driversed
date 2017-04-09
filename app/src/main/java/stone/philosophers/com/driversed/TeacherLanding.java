package stone.philosophers.com.driversed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

public class TeacherLanding extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ListView studentListView;
    private ArrayList<String> studentNameList = new ArrayList<String>();
    private Button addStudentButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_landing);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        studentListView = (ListView) findViewById(R.id.studentListView);
        addStudentButton = (Button) findViewById(R.id.addStudentButton);

        addStudentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                
            }
        });


        // This must run to populate the student list from the database.
        fillStudentListView();
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

    private void fillStudentListView() {

        final FireBaseHandeler db = new FireBaseHandeler(mFirebaseAuth);
        db.setCustomStudentListener(new FireBaseHandeler.CustomStudentListener() {
            @Override
            public void onStudentsLoaded(Student[] students) {
                Student[] studentArray =  db.getStudents();

                for(int i = 0; i < studentArray.length; i++) {
                    studentNameList.add(i, studentArray[i].getName());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        TeacherLanding.this,
                        android.R.layout.simple_list_item_1,
                        studentNameList);
                studentListView.setAdapter(arrayAdapter);
            }
        });
    }
}
