//package com.example.covidcare;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.NumberPicker;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class StatusActivity extends AppCompatActivity {
//
//    private static final String TAG = "StatusActivity";
//
//    public static final String EXTRA_NEW_INDEX = "com.example.covidcare.StatusActivity.EXTRA_NEW_INDEX";
//
//
//    private NumberPicker numberPicker;
//    private Button btnUpdate;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_status);
//
//        Intent intent = getIntent();
//        int index = intent.getIntExtra(MainActivity.EXTRA_INDEX, 0);
//
//        setTitle("Update Status");
//
//        numberPicker = (NumberPicker) findViewById(R.id.status_picker);
//        numberPicker.setMinValue(0);
//        numberPicker.setMaxValue(3);
//
//        numberPicker.setDisplayedValues(new String[]{"don't know", "Health", "Infected", "treated"});
//        numberPicker.setValue(index);
//
//        numberPicker.setDefaultFocusHighlightEnabled(true);
//
//        btnUpdate = (Button) findViewById(R.id.btnUpdate);
//
//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int new_index = numberPicker.getValue();
//                Intent data = new Intent();
//                data.putExtra(EXTRA_NEW_INDEX, new_index);
//                setResult(RESULT_OK, data);
//                finish();
//
//            }
//        });
//
//
//    }
//
//
//}
