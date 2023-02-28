package com.example.belapin;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterBelanjaanUser extends RecyclerView.Adapter<AdapterBelanjaanUser.HolderBelanjaanUser>{

    private Context context;
    private ArrayList<ModelBelanjaan> belanjaanArrayList;

    public AdapterBelanjaanUser(Context context, ArrayList<ModelBelanjaan> belanjaanArrayList) {
        this.context = context;
        this.belanjaanArrayList = belanjaanArrayList;
    }
    
    @NonNull
    @Override
    public HolderBelanjaanUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.baris_belanjaan_user, parent, false);
        return new HolderBelanjaanUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBelanjaanUser holder, int position) {

        // get data
        ModelBelanjaan modelBelanjaan = belanjaanArrayList.get(position);
        String pesananId = modelBelanjaan.getPesananId();
        String yangOrder = modelBelanjaan.getYangOrder();
        String biayaOrder = modelBelanjaan.getCost();
        String statusOrder = modelBelanjaan.getStatusOrder();
        String waktuOrder = modelBelanjaan.getWaktoOrder();
        String keOrder = modelBelanjaan.getOrderKe();

        // get pasar info
        loadPasarInfo(modelBelanjaan, holder);

        holder.totalAmount.setText("Total: Rp"+biayaOrder);
        holder.belanjaanId.setText("Belanjaan ID:"+pesananId);
        holder.statusBelanjaan.setText(statusOrder);
        // change status
        if (statusOrder.equals("Selesai")) {
            holder.statusBelanjaan.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if (statusOrder.equals("Batal")) {
            holder.statusBelanjaan.setTextColor(context.getResources().getColor(R.color.red));
        }

        // convert timestamp to time format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(waktuOrder));
        String formatDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.waktu.setText(formatDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open belanjaan detail, need to keys there, pesananId, orderKe
                Intent intent = new Intent(context, BelanjaanDetailUser.class);
                intent.putExtra("pesananId", pesananId);
                intent.putExtra("keOrder", keOrder);

                // now get these values through intent on BelanjaanDetailUser
                context.startActivity(intent);
            }
        });
    }

    private void loadPasarInfo(ModelBelanjaan modelBelanjaan, HolderBelanjaanUser holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        databaseReference.child(modelBelanjaan.getOrderKe()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namaToko = ""+snapshot.child("namaToko").getValue();
                holder.namaPasar.setText(namaToko);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return belanjaanArrayList.size();
    }

    // view holder class
    class HolderBelanjaanUser extends RecyclerView.ViewHolder {

        // view of layout
        private TextView belanjaanId, waktu, namaPasar, totalAmount, statusBelanjaan;
        private ImageView next;

        public HolderBelanjaanUser(@NonNull View itemView) {
            super(itemView);

            belanjaanId = itemView.findViewById(R.id.belanjaanId);
            waktu = itemView.findViewById(R.id.waktu);
            namaPasar = itemView.findViewById(R.id.namaPasar);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            statusBelanjaan = itemView.findViewById(R.id.statusBelanjaan);
            next = itemView.findViewById(R.id.next);

        }
    }
}
