package exercise.find.roots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SuccessActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        // Gets the passed data using the intent
        Intent intent = getIntent();
        if (intent.hasExtra("number_of_roots")){
            String numberRoots = intent.getStringExtra("number_of_roots");
            TextView success = findViewById(R.id.textViewSuccess);
            success.setText(numberRoots);
        }
        if (intent.hasExtra("original_number")){
            String original_number_str = intent.getStringExtra("original_number");
            String root1 = intent.getStringExtra("root1");
            String root2 = intent.getStringExtra("root2");
            String calculation_time = intent.getStringExtra("calculation_time");
            TextView success = findViewById(R.id.textViewSuccess);
            String textToSet = "original_number is: " + original_number_str + " root1: " + root1 + " root2: " + root2 +
                    " calculation_time: " + calculation_time + "ml";
            success.setText(textToSet);

        }
    }
}
