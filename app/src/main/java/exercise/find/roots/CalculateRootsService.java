package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

public class CalculateRootsService extends IntentService {

  boolean isFinishedCalculation = false;
  //ArrayList<Integer> roots = new ArrayList<>();
  //int count = 0; // number of roots
  public CalculateRootsService() {
    super("CalculateRootsService");
  }

  protected Pair<Long, Long> calculateRoots(long numberToCalculateRootsFor){
    long timeStartMs = System.currentTimeMillis();
    long endTime = timeStartMs + 20000L;
    System.out.println(timeStartMs);
    System.out.println("here " + numberToCalculateRootsFor);
    int i = 2;
    long currentTimeAfter = System.currentTimeMillis();

    while ((i <= numberToCalculateRootsFor / 2) && (currentTimeAfter < endTime)){
      long root = (long) (numberToCalculateRootsFor / i);
      if (numberToCalculateRootsFor % i == 0) {
        Long j = (long) (numberToCalculateRootsFor / root);
        Pair<Long, Long> newPair = new Pair<>(j, root);
      //  roots.add(root);
      //  count++;
        isFinishedCalculation = true;
        return newPair;
      }
      currentTimeAfter = System.currentTimeMillis();
      i++;
    }
    return null;
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent == null) return;
    //long timeStartMs = System.currentTimeMillis();
    long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);

    System.out.println("got the number: " + numberToCalculateRootsFor); //todo remove

    if (numberToCalculateRootsFor <= 0) {
      Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
      return;
    }
    /*
    TODO:
     calculate the roots.
     check the time (using `System.currentTimeMillis()`) and stop calculations if can't find an answer after 20 seconds
     upon success (found a root, or found that the input number is prime):
      send broadcast with action "found_roots" and with extras:
       - "original_number"(long)
       - "root1"(long)
       - "root2"(long)
     upon failure (giving up after 20 seconds without an answer):
      send broadcast with action "stopped_calculations" and with extras:
       - "original_number"(long)
       - "time_until_give_up_seconds"(long) the time we tried calculating

      examples:
       for input "33", roots are (3, 11)
       for input "30", roots can be (3, 10) or (2, 15) or other options
       for input "17", roots are (17, 1)
       for input "829851628752296034247307144300617649465159", after 20 seconds give up
*/

    // set timer
    long timeStartMs = System.currentTimeMillis();
    long endTime = timeStartMs + 10000L;
    // todo start time is double
    System.out.println(timeStartMs);
    numberToCalculateRootsFor = Long.parseLong("9181531581341931811"); // todo remove
    Pair<Long, Long> roots1 = calculateRoots(numberToCalculateRootsFor);
    System.out.println("stopped");
    long currentTimeAfter = System.currentTimeMillis();
    boolean isSuccess = false;

    System.out.println(endTime);
    System.out.println(currentTimeAfter);

    if (currentTimeAfter > endTime ){
      System.out.println("Didn't finish calc");
      System.out.println("Time upon giving up: " + (currentTimeAfter - timeStartMs));
      isSuccess = false;
    }
    else{
      if (isFinishedCalculation){
        System.out.println("found the pair: " + roots1.first + " " + roots1.second);
        isSuccess = true;
      }
      else{
        System.out.println("prime number");
        System.out.println("found the pair: " + "1" + " " + numberToCalculateRootsFor);
        isSuccess = true;
      }
    }

    // send broadcast upon success
    if (isSuccess) {
      Intent successIntent = new Intent("found_roots");
      successIntent.putExtra("original_number", numberToCalculateRootsFor);
      if (roots1 != null) {
        successIntent.putExtra("root1", roots1.first);
        successIntent.putExtra("root2", roots1.second);
      }
      else{
        long first = (long)1;
        successIntent.putExtra("root1", first);
        successIntent.putExtra("root2", numberToCalculateRootsFor);
      }
      successIntent.putExtra("calculation_time", (currentTimeAfter - timeStartMs));
      sendBroadcast(successIntent);
    }
    else {
      // send broadcast upon failure
      Intent failureIntent = new Intent("stopped_calculations");
      failureIntent.putExtra("original_number", numberToCalculateRootsFor);
      failureIntent.putExtra("time_until_give_up_seconds", (currentTimeAfter - timeStartMs));
      System.out.println("Failllllll");
      sendBroadcast(failureIntent);
    }
  }
}