package exercise.find.roots;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

  private BroadcastReceiver broadcastReceiverForSuccess = null;
  private BroadcastReceiver broadcastReceiverForFailure = null;
  private boolean isWaitingForCalculation = false;

  protected static boolean isNumeric(String string){
      try {
        for (int i = 0; i < string.length(); i++){
          char current = string.charAt(i);
          // '0'=48, '9'=57
          if (current < 48 || current > 57){
            return false;
          }
        }
        return true;
      }
      catch (RuntimeException e){
        System.out.println("too long");
      }
      return false;
    }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ProgressBar progressBar = findViewById(R.id.progressBar);
    EditText editTextUserInput = findViewById(R.id.editTextInputNumber);
    Button buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

    // set initial UI:
    progressBar.setVisibility(View.GONE); // hide progress
    editTextUserInput.setText(""); // cleanup text in edit-text
    editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
    buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

    // set listener on the input written by the keyboard to the edit-text
    editTextUserInput.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
      public void onTextChanged(CharSequence s, int start, int before, int count) { }
      public void afterTextChanged(Editable s) {
        // text did change
        String newText = editTextUserInput.getText().toString();
        // check if a calculation is running in the background
        // if yes, button disabled, otherwise, button is enabled
        if (!isWaitingForCalculation){
          buttonCalculateRoots.setEnabled(true);
        }
      }
    });

    // set click-listener to the button
    buttonCalculateRoots.setOnClickListener(v -> {
      Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
      String userInputString = editTextUserInput.getText().toString();

      // todo: check that `userInputString` is a number. handle bad input. convert `userInputString` to long
      if (!isNumeric(userInputString)){
       //System.out.println("String is not numeric");
       return;
      }
      long userInputLong = 0;
      try {
        userInputLong = Long.parseLong(userInputString);
      }
      catch (NumberFormatException e){
        Toast.makeText(this, "calculation aborted after 20 seconds", Toast.LENGTH_SHORT).show();
        return;
      }

      //long userInputLong = 0; // this should be the converted string from the user
      intentToOpenService.putExtra("number_for_service", userInputLong);
      startService(intentToOpenService);
      isWaitingForCalculation = true;
      // set views states according to the spec (below)

      // set views states after pressing the button and while the calculation didn't finish
      progressBar.setVisibility(View.VISIBLE); // show progress bar
      //editTextUserInput.setText(""); // cleanup text in edit-text
      editTextUserInput.setEnabled(false); // set edit-text as disabled (user can't input text)
      buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)
    });

    // register a broadcast-receiver to handle action "found_roots"
    broadcastReceiverForSuccess = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("found_roots")) return;
        // success finding roots!
        /*
         TODO: handle "roots-found" as defined in the spec (below).
          also:
           - the service found roots and passed them to you in the `incomingIntent`. extract them.
           - when creating an intent to open the new-activity, pass the roots as extras to the new-activity intent
             (see for example how did we pass an extra when starting the calculation-service)
         */
        isWaitingForCalculation = false;
        Intent successIntent = new Intent(MainActivity.this, SuccessActivity.class);
        Long original_number = incomingIntent.getLongExtra("original_number", 0);
        String original_number_str = Long.toString(original_number);
        String root1_str = Long.toString(incomingIntent.getLongExtra("root1", 0));
        String root2_str = Long.toString(incomingIntent.getLongExtra("root2", 0));
        String calculation_time_str = Long.toString(incomingIntent.getLongExtra("calculation_time", 0));

        successIntent.putExtra("original_number", original_number_str);
        successIntent.putExtra("root1", root1_str);
        successIntent.putExtra("root2", root2_str);
        successIntent.putExtra("calculation_time", calculation_time_str);
        startActivity(successIntent);

        // set UI:
        progressBar.setVisibility(View.GONE); // hide progress
        editTextUserInput.setText(""); // cleanup text in edit-text
        editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
        buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)
      }
    };
    registerReceiver(broadcastReceiverForSuccess, new IntentFilter("found_roots"));

    /*
    todo:
     add a broadcast-receiver to listen for abort-calculating as defined in the spec (below)
     to show a Toast, use this code:
     `Toast.makeText(this, "text goes here", Toast.LENGTH_SHORT).show()`
     */
    broadcastReceiverForFailure = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("stopped_calculations")) return;
        String textToToast = "calculation aborted after " + incomingIntent.getLongExtra("time_until_give_up_seconds", 0) + " seconds";
        Toast.makeText(context, textToToast, Toast.LENGTH_SHORT).show();

        // set UI:
        isWaitingForCalculation = false;
        progressBar.setVisibility(View.GONE); // hide progress
        editTextUserInput.setText(""); // cleanup text in edit-text
        editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
        buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)
      }
    };
    registerReceiver(broadcastReceiverForFailure, new IntentFilter("stopped_calculations"));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    //  remove ALL broadcast receivers we registered earlier in onCreate().
    //  to remove a registered receiver, call method `this.unregisterReceiver(<receiver-to-remove>)`
    this.unregisterReceiver(broadcastReceiverForSuccess);
    this.unregisterReceiver(broadcastReceiverForFailure);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    // put relevant data into bundle as you see fit
    outState.putSerializable("state", this.saveState());
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    // load data from bundle and set screen state (see spec below)
    Serializable saved_output = savedInstanceState.getSerializable("state");
    MainActivity typo = new MainActivity();
    typo.loadState(saved_output);

    TextView textView = findViewById(R.id.editTextInputNumber);
    textView.setText(((MainActivityState) saved_output).valueInText);
  }

  public Serializable saveState() {
    // insert all data to the state, so in the future we can load from this state
    MainActivityState state = new MainActivityState();
    state.isWaitingForCalculation = isWaitingForCalculation;
    //if (isWaitingForCalculation) {
    TextView textView = findViewById(R.id.editTextInputNumber);
    String val = textView.getText().toString();
    state.valueInText = val;
    return state;
  }

  public void loadState(Serializable prevState) {
    if (!(prevState instanceof MainActivityState)) {
      return; // ignore
    }
    MainActivityState casted = (MainActivityState) prevState;
    this.isWaitingForCalculation = casted.isWaitingForCalculation;

    TextView textView = findViewById(R.id.editTextInputNumber);
    textView.setText(((MainActivityState) prevState).valueInText);
  }

  private static class MainActivityState implements Serializable {
    private String valueInText;
    private boolean isWaitingForCalculation;
  }
}


