package zfr.mobile.projectpapb;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolder>{

    private Context context;
    private List<User> list;
    private OnItemClickListener listener;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AdapterUser(Context context, List<User> list){
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user,parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);
        holder.namaPenerima.setText(user.getNamaPenerima());
        holder.namaPengirim.setText(user.getNamaPengirim());
        holder.alamatPenerima.setText(user.getAlamatPenerima());
        holder.jenisPengiriman.setText(user.getJenisPengiriman());
        Glide.with(context).load(user.getBuktifoto()).into(holder.buktifoto);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView namaPenerima, namaPengirim, alamatPenerima, jenisPengiriman, urlGambar;
        ImageView buktifoto;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            namaPenerima = itemView.findViewById(R.id.nama_penerima);
            namaPengirim = itemView.findViewById(R.id.nama_pengirim);
            alamatPenerima = itemView.findViewById(R.id.Alamat_penerima);
            jenisPengiriman = itemView.findViewById(R.id.Jenis_pengiriman);
            buktifoto = itemView.findViewById(R.id.buktifoto);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }
}