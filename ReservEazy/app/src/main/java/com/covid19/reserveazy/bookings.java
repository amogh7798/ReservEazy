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

public class bookings {
    private String shop_key;
    private String shop_name;
    private Bitmap shop_pic;
    private String shop_area;
    private String shop_phone;
    private String shop_timings;
    private String date;
    private String slot;

    bookings(String shop_key,String shop_name,Bitmap shop_pic,String shop_timings,String shop_area,String shop_phone,String slot,String date){
        this.shop_name = shop_name;
        this.shop_pic = shop_pic;
        this.shop_area = shop_area;
        this.shop_phone = shop_phone;
        this.shop_timings = shop_timings;
        this.slot = slot;
        this.date = date;
        this.shop_key = shop_key;
    }

    public String getShop_key() {
        return shop_key;
    }

    public String getDate() {
        return date;
    }

    public String getShop_timings() {
        return shop_timings;
    }

    public String getShop_name() {
        return shop_name;
    }

    public String getShop_area() {
        return shop_area;
    }

    public Bitmap getShop_pic() {
        return shop_pic;
    }

    public String getShop_phone() {
        return shop_phone;
    }

    public String getSlot() {
        return slot;
    }
}
