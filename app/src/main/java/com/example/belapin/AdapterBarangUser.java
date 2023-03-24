package com.example.belapin;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

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
        final ModelBarang modelBarang = barangList.get(position);
//        String diskonTersedia = modelBarang.getDiskonTersedia();
//        String diskonNote = modelBarang.getHargaDiskonNote();
//        String hargaDiskon = modelBarang.getHargaDiskon();
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

//        if(diskonTersedia.equals("true")){
//            // product diskon
//            holder.hargaDiskon.setVisibility(View.VISIBLE);
//            holder.hargaDiskonNote.setVisibility(View.VISIBLE);
//            holder.hargaAsli.setPaintFlags(holder.hargaAsli.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // add strike through harga asli
//        }
//        else {
//            holder.hargaDiskon.setVisibility(View.GONE);
//            holder.hargaDiskonNote.setVisibility(View.GONE);
//            holder.hargaAsli.setPaintFlags(0);
//        }

        holder.tambahKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add barang to keranjang
                showKuantiti(modelBarang);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show barang detail
            }
        });

    }

    private double total = 0;
    private double finalTotal = 0;
    private int kuantitiBrgApk = 0;

    private void showKuantiti(ModelBarang modelBarang) {
        // inflate layout for dialog Kuantiti
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_kuantiti, null);

        // ini layout view
        ImageView tambahGambar = view.findViewById(R.id.tambahGambar);
        TextView judul = view.findViewById(R.id.judul);
        TextView kuantiti = view.findViewById(R.id.kuantiti);
        TextView deskripsi = view.findViewById(R.id.deskripsi);
        TextView hargaAsli = view.findViewById(R.id.hargaAsli);
        TextView hargaTotal = view.findViewById(R.id.hargaTotal);
        ImageView tombolKurang = view.findViewById(R.id.tombolKurang);
        TextView kuantitiTambahKurang = view.findViewById(R.id.kuantitiTambahKurang);
        ImageView tombolTambah = view.findViewById(R.id.tombolTambah);
        Button tombolLanjut = view.findViewById(R.id.tombolLanjut);

        // get data from model
        String barangId = modelBarang.getBarangId();
        String judulBrg = modelBarang.getBarangJudul();
        String kuantitiBrg = modelBarang.getBarangKuantiti();
        String deskripsiBrg = modelBarang.getBarangDeskripsi();
        String image = modelBarang.getBarangIcon();
        String harga = modelBarang.getHargaAsli();

        total = Double.parseDouble(harga.replaceAll("Rp", ""));
        finalTotal = Double.parseDouble(harga.replaceAll("Rp", ""));
        kuantitiBrgApk = 1;

        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        // set data
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_add).into(tambahGambar);
        }
        catch (Exception e) {
            tambahGambar.setImageResource(R.drawable.ic_add);
        }

        judul.setText(""+judulBrg);
        kuantiti.setText(""+kuantitiBrg);
        deskripsi.setText(""+deskripsiBrg);
        kuantitiTambahKurang.setText(""+kuantitiBrgApk);
        hargaAsli.setText("Rp"+modelBarang.getHargaAsli());
        hargaTotal.setText("Rp"+finalTotal);

        AlertDialog dialog = builder.create();
        dialog.show();

        // add kuantiti
        tombolTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalTotal = finalTotal + total;
                kuantitiBrgApk++;

                hargaTotal.setText("Rp"+finalTotal);
                kuantitiTambahKurang.setText(""+kuantitiBrgApk);
            }
        });

        // decrease barang kuantiti only if kuantiti > 1
        tombolKurang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kuantitiBrgApk > 1) {
                    finalTotal = finalTotal - total;
                    kuantitiBrgApk--;

                    hargaTotal.setText("Rp"+finalTotal);
                    kuantitiTambahKurang.setText(""+kuantitiBrgApk);
                }
            }
        });

        tombolLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String judulL = judul.getText().toString().trim();
                String hargaSatuan = hargaAsli.getText().toString().trim().replace("Rp", "");
                String harga = hargaTotal.getText().toString().trim().replace("Rp", "");
                String kuantitiL = kuantiti.getText().toString().trim();

                // add to database (SQLite)
                tambahKeranjang(barangId, judulL, hargaSatuan, harga, kuantitiL);

                dialog.dismiss();

            }
        });

    }

    private int barangIdDB = 1;
    private void tambahKeranjang(String barangId, String judul, String hargaSatuan, String harga, String kuantitiL) {
        barangIdDB++;
        EasyDB easyDB = EasyDB.init(context, "DB_ITEMS")
                .setTableName("TABLE_BARANG")
                .addColumn(new Column("Barang_Id", new String[] {"text", "unique"}))
                .addColumn(new Column("Barang_BID", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_Nama", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_HargaSatuan", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_Harga", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_Kuantiti", new String[] {"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Barang_Id", barangIdDB)
                .addData("Barang_BID", barangId)
                .addData("Barang_Nama", judul)
                .addData("Barang_HargaSatuan", hargaSatuan)
                .addData("Barang_Harga", harga)
                .addData("Barang_Kuantiti", kuantitiL)
                .doneDataAdding();

        Toast.makeText(context, "Menambah ke keranjang...", Toast.LENGTH_SHORT).show();

        // update hitung keranjang
        ((DetailPasar)context).hitungKeranjang();

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
//            hargaDiskonNote = itemView.findViewById(R.id.hargaDiskonNote);
            judul = itemView.findViewById(R.id.judul);
            description = itemView.findViewById(R.id.description);
            tambahKeranjang = itemView.findViewById(R.id.tambahKeranjang);
//            hargaDiskon = itemView.findViewById(R.id.hargaDiskon);
            hargaAsli = itemView.findViewById(R.id.hargaAsli);
        }
    }
}
