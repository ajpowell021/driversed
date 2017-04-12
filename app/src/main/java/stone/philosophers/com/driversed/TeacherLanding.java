package stone.philosophers.com.driversed;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View.OnClickListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

public class TeacherLanding extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ListView studentListView;
    private ArrayList<String> studentNameList = new ArrayList<String>();
    private Button addStudentButton;
    final Context context = this;

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
        mainToolbar.setBackgroundColor(Color.parseColor("#03a9f4"));
        setSupportActionBar(mainToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
      
        studentListView = (ListView) findViewById(R.id.studentListView);
        addStudentButton = (Button) findViewById(R.id.addStudentButton);

        addStudentButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                buildAddStudentAlertDialog();
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
                studentNameList = new ArrayList<String>();

                for(int i = 0; i < studentArray.length; i++) {
                    studentNameList.add(i, studentArray[i].getName());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        TeacherLanding.this,
                        android.R.layout.simple_list_item_1,
                        studentNameList);
                studentListView.setAdapter(arrayAdapter);
                studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(TeacherLanding.this, TeacherStudentView.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void buildAddStudentAlertDialog() {
        final Dialog addStudentAlertDialog = new Dialog(context);
        addStudentAlertDialog.setContentView(R.layout.add_student_dialog_view);
        addStudentAlertDialog.setCancelable(true);
        Button positiveButton = (Button) addStudentAlertDialog.findViewById(R.id.dialog_save_button);
        positiveButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText studentName = ((EditText) addStudentAlertDialog.findViewById(R.id.enterStudentName));
                        EditText teacherName = (EditText) addStudentAlertDialog.findViewById(R.id.enterTeacherName);
                        EditText dayHoursDriven = (EditText) addStudentAlertDialog.findViewById(R.id.enterHoursDriven);
                        EditText nightHoursDriven = (EditText) addStudentAlertDialog.findViewById(R.id.enterNightHoursDriven);
                        EditText emailAddress = (EditText) addStudentAlertDialog.findViewById(R.id.enterEmail);

                        if(studentName.getText().toString().isEmpty() || teacherName.getText().toString().isEmpty() || emailAddress.getText().toString().isEmpty()) {

                            addStudentAlertDialog.dismiss();
                            Toast.makeText(context, getString(R.string.add_student_error_message), Toast.LENGTH_LONG).show();
                        }
                        else{

                            String name = studentName.getText().toString();
                            String teacher = teacherName.getText().toString();
                            double dayHoursDouble = Double.parseDouble(dayHoursDriven.getText().toString());
                            double nightHoursDouble = Double.parseDouble(nightHoursDriven.getText().toString());
                            String email = emailAddress.getText().toString();

                            final Student studentToAdd = new Student(name, teacher, dayHoursDouble, nightHoursDouble, email);

                            final FireBaseHandeler db = new FireBaseHandeler(mFirebaseAuth);
                            db.addStudent(studentToAdd);
                            addStudentAlertDialog.dismiss();
                        }
                    }
                });

        Button cancelButton = (Button) addStudentAlertDialog.findViewById(R.id.dialog_cancel_button);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addStudentAlertDialog.dismiss();
            }
        });
        addStudentAlertDialog.show();
    }
}
