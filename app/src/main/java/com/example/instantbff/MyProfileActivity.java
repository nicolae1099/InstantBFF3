package com.example.instantbff;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name;
    private EditText age;
    private EditText city;
    private EditText instagram;
    private EditText description;

    private FirebaseFirestore firebaseFirestore;
    private String TAG = "BFF";

    private ImageButton editNameButton;
    private ImageButton editAgeButton;
    private ImageButton editCityButton;
    private ImageButton editInstagramButton;
    private ImageButton editDescriptionButton;
    private Button editPasswordButton;

    private String changedName;
    private String changedAge;
    private String changedCity;
    private String changedInstagram;
    private String changedPersonality;
    private String changedDescription;

    private List<String> items = new ArrayList<>(
            Arrays.asList("INTJ", "INTP", "ENTJ", "ENTP", "INFJ", "INFP", "ENFJ", "ENFP",
                    "ESTJ", "ESFJ", "ISTP", "ISFP", "ESTP", "ESFP"));

    private String userId;
    private User user;

    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        name = findViewById(R.id.nameText_myProfile);
        age = findViewById(R.id.ageText_myProfile);
        city = findViewById(R.id.cityText_myProfile);
        instagram = findViewById(R.id.instagramText_myProfile);
        description = findViewById(R.id.description_myprofile);

        editNameButton = findViewById(R.id.edit_name_myProfile);
        name.setEnabled(false);
        name.setFocusable(false);
        editNameButton.setOnClickListener(this);

        editAgeButton = findViewById(R.id.edit_age_myProfile);
        age.setEnabled(false);
        age.setFocusable(false);
        editAgeButton.setOnClickListener(this);

        editCityButton = findViewById(R.id.edit_city_myProfile);
        city.setEnabled(false);
        city.setFocusable(false);
        editCityButton.setOnClickListener(this);

        editInstagramButton = findViewById(R.id.edit_instagram_myProfile);
        instagram.setEnabled(false);
        instagram.setFocusable(false);
        editInstagramButton.setOnClickListener(this);

        editPasswordButton = findViewById(R.id.changePassword_button);
        editPasswordButton.setOnClickListener(this);

        editDescriptionButton = findViewById(R.id.edit_description_myProfile);
        description.setEnabled(false);
        description.setFocusable(false);
        editDescriptionButton.setOnClickListener(this);

        //Initialize Views
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        imageView = (ImageView) findViewById(R.id.imgView);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("EXTRA_USER_ID");

        Spinner dropdown = findViewById(R.id.spinner_myprofile);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        DocumentReference docRef = firebaseFirestore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    user = new User(
                            String.valueOf(document.getData().get("name")),
                            String.valueOf(document.getData().get("email")),
                            String.valueOf(document.getData().get("city")),
                            String.valueOf(document.getData().get("instagram")),
                            String.valueOf(document.getData().get("age")),
                            String.valueOf(document.getData().get("personality")),
                            String.valueOf(document.getData().get("description"))
                    );
                    name.setText(user.getFullName());
                    age.setText(user.getAge());
                    city.setText(user.getCity());
                    instagram.setText(user.getInstagram());
                    description.setText(user.getDescription());

                    int position = 0;
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).equals(user.getPersonality())) {
                            position = i;
                            break;
                        }
                    }

                    dropdown.setSelection(position);

                    dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Map<String, Object> changedUser = new HashMap<>();
                            changedPersonality = parent.getItemAtPosition(position).toString();
                            user.setPersonality(changedPersonality);

                            saveChangedUser(changedUser);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReference("img/" + userId);
        final long ONE_MEGABYTE = 1024 * 1024;
        mImageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                    imageView.setMinimumHeight(dm.heightPixels);
                    imageView.setMinimumWidth(dm.widthPixels);
                    imageView.setImageBitmap(bm);
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit_name_myProfile) {
            editName();
        } else if (v.getId() == R.id.edit_age_myProfile){
            editAge();
        } else if (v.getId() == R.id.edit_instagram_myProfile) {
            editInstagram();
        } else if (v.getId() == R.id.edit_city_myProfile) {
            editCity();
        } else if (v.getId() == R.id.edit_description_myProfile) {
            editDescription();
        }
    }

    public void editName() {
        name.setEnabled(true);
        name.setFocusableInTouchMode(true);
        Map<String, Object> changedUser = new HashMap<>();

        name.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        changedName = name.getText().toString().trim();
                        name.setEnabled(false);
                        name.setFocusableInTouchMode(false);
                        user.setFullName(changedName);

                        saveChangedUser(changedUser);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
    }

    public void editAge() {
        age.setEnabled(true);
        age.setFocusableInTouchMode(true);

        Map<String, Object> changedUser = new HashMap<>();

        age.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        changedAge = age.getText().toString().trim();
                        age.setEnabled(false);
                        age.setFocusableInTouchMode(false);
                        user.setAge(changedAge);

                        saveChangedUser(changedUser);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
    }

    public void editCity() {
        city.setEnabled(true);
        city.setFocusableInTouchMode(true);

        Map<String, Object> changedUser = new HashMap<>();

        city.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        changedCity = city.getText().toString().trim();
                        city.setEnabled(false);
                        city.setFocusableInTouchMode(false);
                        user.setCity(changedCity);

                        saveChangedUser(changedUser);

                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
    }

    public void editInstagram() {
        instagram.setEnabled(true);
        instagram.setFocusableInTouchMode(true);

        Map<String, Object> changedUser = new HashMap<>();

        instagram.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        changedInstagram = instagram.getText().toString().trim();
                        instagram.setEnabled(false);
                        instagram.setFocusableInTouchMode(false);
                        user.setInstagram(changedInstagram);

                        saveChangedUser(changedUser);

                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
    }

    public void editDescription() {
        description.setEnabled(true);
        description.setFocusableInTouchMode(true);

        Map<String, Object> changedUser = new HashMap<>();

        description.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        changedDescription = description.getText().toString();
                        description.setEnabled(false);
                        description.setFocusableInTouchMode(false);

                        user.setDescription(changedDescription);

                        saveChangedUser(changedUser);

                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
    }

    public void saveChangedUser( Map<String, Object> changedUser) {
        changedUser.put("name", user.getFullName());
        changedUser.put("email", user.getEmail());
        changedUser.put("city", user.getCity());
        changedUser.put("instagram", user.getInstagram());
        changedUser.put("age", user.getAge());
        changedUser.put("personality", user.getPersonality());
        changedUser.put("description", user.getDescription());

        firebaseFirestore.collection("users").document(userId)
                .set(changedUser)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("img/"+ userId);
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(MyProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(MyProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


}
