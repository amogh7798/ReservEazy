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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class order_cancel extends Activity {

    Button home;
    String customer_key;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel);

        home = (Button) findViewById(R.id.o_c_home);

        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            customer_key = (String)intent.getSerializableExtra("key");
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(order_cancel.this,customer_home.class).putExtra("key",customer_key));
            }
        });
    }
}
