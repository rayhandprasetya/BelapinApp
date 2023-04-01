package com.example.belapin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
//        String diskonTersedia = modelBarang.getDiskonTersedia();
//        String hargaDiskonNote = modelBarang.getHargaDiskonNote();
//        String hargaDiskon = modelBarang.getHargaDiskon();
        String hargaAsli = modelBarang.getHargaAsli();
        String kategoriBarang = modelBarang.getBarangKategori();
        String barangDeskripsi = modelBarang.getBarangDeskripsi();
        String gambar = modelBarang.getBarangIcon();
//        String kuantiti = modelBarang.getBarangKuantiti();
        String judul = modelBarang.getBarangJudul();
        String timestamp = modelBarang.getTimestamp();

        // set data
        holder.judul.setText((judul));
//        holder.kuantiti.setText(kuantiti);
//        holder.hargaDiskonNote.setText(hargaDiskonNote);
//        holder.hargaDiskon.setText("Rp"+hargaDiskon);
        holder.hargaAsli.setText("Rp" + hargaAsli);
//        if(diskonTersedia.equals("true")){
//            // product diskon
//            holder.hargaDiskon.setVisibility(View.VISIBLE);
//            holder.hargaDiskonNote.setVisibility(View.VISIBLE);
//            holder.hargaAsli.setPaintFlags(holder.hargaAsli.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // add strike through harga asli
//        }
//        holder.hargaDiskon.setVisibility(View.GONE);
//            holder.hargaDiskonNote.setVisibility(View.GONE);
        holder.hargaAsli.setPaintFlags(0);

        try {
            Glide.with(context)
                    .load(gambar)
                    .placeholder(R.drawable.ic_add)
                    .into(holder.gambarBarang);
        } catch (Exception e) {

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle item click, show item details (in bottom sheet)
                detailPage(modelBarang); // model barang contains of details of clicked barang
            }
        });


    }

    private void detailPage(ModelBarang modelBarang) {
        // botgom sheet
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        // inflate view for bottom sheet
        View view = LayoutInflater.from(context).inflate(R.layout.barang_detail_admin, null);
        bottomSheetDialog.setContentView(view);

        // init views of bottom sheet
        ImageButton tmblKembali = view.findViewById(R.id.tmblKembali);
        ImageButton tmblHapus = view.findViewById(R.id.tmblHapus);
        ImageButton tmblEdit = view.findViewById(R.id.tmblEdit);
//        ImageView gambarBarang = view.findViewById(R.id.gambarBarang);
//        TextView hargaDiskonNoteUI = view.findViewById(R.id.hargaDiskonNote);
        TextView judulUI = view.findViewById(R.id.judul);
        TextView deskripsi = view.findViewById(R.id.deskripsi);
        TextView kategori = view.findViewById(R.id.kategori);
//        TextView kuantitiUI = view.findViewById(R.id.kuantiti);
//        TextView hargaDiskonUI = view.findViewById(R.id.hargaDiskon);
        TextView hargaAsliUI = view.findViewById(R.id.hargaAsli);

        // get data
        String id = modelBarang.getBarangId();
        String uid = modelBarang.getUid();
//        String diskonTersedia = modelBarang.getDiskonTersedia();
//        String hargaDiskonNote = modelBarang.getHargaDiskonNote();
//        String hargaDiskon = modelBarang.getHargaDiskon();
        String hargaAsli = modelBarang.getHargaAsli();
        String kategoriBarang = modelBarang.getBarangKategori();
        String barangDeskripsi = modelBarang.getBarangDeskripsi();
        String gambar = modelBarang.getBarangIcon();
//        String kuantiti = modelBarang.getBarangKuantiti();
        String judul = modelBarang.getBarangJudul();
        String timestamp = modelBarang.getTimestamp();

        judulUI.setText(judul);
        deskripsi.setText(barangDeskripsi);
        kategori.setText(kategoriBarang);
//        kuantitiUI.setText(kuantiti);
//        hargaDiskonNoteUI.setText(hargaDiskonNote);
//        hargaDiskonUI.setText("Rp"+hargaDiskon);
        hargaAsliUI.setText("Rp" + hargaAsli);
//        if(diskonTersedia.equals("true")){
//            // product diskon
//            hargaDiskonUI.setVisibility(View.VISIBLE);
//            hargaDiskonNoteUI.setVisibility(View.VISIBLE);
//            hargaAsliUI.setPaintFlags(hargaAsliUI.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // add strike through harga asli
//        }
//        else {
//            hargaDiskonUI.setVisibility(View.GONE);
//            hargaDiskonNoteUI.setVisibility(View.GONE);
//
//        }

//        try {
//            Picasso.get().load(gambar).placeholder(R.drawable.ic_add).into(gambarBarang);
//        }
//        catch (Exception e) {
//            gambarBarang.setImageResource(R.drawable.ic_add);
//        }

        // show dialog
        bottomSheetDialog.show();

        // edit barang
        tmblEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

                // open edit barang class, pass id of barang
                Intent intent = new Intent(context, EditBarang.class);
                intent.putExtra("barangId", id);
                context.startActivity(intent);
            }
        });

        // delete barang
        tmblHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

                // show delete confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Removed").setMessage("Are you sure want to remove" + judul + " ?")
                        .setPositiveButton("HAPUS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // delete barang
                                deleteBarang(id); // id is barang id

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // cancel delete
                                dialogInterface.dismiss();

                            }
                        }).show();
            }
        });

        // back
        tmblKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dismiss bottom sheet
                bottomSheetDialog.dismiss();

            }
        });

    }

    private void deleteBarang(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Barang").
                child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // barang deleted
                        Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed delete barang
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
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
//            hargaDiskonNote = itemView.findViewById(R.id.hargaDiskonNote);
            judul = itemView.findViewById(R.id.judul);
//            kuantiti = itemView.findViewById(R.id.kuantiti);
//            hargaDiskon = itemView.findViewById(R.id.hargaDiskon);
            hargaAsli = itemView.findViewById(R.id.hargaAsli);
        }
    }
}