/*

TODO:
the spec is:

upon launch, Activity starts out "clean":
* progress-bar is hidden
* "input" edit-text has no input and it is enabled
* "calculate roots" button is disabled

the button behavior is:
* when there is no valid-number as an input in the edit-text, button is disabled
* when we triggered a calculation and still didn't get any result, button is disabled
* otherwise (valid number && not calculating anything in the BG), button is enabled

the edit-text behavior is:
* when there is a calculation in the BG, edit-text is disabled (user can't input anything)
* otherwise (not calculating anything in the BG), edit-text is enabled (user can tap to open the keyboard and add input)

the progress behavior is:
* when there is a calculation in the BG, progress is showing
* otherwise (not calculating anything in the BG), progress is hidden

when "calculate roots" button is clicked:
* change states for the progress, edit-text and button as needed, so user can't interact with the screen

when calculation is complete successfully:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* open a new "success" screen showing the following data:
  - the original input number
  - 2 roots combining this number (e.g. if the input was 99 then you can show "99=9*11" or "99=3*33"
  - calculation time in seconds

when calculation is aborted as it took too much time:
* change states for the progress, edit-text and button as needed, so the screen can accept new input
* show a toast "calculation aborted after X seconds"


upon screen rotation (saveState && loadState) the new screen should show exactly the same state as the old screen. this means:
* edit-text shows the same input
* edit-text is disabled/enabled based on current "is waiting for calculation?" state
* progress is showing/hidden based on current "is waiting for calculation?" state
* button is enabled/disabled based on current "is waiting for calculation?" state && there is a valid number in the edit-text input


 */