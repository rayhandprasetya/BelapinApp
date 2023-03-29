package com.example.belapin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterKeranjang extends RecyclerView.Adapter<AdapterKeranjang.HolderAdapterKeranjang> {

    private Context context;
    private ArrayList<ModelKeranjang> keranjang;

    public AdapterKeranjang(Context context, ArrayList<ModelKeranjang> keranjang) {
        this.context = context;
        this.keranjang = keranjang;
    }

    @NonNull
    @Override
    public HolderAdapterKeranjang onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout baris keranjang
        View view = LayoutInflater.from(context).inflate(R.layout.baris_keranjang_barang, parent, false);
        return new HolderAdapterKeranjang(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAdapterKeranjang holder, int position) {

        // get data
        ModelKeranjang modelKeranjang = keranjang.get(position);
        String id = modelKeranjang.getId();
        String getbId = modelKeranjang.getbId();
        String judul = modelKeranjang.getName();
        final String cost = modelKeranjang.getCost();
        String harga = modelKeranjang.getHarga();
        String kuantiti = modelKeranjang.getKuantiti();

        // set data
        holder.namaBarang.setText(""+judul);
        holder.hargaBarang.setText(""+cost);
        holder.hargaSatuan.setText(""+harga);
        holder.kuantitiBrgKeranjang.setText("["+kuantiti+"]");

        // delete barang from keranjang
        holder.hapusBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create table if not exist
                EasyDB easyDB = EasyDB.init(context, "DB_ITEMS")
                        .setTableName("TABLE_BARANG")
                        .addColumn(new Column("Barang_Id", new String[] {"text", "unique"}))
                        .addColumn(new Column("Barang_BID", new String[] {"text", "not null"}))
                        .addColumn(new Column("Barang_Nama", new String[] {"text", "not null"}))
                        .addColumn(new Column("Barang_HargaSatuan", new String[] {"text", "not null"}))
                        .addColumn(new Column("Barang_Harga", new String[] {"text", "not null"}))
                        .addColumn(new Column("Barang_Kuantiti", new String[] {"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, id);
                Toast.makeText(context, "Terhapus dari keranjang", Toast.LENGTH_SHORT).show();

                // refresh list
                keranjang.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

//                double tx = Double.parseDouble((((DetailPasar)context).totalSemua.getText().toString().trim().replace("Rp", "").replace(",", ".")));
//                double totalPrice = tx - Double.parseDouble((cost.replace("Rp", "").replace(",", ".")));
//                double totalPriceLain = Double.parseDouble(String.format("%.2f", totalPrice));
//                ((DetailPasar)context).totalSemuaHarga = 0.00;
//                ((DetailPasar)context).totalSemua.setText("Rp"+String.format("%.2f", totalPriceLain));


                double tx = Double.parseDouble((((DetailPasar)context).totalSemua.getText().toString().trim().replace(",", "").replace("Rp", "")));
                double totalPrice = tx - Double.parseDouble(cost.replace(",", "").replace("Rp", ""));
                double totalPriceLain = Double.parseDouble(String.format("%.2f", totalPrice).replace(",", "").replace("Rp", ""));
                ((DetailPasar)context).totalSemuaHarga = 0.00;
                ((DetailPasar)context).totalSemua.setText("Rp"+String.format("%.2f", totalPriceLain));

            }
        });

    }

    @Override
    public int getItemCount() {
        return keranjang.size(); // number of records
    }

    // view holder
    class HolderAdapterKeranjang extends RecyclerView.ViewHolder {

        // ui view for barus_keranjang_barang
        private TextView namaBarang, hargaBarang, hargaSatuan, kuantitiBrgKeranjang, hapusBarang;

        public HolderAdapterKeranjang(@NonNull View itemView) {
            super(itemView);

            // init views
            namaBarang = itemView.findViewById(R.id.namaBarang);
            hargaBarang = itemView.findViewById(R.id.hargaBarang);
            hargaSatuan = itemView.findViewById(R.id.hargaSatuan);
            kuantitiBrgKeranjang = itemView.findViewById(R.id.kuantitiBrgKeranjang);
            hapusBarang = itemView.findViewById(R.id.hapusBarang);
        }
    }
}
