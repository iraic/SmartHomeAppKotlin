package com.example.smarthomeappkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(var dataset: Array<Array<String?>>?, r: RecyclerViewOnItemClickListener) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    private val r: RecyclerViewOnItemClickListener

    class ViewHolder(itemView: View, r: RecyclerViewOnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        var tvId: TextView
        var tvUser: TextView
        var tvTipo: TextView
        var tvValor: TextView
        var tvFecha: TextView
        var ivEdit: ImageView
        var ivDelete: ImageView

        init {
            tvId = itemView.findViewById(R.id.tvId)
            tvUser = itemView.findViewById(R.id.tvUser)
            tvTipo = itemView.findViewById(R.id.tvTipo)
            tvValor = itemView.findViewById(R.id.tvValor)
            tvFecha = itemView.findViewById(R.id.tvFecha)
            ivEdit = itemView.findViewById(R.id.ivEdit)
            ivDelete = itemView.findViewById(R.id.ivDelete)
            itemView.setOnClickListener { view -> r.onClick(view, adapterPosition) }
            ivEdit.setOnClickListener { view -> r.onClickEdit(view, adapterPosition) }
            ivDelete.setOnClickListener { view -> r.onClickDel(view, adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_sensores, parent, false)
        return ViewHolder(view, r)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvId.text = dataset!![position][0]
        holder.tvUser.text = dataset!![position][1]
        holder.tvTipo.text = dataset!![position][2]
        holder.tvValor.text = dataset!![position][3]
        holder.tvFecha.text = dataset!![position][4]
    }

    override fun getItemCount(): Int {
        return dataset!!.size
    }

    init {
        this.r = r
    }
}