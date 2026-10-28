package com.example.auraperfume;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class showAura extends Fragment implements AuraAdapter.AuraClickListener {

    private RecyclerView rvAuras;
    private TextView tvEmpty;
    private AuraAdapter adapter;
    private List<Aura> auraList;
    private FireBaseServices fbs;

    public showAura() {
        // Required empty public constructor
    }

    public static showAura newInstance() {
        return new showAura();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_aura, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fbs = FireBaseServices.getInstance();
        rvAuras = view.findViewById(R.id.rvAuras);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        auraList = new ArrayList<>();
        adapter = new AuraAdapter(getContext(), auraList, this);

        rvAuras.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAuras.setAdapter(adapter);

        loadAura();
    }

    private void loadAura() {
        fbs.getDb().collection("auras").get().addOnSuccessListener(result -> {
            if (!isAdded()) return;
            auraList.clear();
            for (DocumentSnapshot document : result.getDocuments()) {
                Aura aura = document.toObject(Aura.class);
                if (aura != null) {
                    aura.setId(document.getId());
                    auraList.add(aura);
                }
            }
            adapter.notifyDataSetChanged();
            tvEmpty.setVisibility(auraList.isEmpty() ? View.VISIBLE : View.GONE);
        }).addOnFailureListener(e -> {
            if (!isAdded()) return;
            Toast.makeText(getContext(), "Could not load: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onUpdateClick(Aura aura) {
        Toast.makeText(getContext(), "Update clicked for " + aura.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Aura aura) {
        if (aura.getId() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Perfume")
                .setMessage("Are you sure you want to delete " + aura.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    fbs.getDb().collection("auras").document(aura.getId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                if (!isAdded()) return;
                                Toast.makeText(getContext(), aura.getName() + " deleted successfully", Toast.LENGTH_SHORT).show();
                                loadAura();
                            })
                            .addOnFailureListener(e -> {
                                if (!isAdded()) return;
                                Toast.makeText(getContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
