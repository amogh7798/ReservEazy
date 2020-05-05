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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class booking_adapter extends RecyclerView.Adapter<booking_adapter.booking_viewholder> {
    ArrayList<bookings> book;
    SelectedBooking selectedBooking;

    public class booking_viewholder extends RecyclerView.ViewHolder{

        ImageView shop_pic;
        TextView shop_name;
        TextView shop_area;
        TextView shop_timings;
        TextView shop_phone;
        TextView slot;
        TextView date;
        Button cancel;

        public booking_viewholder(@NonNull View itemView) {
            super(itemView);
            shop_pic = itemView.findViewById(R.id.book_pic);
            shop_name = itemView.findViewById(R.id.book_name);
            shop_area = itemView.findViewById(R.id.book_area);
            shop_phone = itemView.findViewById(R.id.book_phone);
            shop_timings = itemView.findViewById(R.id.book_timings);
            slot = itemView.findViewById(R.id.book_slot);
            date = itemView.findViewById(R.id.book_date);
            cancel = itemView.findViewById(R.id.book_cancel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel.setVisibility(View.VISIBLE);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                              selectedBooking.selectedBooking(slot.getText().toString(),date.getText().toString(),book.get(getAdapterPosition()).getShop_key());
                        }
                    });
                }
            });
        }
    }

    public interface SelectedBooking{
        public void selectedBooking(String slot,String date,String merchant_key);
    }
    booking_adapter(ArrayList<bookings> booking,SelectedBooking selectedBooking){
        this.selectedBooking = selectedBooking;
        this.book = booking;
    }
    @NonNull
    @Override
    public booking_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_card,parent,false);
        booking_viewholder vh = new booking_viewholder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull booking_viewholder holder, int position) {
        bookings current_booking = book.get(position);

        if (current_booking.getShop_pic() != null) {
            holder.shop_pic.setImageBitmap(current_booking.getShop_pic());
        }
        holder.date.setText(current_booking.getDate());
        holder.slot.setText(current_booking.getSlot());
        holder.shop_name.setText(current_booking.getShop_name());
        holder.shop_area.setText(current_booking.getShop_area());
        holder.shop_timings.setText(current_booking.getShop_timings());
        holder.shop_phone.setText(current_booking.getShop_phone());
    }

    @Override
    public int getItemCount() {
        return book.size();
    }
}
