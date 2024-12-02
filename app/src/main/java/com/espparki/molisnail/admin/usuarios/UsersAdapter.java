package com.espparki.molisnail.admin.usuarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.espparki.molisnail.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<User> userList;
    private final OnViewUserListener onViewListener;
    private final OnDeleteUserListener onDeleteListener;

    public interface OnViewUserListener {
        void onView(User user);
    }

    public interface OnDeleteUserListener {
        void onDelete(User user);
    }

    public UsersAdapter(List<User> userList, OnViewUserListener onViewListener, OnDeleteUserListener onDeleteListener) {
        this.userList = userList;
        this.onViewListener = onViewListener;
        this.onDeleteListener = onDeleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;
        private final TextView tvEmail;
        private final Button btnView;
        private final Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivUserPhoto);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            btnView = itemView.findViewById(R.id.btnViewUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }

        public void bind(User user) {
            String imageUrl = null;

            if (user.getGoogle_photo() != null && !user.getGoogle_photo().isEmpty()) {
                imageUrl = user.getGoogle_photo();
            } else if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
                imageUrl = user.getPhoto();
            }

            if (imageUrl != null) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_default_profile_picture).error(R.drawable.garras_icon))
                        .into(ivPhoto);
            } else {
                ivPhoto.setImageResource(R.drawable.ic_default_profile_picture);
            }

            tvEmail.setText(user.getEmail());

            btnView.setOnClickListener(v -> onViewListener.onView(user));
            btnDelete.setOnClickListener(v -> onDeleteListener.onDelete(user));
        }
    }
}
