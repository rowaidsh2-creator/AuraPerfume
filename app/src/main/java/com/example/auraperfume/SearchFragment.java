package com.example.auraperfume;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;

public class SearchFragment extends Fragment {

    private EditText etName, etType, etDescription, etLocation;
    private CheckBox cbSoldOut;
    private ImageView ivLogo;
    private Button btnAdd;

    private Uri selectedImageUri;
    private FireBaseServices fbs;

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivLogo.setImageURI(uri);
                }
            });

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fbs = FireBaseServices.getInstance();
        etName = view.findViewById(R.id.etName);
        etType = view.findViewById(R.id.etType);
        etDescription = view.findViewById(R.id.etDescription);
        etLocation = view.findViewById(R.id.etLocation);
        cbSoldOut = view.findViewById(R.id.cbSoldOut);
        ivLogo = view.findViewById(R.id.ivLogo);
        btnAdd = view.findViewById(R.id.btnAdd);

        ivLogo.setOnClickListener(v -> pickImage.launch("image/*"));
        btnAdd.setOnClickListener(v -> addAura());
    }

    private void addAura() {
        String name = etName.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        boolean isSoldOut = cbSoldOut.isChecked();

        if (name.isEmpty() || type.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference auraRef = fbs.getDb().collection("auras").document();
        String id = auraRef.getId();

        if (selectedImageUri != null) {
            StorageReference logoRef = fbs.getStorage().getReference().child("aura_logos/" + id);
            logoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> logoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        saveAuraToFirestore(auraRef, id, name, type, description, location, downloadUri.toString(), isSoldOut);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            saveAuraToFirestore(auraRef, id, name, type, description, location, "", isSoldOut);
        }
    }

    private void saveAuraToFirestore(DocumentReference auraRef, String id, String name, String type, String description, String location, String logoUrl, boolean isSoldOut) {
        Aura aura = new Aura(id, name, type, description, location, logoUrl, isSoldOut);

        auraRef.set(aura)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Aura added successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    MainActivity activity = (MainActivity) requireActivity();
                    activity.showFragment(new showAura());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add Aura: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        etName.setText("");
        etType.setText("");
        etDescription.setText("");
        etLocation.setText("");
        cbSoldOut.setChecked(false);
        ivLogo.setImageResource(R.drawable.ic_launcher_foreground);
        selectedImageUri = null;
    }
}
