package com.example.belapin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterPesananUser extends RecyclerView.Adapter<AdapterPesananUser.HolderPesananUser> {

    private Context context;
    private ArrayList<ModelBelanjaanDetail> belanjaanDetailArrayList;

    public AdapterPesananUser(Context context, ArrayList<ModelBelanjaanDetail> belanjaanDetailArrayList) {
        this.context = context;
        this.belanjaanDetailArrayList = belanjaanDetailArrayList;
    }

    @NonNull
    @Override
    public HolderPesananUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.baris_pesananbarang, parent, false);
        return new HolderPesananUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPesananUser holder, int position) {
        // get data at position
        ModelBelanjaanDetail modelBelanjaanDetail = belanjaanDetailArrayList.get(position);

        String bId = modelBelanjaanDetail.getbId();
        String name = modelBelanjaanDetail.getName();
        String cost = modelBelanjaanDetail.getCost();
        String harga = modelBelanjaanDetail.getHarga();
        String kuantiti = modelBelanjaanDetail.getKuantiti();

        // set data
        holder.namaBarang.setText(name);
        holder.hargaSatuan.setText("Rp" + harga);
        holder.kuantitiBarang.setText("[" + kuantiti + "]");
        holder.hargaBarang.setText("Rp" + cost);
    }

    @Override
    public int getItemCount() {
        // return list size
        return belanjaanDetailArrayList.size();
    }

    class HolderPesananUser extends RecyclerView.ViewHolder {

        // views of baris_pesananbarang.xml
        private TextView namaBarang, hargaBarang, hargaSatuan, kuantitiBarang;

        public HolderPesananUser(@NonNull View itemView) {
            super(itemView);

            // init views
            namaBarang = itemView.findViewById(R.id.namaBarang);
            hargaBarang = itemView.findViewById(R.id.hargaBarang);
            hargaSatuan = itemView.findViewById(R.id.hargaSatuan);
            kuantitiBarang = itemView.findViewById(R.id.kuantitiBarang);
        }
    }
}
