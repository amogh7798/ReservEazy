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

public class customer {
    private String Customer_name;
    private String Customer_phone;
    private String Customer_email;
    private String customer_key;
    private Bitmap customer_pic;

    customer(String customer_key,String customer_name,String customer_email,String customer_phone,Bitmap customer_pic){
        this.Customer_email = customer_email;
        this.customer_key = customer_key;
        this.Customer_name = customer_name;
        this.customer_pic = customer_pic;
        this.Customer_phone = customer_phone;
    }

    public Bitmap getCustomer_pic() {
        return customer_pic;
    }

    public String getCustomer_email() {
        return Customer_email;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public String getCustomer_name() {
        return Customer_name;
    }

    public String getCustomer_phone() {
        return Customer_phone;
    }
}
