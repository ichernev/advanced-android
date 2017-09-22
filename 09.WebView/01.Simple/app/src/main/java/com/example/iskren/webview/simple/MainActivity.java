package com.example.iskren.webview.simple;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.webview_url);
        ((Button) findViewById(R.id.webview_start)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goButtonClicked();
            }
        });
    }

    private void goButtonClicked() {
        String url = ((EditText) findViewById(R.id.webview_url)).getText().toString();
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(WebActivity.EXTRA_KEY_URL, url);
        startActivity(intent);
    }
}
