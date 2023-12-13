package app.santotomas.robot2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import app.santotomas.robot2.PlayActivity;
import app.santotomas.robot2.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnIniciar = findViewById(R.id.btnIniciar);

        btnIniciar.setOnClickListener(v -> {
            Intent intent=new Intent(MainActivity.this, DispositivosVinculadosActivity.class);
            startActivity(intent);
        });

    }
}