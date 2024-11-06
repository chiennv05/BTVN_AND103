package com.example.bai1_trenlop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterHoder> {
    Context context;
    List<molder> list;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Adapter(Context context, List<molder> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho item
        View view = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);
        return new AdapterHoder(view); // Trả về AdapterHoder
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHoder holder, int position) {
        molder city = list.get(position);
        holder.txtthanhpho.setText(city.getTenthanhpho());

        // Xử lý sự kiện khi nhấn nút "Update"
        holder.update.setOnClickListener(v -> showUpdateCityDialog(city, position));

        // Xử lý sự kiện khi nhấn nút "Delete"
        holder.delete.setOnClickListener(v -> {
            if (city.getId() != null) {
                db.collection("ThanhPho").document(city.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, list.size());
                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(context, "ID không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showUpdateCityDialog(molder city, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewDialog = inflater.inflate(R.layout.item_sua, null); // Sử dụng layout mà bạn cung cấp
        builder.setView(viewDialog);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText edtUpdate = viewDialog.findViewById(R.id.edt_ud);
        Button btnUpdate = viewDialog.findViewById(R.id.btnupdate_ud);

        edtUpdate.setText(city.getTenthanhpho());

        btnUpdate.setOnClickListener(view -> {
            String updatedCityName = edtUpdate.getText().toString();
            if (updatedCityName.isEmpty()) {
                Toast.makeText(context, "Tên thành phố không được để trống", Toast.LENGTH_SHORT).show();
            } else {
                db.collection("ThanhPho").document(city.getId())
                        .update("tenthanhpho", updatedCityName)
                        .addOnSuccessListener(aVoid -> {
                            city.setTenthanhpho(updatedCityName);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public class AdapterHoder extends RecyclerView.ViewHolder {
        TextView txtthanhpho;
        Button update, delete;

        public AdapterHoder(@NonNull View itemView) {
            super(itemView);
            txtthanhpho = itemView.findViewById(R.id.txtthanhpho);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
