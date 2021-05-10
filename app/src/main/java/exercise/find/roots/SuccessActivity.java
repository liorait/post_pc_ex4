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
    }
}
