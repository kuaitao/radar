package com.example.radarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

//import com.example.myradarlibrary.RadarData;
//import com.example.myradarlibrary.RadarView;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//
//        RadarView radarView = (RadarView) findViewById(R.id.radarView);
//
//        List<RadarData> dataList = new ArrayList();
//        for (int i = 1; i < 7; i++) {
//            RadarData data = new RadarData("标题" + i, i * 11,i * 11+10);
//            dataList.add(data);
//        }
//        radarView.setDataList(dataList);
//        radarView.setOnItemTextClick(new RadarView.OnItemClickListner() {
//            @Override
//            public void onClick(int index, RadarData radarData) {
//                Toast.makeText(MainActivity.this, "" + radarData.getTitle(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }


}
