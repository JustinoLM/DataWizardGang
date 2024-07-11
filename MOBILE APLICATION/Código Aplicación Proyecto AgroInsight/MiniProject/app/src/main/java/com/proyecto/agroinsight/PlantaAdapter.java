package com.proyecto.agroinsight;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlantaAdapter extends RecyclerView.Adapter<PlantaAdapter.PlantaViewHolder> {

    private final Context context;
    private List<Planta> plantas;

    public PlantaAdapter(Context context, List<Planta> plantas) {
        this.context = context;
        this.plantas = plantas;
    }

    @NonNull
    @Override
    public PlantaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_planta, parent, false);
        return new PlantaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantaViewHolder holder, int position) {
        Planta planta = plantas.get(position);
        if (planta.getImagen() != null && planta.getImagen().length > 0) {
            holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(planta.getImagen(), 0, planta.getImagen().length));
        } else {
            holder.imageView.setImageResource(R.drawable.plantasagro);  // Imagen de recurso predeterminado si no hay imagen
        }
        holder.textViewNombre.setText(planta.getNombreComun());
        holder.textViewFecha.setText(planta.getFechaGuardado());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PlantasActivity.class);
            intent.putExtra("idPlanta", planta.getIdPlanta());
            intent.putExtra("nombreComun", planta.getNombreComun());
            intent.putExtra("imagen", planta.getImagen());
            intent.putExtra("fechaGuardado", planta.getFechaGuardado());
            intent.putExtra("tipoPlaga", planta.getTipoPlaga());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return plantas.size();
    }

    public static class PlantaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewNombre;
        TextView textViewFecha;

        public PlantaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
        }
    }

    // Método para actualizar la lista de plantas
    public void setPlantas(List<Planta> plantas) {
        this.plantas = plantas;
        notifyDataSetChanged();  // Notificar al adaptador que los datos han cambiado
    }
}

