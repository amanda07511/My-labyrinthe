package iut.my_labyrinthe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button level1 = (Button) findViewById(R.id.button);
        Button level2 = (Button) findViewById(R.id.button2);
        Button level3 = (Button) findViewById(R.id.button3);

        level1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LabyrintheActivity.class);
                i.putExtra("file", "nivel1.txt");
                startActivity(i);
                finish();
            }
        });
        level2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LabyrintheActivity.class);
                i.putExtra("file", "nivel2.txt");
                startActivity(i);
                finish();
            }
        });
        level3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LabyrintheActivity.class);
                i.putExtra("file", "nivel3.txt");
                startActivity(i);
                finish();
            }
        });



    }
}
