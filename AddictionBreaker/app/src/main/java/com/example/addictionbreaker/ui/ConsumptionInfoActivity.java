package com.example.addictionbreaker.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addictionbreaker.R;
import com.example.addictionbreaker.data.DatabaseHelper;
import com.example.addictionbreaker.model.User;
import com.google.gson.Gson;

import java.util.Calendar;
//a comment

public class ConsumptionInfoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DatabaseHelper myDb;
    private String string1;
    private String string2;
    private TextView consumption_info_how_frequent  ;
    private TextView consumption_info_cost;
    private TextView startDateText;
    private boolean isBlank = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption_info);
        myDb = new DatabaseHelper(this);
        consumption_info_how_frequent = (TextView)findViewById(R.id.consumption_info_how_frequent);
        consumption_info_cost = (TextView)findViewById(R.id.consumption_info_cost);
        startDateText = findViewById(R.id.start_date);
        final EditText averageConsumption = findViewById(R.id.averageConsumption);
        final EditText averageCost = findViewById(R.id.averageCost);
        final Button letsGo = findViewById(R.id.letsGo);
        Button startDate = findViewById(R.id.start_date_button);

        //retrieve user info
        final SharedPreferences myPrefs = this.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        String info = myPrefs.getString("userInfo", null);
        final User user = gson.fromJson(info , User.class);
        setStrings(user);

        Resources res = getResources();
        String how_frequent = String.format(res.getString(R.string.consumption_info_how_frequent), string1);
        String cost = String.format(res.getString(R.string.consumption_info_cost),string2);
        consumption_info_how_frequent.setText(how_frequent);
        consumption_info_cost.setText(cost);


        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDateText.getText().toString().isEmpty();
                if(averageConsumption.getText().toString().isEmpty() || averageCost.getText().toString().isEmpty() || startDateText.getText().toString().isEmpty()){
                    isBlank = alertMessage(letsGo);
                }
                else{
                    isBlank = false;
                }
                if(!isBlank){
                    String newInfo = gson.toJson(user);
                    myPrefs.edit().putString("userInfo", newInfo).commit();
                    boolean isInserted = myDb.insertData(user.getName(), Integer.toString(user.getAge()), user.getAddiction(),Integer.toString(user.getConsumption()),Integer.toString(user.getCostOfAddiction()));
                    if(isInserted){
                        Toast.makeText(ConsumptionInfoActivity.this, "Data inserted", Toast.LENGTH_LONG).show();
                    }
                        Intent intent = new Intent(ConsumptionInfoActivity.this, AdictionList.class);
                        startActivity(intent);
                    }
                }
            });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

    }
    private void setStrings(User user) {
        if (user.getAddiction().equals("Cigarettes")){
            string1 = "cigarettes";
            string2 = "a pack of cigarettes";
        }
        else if(user.getAddiction().equals("Alcohol")){
            string1 = "drinks";
            string2 = "a day of drinking";
        }
        else if(user.getAddiction().equals("Vaping/Juuling")){
            string1 = "pod";
            string2 = "a pod";
        }
        else{
            string1 = "cigarettes";
            string2 = "a pack of cigarettes";
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String date = "Start Date: " + month + "/" + day + "/" + year;
        startDateText.setText(date);
    }

    private boolean alertMessage(Button letsGo){
        AlertDialog a = new AlertDialog.Builder(letsGo.getContext()).create();
        a.setTitle("Missing/Blank Fields!");
        a.setMessage("Make sure you pick a start date and fill in the consumption info");
        a.show();
        return true;
    }
}
