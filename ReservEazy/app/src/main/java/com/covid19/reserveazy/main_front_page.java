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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.concurrent.TimeUnit;

public class main_front_page extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_front_page);

        if(ActivityCompat.checkSelfPermission(main_front_page.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(main_front_page.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
        }

        if(ActivityCompat.checkSelfPermission(main_front_page.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(main_front_page.this, new String[]{Manifest.permission.INTERNET}, 200);
        }

        try {
            TimeUnit.SECONDS.sleep(5);
            startActivity(new Intent(main_front_page.this,Front_Activity.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
