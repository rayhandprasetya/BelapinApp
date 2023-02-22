package com.example.belapin;

import android.content.Context;
import android.graphics.Paint;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterBarangUser extends RecyclerView.Adapter<AdapterBarangUser.HolderBarangUser> implements Filterable {

    private Context context;
    public ArrayList<ModelBarang> barangList, filterList;
    private FilterBarangUser filter;

    public AdapterBarangUser(Context context, ArrayList<ModelBarang> barangList) {
        this.context = context;
        this.barangList = barangList;
        this.filterList = barangList;
    }

    @NonNull
    @Override
    public HolderBarangUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.baris_barang_user, parent, false);
        return new HolderBarangUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBarangUser holder, int position) {

        //get data
        ModelBarang modelBarang = barangList.get(position);
        String diskonTersedia = modelBarang.getDiskonTersedia();
        String diskonNote = modelBarang.getHargaDiskonNote();
        String hargaDiskon = modelBarang.getHargaDiskon();
        String barangKategori = modelBarang.getBarangKategori();
        String hargaAsli = modelBarang.getHargaAsli();
        String barangDeskripsi = modelBarang.getBarangDeskripsi();
        String barangJudul = modelBarang.getBarangJudul();
        String barangKuantiti = modelBarang.getBarangKuantiti();
        String barangId = modelBarang.getBarangId();
        String timestamp = modelBarang.getTimestamp();
        String barangIcon = modelBarang.getBarangIcon();

        // set data
        holder.judul.setText(barangJudul);
        holder.description.setText(barangDeskripsi);
        holder.hargaAsli.setText("Rp"+hargaAsli);

        if(diskonTersedia.equals("true")){
            // product diskon
            holder.hargaDiskon.setVisibility(View.VISIBLE);
            holder.hargaDiskonNote.setVisibility(View.VISIBLE);
            holder.hargaAsli.setPaintFlags(holder.hargaAsli.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // add strike through harga asli
        }
        else {
            holder.hargaDiskon.setVisibility(View.GONE);
            holder.hargaDiskonNote.setVisibility(View.GONE);
            holder.hargaAsli.setPaintFlags(0);
        }

        holder.tambahKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add barang to keranjang
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show barang detail
            }
        });

    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null) {
            filter = new FilterBarangUser(this, filterList);
        }
        return filter;
    }

    class HolderBarangUser extends RecyclerView.ViewHolder {

        // uid views
        private ImageView gambarBarang, next;
        private TextView hargaDiskonNote, judul, description, tambahKeranjang,
                hargaDiskon, hargaAsli;


        public HolderBarangUser(@NonNull View itemView) {

            super(itemView);

            // init views
            gambarBarang = itemView.findViewById(R.id.gambarBarang);
            next = itemView.findViewById(R.id.next);
            hargaDiskonNote = itemView.findViewById(R.id.hargaDiskonNote);
            judul = itemView.findViewById(R.id.judul);
            description = itemView.findViewById(R.id.description);
            tambahKeranjang = itemView.findViewById(R.id.tambahKeranjang);
            hargaDiskon = itemView.findViewById(R.id.hargaDiskon);
            hargaAsli = itemView.findViewById(R.id.hargaAsli);
        }



    }
}
