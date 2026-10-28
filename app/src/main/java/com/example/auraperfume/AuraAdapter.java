package com.example.auraperfume;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AuraAdapter extends RecyclerView.Adapter<AuraAdapter.AuraViewHolder> {

    public interface AuraClickListener {
        void onUpdateClick(Aura aura);
        void onDeleteClick(Aura aura);
    }

    private final Context context;
    private final List<Aura> auraList;
    private final AuraClickListener listener;

    public AuraAdapter(Context context, List<Aura> auraList, AuraClickListener listener) {
        this.context = context;
        this.auraList = auraList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AuraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aura, parent, false);
        return new AuraViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull AuraViewHolder holder, int position) {
        Aura aura = auraList.get(position);
        holder.tvName.setText(aura.getName());
        holder.tvType.setText(aura.getType());

        // Check if perfume is sold out
        if (aura.isSoldOut()) {
            holder.tvSoldOut.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(0.6f);
        } else {
            holder.tvSoldOut.setVisibility(View.GONE);
            holder.itemView.setAlpha(1.0f);
        }

        // Glide downloads the image from its URL and shows it
        Glide.with(holder.itemView.getContext())
                .load(aura.getLogoUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.ivItem);

        // Update and Delete button clicks
        if (listener != null) {
            holder.btnUpdate.setOnClickListener(v -> listener.onUpdateClick(aura));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(aura));
        }

        // Click row to show details dialog or sold out message
        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (aura.isSoldOut()) {
                builder.setTitle(aura.getName() + " - Sold Out");
                builder.setMessage("Sorry, " + aura.getName() + " is currently sold out and unavailable.");
            } else {
                builder.setTitle(aura.getName());
                builder.setMessage(
                        "Type: " + aura.getType() + "\n" +
                                "Location: " + aura.getLocation() + "\n\n" +
                                "Description:\n" + aura.getDescription()
                );
            }
            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return auraList != null ? auraList.size() : 0;
    }

    // ViewHolder keeps the views of one row so we find them only once
    public static class AuraViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvSoldOut;
        ImageView ivItem;
        Button btnUpdate, btnDelete;

        public AuraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAuraName);
            tvType = itemView.findViewById(R.id.tvAuraType);
            tvSoldOut = itemView.findViewById(R.id.tvSoldOut);
            ivItem = itemView.findViewById(R.id.ivAuraLogo);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
