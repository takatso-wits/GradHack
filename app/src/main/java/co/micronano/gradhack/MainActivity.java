package co.micronano.gradhack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button btnPharm, locateGP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPharm = (Button)findViewById(R.id.btnPharm);
        locateGP = (Button)findViewById(R.id.btnGp);


        btnPharm.setOnClickListener(this);
        locateGP.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if(v == btnPharm){
            startActivity(new Intent(MainActivity.this,LocatePharm.class));
        }
        if(v == locateGP){
            startActivity(new Intent(MainActivity.this, LocateGP.class));
        }
    }
}