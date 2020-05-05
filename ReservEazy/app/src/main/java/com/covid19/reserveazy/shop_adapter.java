/* This file is part of ReservEazy.

    ReservEazy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ReservEazy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ReservEazy.  If not, see <https://www.gnu.org/licenses/>.*/

package com.covid19.reserveazy;

import android.util.Log;
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

public class shop_adapter extends RecyclerView.Adapter<shop_adapter.shop_viewholder> implements Filterable {

    public ArrayList<Shop> shops;
    public ArrayList<Shop> shops_full;

    SelectedShop selectedShop;

    @Override
    public Filter getFilter() {
        return shop_filter;
    }

    public class shop_viewholder extends RecyclerView.ViewHolder{
        ImageView shop_pic;
        TextView shop_name;
        TextView shop_area;
        TextView shop_timings;
        TextView phone;
        public shop_viewholder(@NonNull View itemView) {
            super(itemView);
            this.shop_pic = itemView.findViewById(R.id.shop_pic);
            this.shop_area = itemView.findViewById(R.id.shop_area);
            this.shop_name = itemView.findViewById(R.id.shop_name);
            this.shop_timings = itemView.findViewById(R.id.shop_timings);
            this.phone = itemView.findViewById(R.id.shop_phone);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedShop.selectedShop(shops.get(getAdapterPosition()).getUser_key());
                }
            });
        }
    }

    public interface SelectedShop{
        void selectedShop(String user_key);
    }

    public shop_adapter(ArrayList<Shop> shop,SelectedShop selectedShop)
    {
        this.shops = new ArrayList<>(shop);
        this.selectedShop = selectedShop;
        this.shops_full = new ArrayList<>(shop);
    }

    @NonNull
    @Override
    public shop_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_card,parent,false);
        shop_viewholder vh = new shop_viewholder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull shop_viewholder holder, int position) {
        Shop currentItem = this.shops.get(position);

        if (currentItem.getShop_pic() != null) {
            holder.shop_pic.setImageBitmap(currentItem.getShop_pic());
        }
         holder.shop_name.setText(currentItem.getShop_name());
         holder.shop_area.setText(currentItem.getShop_area());
         holder.shop_timings.setText(currentItem.getShop_timings());
         holder.phone.setText(currentItem.getShop_phone());
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }


    private Filter shop_filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Shop> shopFilter = new ArrayList<>();
            if(constraint==null || constraint.length()==0){
                shopFilter.addAll(shops_full);
            }
            else{
                String filter_pattern = constraint.toString().toLowerCase().trim();
                for(Shop s:shops_full){
                    if(s.getShop_name().toLowerCase().trim().contains(filter_pattern)){
                        shopFilter.add(s);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = shopFilter;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
           shops.clear();
           shops.addAll((ArrayList<Shop>)results.values);
           notifyDataSetChanged();
        }
    };
}
