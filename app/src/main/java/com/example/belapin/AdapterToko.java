package com.example.belapin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterToko extends RecyclerView.Adapter<AdapterToko.HolderToko> {

    private Context context;
    public ArrayList<ModelToko> tokoList;

    public AdapterToko(Context context, ArrayList<ModelToko> tokoList) {
        this.context = context;
        this.tokoList = tokoList;
    }

    @NonNull
    @Override
    public HolderToko onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout baris_toko.xml
        View view = LayoutInflater.from(context).inflate(R.layout.baris_toko, parent, false);
        return new HolderToko(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderToko holder, int position) {
        // get data
        ModelToko modelToko = tokoList.get(position);
        String akunTipe = modelToko.getTipeAkun();
        String alamat = modelToko.getAlamat();
        String kota = modelToko.getKota();
        String negara = modelToko.getNegara();
        String ongkir = modelToko.getOngkir();
        String email = modelToko.getEmail();
        String latitude = modelToko.getLatitute();
        String longitude = modelToko.getLongitude();
        String online = modelToko.getOnline();
        String name = modelToko.getName();
        String nohp = modelToko.getPhone();
        String uid = modelToko.getUid();
        String timestamp = modelToko.getTimestamp();
        String tokoBuka = modelToko.getTokoBuka();
        String provinsi = modelToko.getProvinsi();
        String namaToko = modelToko.getNamaToko();

        // set data
        holder.namaToko.setText(namaToko);
        holder.nohpToko.setText(nohp);
        holder.alamatToko.setText(alamat);

        // check if online
        if (online.equals("true")) {
            // toko is online
            holder.online.setVisibility(View.VISIBLE);
        }
        else {
            // toko is offline
            holder.online.setVisibility(View.GONE);
        }

        // check if toko open
        if (tokoBuka.equals("true")) {
            // toko open
            holder.tokoTutup.setVisibility(View.GONE);
        }
        else {
            // toko close
            holder.tokoTutup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return tokoList.size(); // return number of records
    }

    // view holder
    class HolderToko extends RecyclerView.ViewHolder{

        // ui view of baris toko
        private ImageView tokoIcon, online;
        private TextView tokoTutup, namaToko, nohpToko, alamatToko;

        public HolderToko(@NonNull View itemView) {
            super(itemView);

            tokoIcon = itemView.findViewById(R.id.tokoIcon);
            online = itemView.findViewById(R.id.online);
            tokoTutup = itemView.findViewById(R.id.tokoTutup);
            namaToko = itemView.findViewById(R.id.namaToko);
            alamatToko = itemView.findViewById(R.id.alamatToko);
            nohpToko = itemView.findViewById(R.id.nohpToko);

        }
    }

}
