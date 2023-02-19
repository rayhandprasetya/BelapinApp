package com.example.belapin;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterBarangAdmin extends RecyclerView.Adapter<AdapterBarangAdmin.HolderBarangAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelBarang> barangList, filterList;
    private FilterBarang filter;

    public AdapterBarangAdmin(Context context, ArrayList<ModelBarang> barangList) {
        this.context = context;
        this.barangList = barangList;
        this.filterList = barangList;
    }

    @NonNull
    @Override
    public HolderBarangAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.baris_barang_admin, parent, false);
        return new HolderBarangAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBarangAdmin holder, int position) {
        // get data
        ModelBarang modelBarang = barangList.get(position);
        String id = modelBarang.getBarangId();
        String uid = modelBarang.getUid();
        String diskonTersedia = modelBarang.getDiskonTersedia();
        String hargaDiskonNote = modelBarang.getHargaDiskonNote();
        String hargaDiskon = modelBarang.getHargaDiskon();
        String hargaAsli = modelBarang.getHargaAsli();
        String kategoriBarang = modelBarang.getBarangKategori();
        String barangDeskripsi = modelBarang.getBarangDeskripsi();
        String gambar = modelBarang.getBarangIcon();
        String kuantiti = modelBarang.getBarangKuantiti();
        String judul = modelBarang.getBarangJudul();
        String timestamp = modelBarang.getTimestamp();

        // set data
        holder.judul.setText((judul));
        holder.kuantiti.setText(kuantiti);
        holder.hargaDiskonNote.setText(hargaDiskonNote);
        holder.hargaDiskon.setText("Rp"+hargaDiskon);
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
        }

        try {
            Picasso.get().load(gambar).placeholder(R.drawable.ic_add).into(holder.gambarBarang);
        }
        catch (Exception e) {
            holder.gambarBarang.setImageResource(R.drawable.ic_add);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle item click, show item details
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
            filter = new FilterBarang(this, filterList);
        }
        return filter;
    }

    class HolderBarangAdmin extends RecyclerView.ViewHolder {

        private ImageView gambarBarang;
        private TextView hargaDiskonNote, judul, kuantiti, hargaDiskon, hargaAsli;

        // hold view of recyclerview
        public HolderBarangAdmin(@NonNull View itemView) {
            super(itemView);

            gambarBarang = itemView.findViewById(R.id.gambarBarang);
            hargaDiskonNote = itemView.findViewById(R.id.hargaDiskonNote);
            judul = itemView.findViewById(R.id.judul);
            kuantiti = itemView.findViewById(R.id.kuantiti);
            hargaDiskon = itemView.findViewById(R.id.hargaDiskon);
            hargaAsli = itemView.findViewById(R.id.hargaAsli);
        }
    }
}
