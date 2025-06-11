package com.zenlauncher.sample;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTime;
    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Aplicar tema MindfulLauncher
        setTheme(R.style.MindfulLauncherTheme);
        
        initViews();
        updateDateTime();
    }

    private void initViews() {
        tvTime = findViewById(R.id.tv_time);
        tvDate = findViewById(R.id.tv_date);
    }

    private void updateDateTime() {
        // Atualizar horário
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        tvTime.setText(currentTime);

        // Atualizar data
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd 'de' MMMM", new Locale("pt", "BR"));
        String currentDate = dateFormat.format(new Date());
        tvDate.setText(currentDate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDateTime();
    }
}

