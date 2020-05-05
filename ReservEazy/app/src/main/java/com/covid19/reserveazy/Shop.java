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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class Shop implements Serializable {
    private String user_key;
    private Bitmap shop_pic;
    private String shop_name;
    private String Owner_name;
    private String shop_License;
    private String shop_area;
    private String shop_phone;
    private String shop_category;
    private String shop_timings;
    private String shop_location;
    private String shop_whatsapp;
    private String shop_slot;

    Shop(String user_key,Bitmap shop_pic,String shop_name,String Owner_name,String shop_License,String shop_area,String shop_category,String shop_phone,String shop_timings,String shop_location,String shop_whatsapp,String shop_slot){
        this.user_key = user_key;
        this.shop_pic = shop_pic;
        this.shop_name = shop_name;
        this.Owner_name = Owner_name;
        this.shop_License = shop_License;
        this.shop_area = shop_area;
        this.shop_phone = shop_phone;
        this.shop_category = shop_category;
        this.shop_timings = shop_timings;
        this.shop_location = shop_location;
        this.shop_whatsapp = shop_whatsapp;
        this.shop_slot = shop_slot;
    }

    public String getUser_key() {
        return user_key;
    }

    public String getOwner_name() {
        return Owner_name;
    }

    public Bitmap getShop_pic() {
        return shop_pic;
    }

    public String getShop_phone() {
        return shop_phone;
    }

    public String getShop_area() {
        return shop_area;
    }

    public String getShop_category() {
        return shop_category;
    }

    public String getShop_location() {
        return shop_location;
    }

    public String getShop_License() {
        return shop_License;
    }

    public String getShop_name() {
        return shop_name;
    }

    public String getShop_timings() {
        return shop_timings;
    }

    public String getShop_slot() {
        return shop_slot;
    }

    public String getShop_whatsapp() {
        return shop_whatsapp;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        this.shop_pic.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        out.writeInt(byteArray.length);
        out.write(byteArray);

    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException, IOException {


        int bufferLength = in.readInt();

        byte[] byteArray = new byte[bufferLength];

        int pos = 0;
        do {
            int read = in.read(byteArray, pos, bufferLength - pos);

            if (read != -1) {
                pos += read;
            } else {
                break;
            }

        } while (pos < bufferLength);

        this.shop_pic = BitmapFactory.decodeByteArray(byteArray, 0, bufferLength);

    }
}

