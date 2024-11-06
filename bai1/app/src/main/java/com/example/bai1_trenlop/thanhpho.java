package com.example.bai1_trenlop;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class thanhpho extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView recyclerView;
    Adapter adapter;
    List<molder> list;
    Button btnthem;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanhpho);

        // Khởi tạo Firebase và danh sách
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();

        // Thiết lập RecyclerView và Adapter
        recyclerView = findViewById(R.id.RcvThanhPho);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, list);
        recyclerView.setAdapter(adapter);

        // Lấy danh sách dữ liệu từ Firestore
        loadDataFromFirestore();

        // Nút thêm thành phố
        btnthem = findViewById(R.id.btnthem);
        btnthem.setOnClickListener(view -> showAddCityDialog());
    }

    private void loadDataFromFirestore() {
        db.collection("ThanhPho").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(thanhpho.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Xóa danh sách cũ và cập nhật danh sách mới
                list.clear();
                if (value != null) {
                    for (QueryDocumentSnapshot doc : value) {
                        molder city = doc.toObject(molder.class);
                        city.setId(doc.getId());  // Gán ID tài liệu cho đối tượng
                        list.add(city);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void showAddCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View viewDialog = inflater.inflate(R.layout.item_them, null);
        builder.setView(viewDialog);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText edtThem = viewDialog.findViewById(R.id.edt_them);
        Button btnthem_them = viewDialog.findViewById(R.id.btnthem_them);

        btnthem_them.setOnClickListener(view -> {
            String cityName = edtThem.getText().toString();
            if (cityName.isEmpty()) {
                Toast.makeText(thanhpho.this, "Tên thành phố không được để trống", Toast.LENGTH_SHORT).show();
            } else {
                molder newCity = new molder(cityName);
                db.collection("ThanhPho").add(newCity)
                        .addOnSuccessListener(documentReference -> {
                            newCity.setId(documentReference.getId());  // Gán ID tài liệu
                            list.add(newCity); // Thêm ngay vào danh sách (tùy chọn)
                            adapter.notifyDataSetChanged(); // Cập nhật adapter ngay lập tức
                            Toast.makeText(thanhpho.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(thanhpho.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
