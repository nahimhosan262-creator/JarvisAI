package com.jarvis.ai;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(Color.rgb(8, 15, 30));

        TextView title = new TextView(this);
        title.setText("JARVIS");
        title.setTextSize(38);
        title.setTextColor(Color.rgb(0, 220, 255));
        title.setGravity(Gravity.CENTER);

        TextView status = new TextView(this);
        status.setText("AI ASSISTANT\n\nSystem initialized.\nWelcome, Sir!");
        status.setTextSize(18);
        status.setTextColor(Color.WHITE);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 24, 0, 0);

        layout.addView(title);
        layout.addView(status);
        setContentView(layout);
    }
}
