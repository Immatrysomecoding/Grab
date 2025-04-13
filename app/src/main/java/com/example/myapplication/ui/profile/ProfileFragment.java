package com.example.myapplication.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // UI elements
    private EditText etName;
    private TextView tvPhoneNumber;
    private EditText etEmail;
    private Spinner spinnerGender;
    private TextView btnSave;
    private TextView btnLogout;
    private SwitchCompat switchGoogle;
    private SwitchCompat switchFacebook;
    private SwitchCompat switchApple;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    // For tracking changes
    private String originalName = "";
    private String originalEmail = "";
    private int originalGenderPosition = 0;
    private boolean dataChanged = false;
    private boolean isValidData = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews(view);
        setupFirebase();
        setupListeners();
        loadUserData();
        return view;
    }

    private void initializeViews(View view) {
        etName = view.findViewById(R.id.etName);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        etEmail = view.findViewById(R.id.etEmail);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        btnSave = view.findViewById(R.id.btnSave);
        btnLogout = view.findViewById(R.id.btnLogout);
        switchGoogle = view.findViewById(R.id.switchGoogle);
        switchFacebook = view.findViewById(R.id.switchFacebook);
        switchApple = view.findViewById(R.id.switchApple);

        // Initially disable save button
        updateSaveButton(false);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://grab-741f2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
    }

    private void loadUserData() {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Set phone number from Firebase Auth
            String phone = currentUser.getPhoneNumber();
            if (phone != null && !phone.isEmpty()) {
                // Format phone number, assuming it includes country code
                if (phone.startsWith("+84")) {
                    tvPhoneNumber.setText(phone.substring(3)); // Remove +84
                } else {
                    tvPhoneNumber.setText(phone);
                }
            }

            // Get other user data from Firebase Database
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Get name
                        if (snapshot.hasChild("name")) {
                            String name = snapshot.child("name").getValue(String.class);
                            etName.setText(name);
                            originalName = name != null ? name : "";
                        }

                        // Get email
                        if (snapshot.hasChild("email")) {
                            String email = snapshot.child("email").getValue(String.class);
                            etEmail.setText(email);
                            originalEmail = email != null ? email : "";
                        }

                        // Get gender
                        if (snapshot.hasChild("gender")) {
                            String gender = snapshot.child("gender").getValue(String.class);
                            setGenderSpinner(gender);
                        }

                        // Record original data to track changes
                        originalGenderPosition = spinnerGender.getSelectedItemPosition();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error loading user data: " + error.getMessage());
                    Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setGenderSpinner(String gender) {
        if (gender == null || gender.isEmpty()) {
            spinnerGender.setSelection(0); // Default position
            return;
        }

        String[] genderArray = getResources().getStringArray(R.array.gender_options);
        for (int i = 0; i < genderArray.length; i++) {
            if (genderArray[i].equalsIgnoreCase(gender)) {
                spinnerGender.setSelection(i);
                originalGenderPosition = i;
                break;
            }
        }
    }

    private void setupListeners() {
        // Text change listeners to detect modifications
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkForChanges();
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Validate email format
                String email = s.toString().trim();
                boolean isValid = email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches();

                if (!isValid) {
                    etEmail.setError("Invalid email format");
                    isValidData = false;
                } else {
                    etEmail.setError(null);
                    isValidData = true;
                }

                checkForChanges();
            }
        });

        // Spinner change listener
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkForChanges();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Focus change listeners to update UI
        etName.setOnFocusChangeListener((v, hasFocus) -> {
            updateEditTextBorder(etName, hasFocus);
        });

        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            updateEditTextBorder(etEmail, hasFocus);
        });

        // Save button
        btnSave.setOnClickListener(v -> {
            if (btnSave.isEnabled()) {
                saveUserData();
            }
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void updateEditTextBorder(EditText editText, boolean hasFocus) {
        if (hasFocus) {
            // Change border to green when focused
            editText.setBackgroundResource(R.drawable.edit_text_focused);
        } else {
            // Reset to default border when not focused
            editText.setBackgroundResource(R.drawable.edit_text_normal);
        }
    }

    private void checkForChanges() {
        String currentName = etName.getText().toString().trim();
        String currentEmail = etEmail.getText().toString().trim();
        int currentGenderPosition = spinnerGender.getSelectedItemPosition();

        boolean hasNameChanged = !currentName.equals(originalName);
        boolean hasEmailChanged = !currentEmail.equals(originalEmail);
        boolean hasGenderChanged = currentGenderPosition != originalGenderPosition;

        dataChanged = hasNameChanged || hasEmailChanged || hasGenderChanged;

        // Update save button state based on changes and data validity
        updateSaveButton(dataChanged && isValidData);
    }

    private void updateSaveButton(boolean enabled) {
        btnSave.setEnabled(enabled);
        if (enabled) {
            btnSave.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            btnSave.setTextColor(getResources().getColor(R.color.text_disabled));
        }
    }

    private void saveUserData() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to save changes", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();

        // Validate data
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create map of data to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        if (!email.isEmpty()) {
            updates.put("email", email);
        }
        updates.put("gender", gender);

        // Show loading state
        btnSave.setText("Saving...");
        btnSave.setEnabled(false);

        // Update in Firebase Database
        mDatabase.child("users").child(userId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    // Update was successful
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    // Update original values
                    originalName = name;
                    originalEmail = email;
                    originalGenderPosition = spinnerGender.getSelectedItemPosition();

                    // Reset button state
                    btnSave.setText("Save");
                    updateSaveButton(false);
                    dataChanged = false;
                })
                .addOnFailureListener(e -> {
                    // Update failed
                    Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSave.setText("Save");
                    updateSaveButton(true);
                });
    }

    private void logout() {
        if (mAuth != null) {
            mAuth.signOut();
            // Navigate to login screen
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }
}