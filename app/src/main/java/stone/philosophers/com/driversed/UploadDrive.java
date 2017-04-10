package stone.philosophers.com.driversed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;



// After a drive has been completed, upload the info to the database.
// Also uploads an image if wanted.

public class UploadDrive extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorage;
    private ImageView mImageView;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_LOAD_PHOTO = 2;

    private AlertDialog imageUploadDialog;
    private ProgressDialog uploadProgressDialog;
    private Button selectImageButton;
    private Button uploadTripButton;
    private Button cancelButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_drive);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        uploadProgressDialog = new ProgressDialog(this);

        // Find UI Elements
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        selectImageButton = (Button) findViewById(R.id.selectImageButton);
        mImageView = (ImageView) findViewById(R.id.uploadedImage);
        mImageView.setImageResource(R.drawable.taters);
        uploadTripButton = (Button) findViewById(R.id.uploadTripButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        // On click listeners
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildUploadDialog();
            }
        });

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
                    double milesDriven = Double.parseDouble(milesDrivenString);
                    long startTime = Long.parseLong(startTimeString);
                    long endTime = Long.parseLong(endTimeString);

                    Trip tripToAdd = new Trip(studentName, teacherName, startTime, endTime, milesDriven, email);

                    final FireBaseHandeler db = new FireBaseHandeler(mFirebaseAuth);
                    db.addTrip(tripToAdd);

                    Intent intent = new Intent(UploadDrive.this, StudentLanding.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void buildUploadDialog() {
        new AlertDialog.Builder(UploadDrive.this)
                .setTitle(R.string.upload_dialog_title)
                .setMessage(R.string.upload_dialog_message)
                .setPositiveButton(R.string.upload_dialog_camera_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }
                })
                .setNegativeButton(R.string.global_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Left empty on purpose.
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);


            // THIS STUFF MAY BE USEFUL FOR UPLOADING? IDK

            //uploadProgressDialog.setMessage("Uploading ... ");
            //uploadProgressDialog.show();

            // Uploading
            //StorageReference filepath = mStorage.child("Photos").child(selectedImage.getLastPathSegment());

            //filepath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               // @Override
               // public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 //   uploadProgressDialog.dismiss();
                  //  Toast.makeText(UploadDrive.this, "Upload Done", Toast.LENGTH_LONG).show();
              //  }
           // });
        }

        if (requestCode == REQUEST_LOAD_PHOTO && resultCode == RESULT_OK) {


        }
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
}


