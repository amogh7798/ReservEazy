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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class customer_adapter extends RecyclerView.Adapter<customer_adapter.customer_viewholder> {

    ArrayList<customer> customers;
    public class customer_viewholder extends RecyclerView.ViewHolder{

        ImageView customer_pic;
        TextView customer_name;
        TextView customer_email;
        TextView customer_phone;

        public customer_viewholder(@NonNull View itemView) {
            super(itemView);
            customer_pic = itemView.findViewById(R.id.customer_pic);
            customer_name = itemView.findViewById(R.id.customer_name);
            customer_email = itemView.findViewById(R.id.customer_email);
            customer_phone = itemView.findViewById(R.id.customer_phone);
        }
    }

    customer_adapter(ArrayList<customer> customers){
        this.customers = customers;
    }

    @NonNull
    @Override
    public customer_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_card,parent,false);
        customer_viewholder vh = new customer_viewholder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull customer_viewholder holder, int position) {
        customer current_customer = customers.get(position);

        if(current_customer.getCustomer_pic()!=null) {
            holder.customer_pic.setImageBitmap(current_customer.getCustomer_pic());
        }
        holder.customer_phone.setText(current_customer.getCustomer_phone());
        holder.customer_email.setText(current_customer.getCustomer_email());
        holder.customer_name.setText(current_customer.getCustomer_name());
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }
}
