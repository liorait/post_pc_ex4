package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

public class CalculateRootsService extends IntentService {

  // todo code that runs in service does not run on main thread
  boolean isFinishedCalculation = false;
  ArrayList<Integer> roots = new ArrayList<>();
  int count = 0; // number of roots
  public CalculateRootsService() {
    super("CalculateRootsService");
  }

  protected Pair<Integer, Integer> calculateRoots(long numberToCalculateRootsFor){
    long timeStartMs = System.currentTimeMillis();
    long endTime = timeStartMs + 20000L;
    System.out.println(timeStartMs);
    System.out.println("here " + numberToCalculateRootsFor);
    //if (isPrime(numberToCalculateRootsFor)){

    //}
    // calculates roots
    int i = 2;

    //for (int i = 2; i <= numberToCalculateRootsFor / 2; i++) {
    long currentTimeAfter = System.currentTimeMillis();

    while ((i <= numberToCalculateRootsFor / 2) && (currentTimeAfter < endTime)){
      int root = (int) (numberToCalculateRootsFor / i);
      if (numberToCalculateRootsFor % i == 0) {
        int j = (int) (numberToCalculateRootsFor / root);
        Pair<Integer, Integer> newPair = new Pair<>(j, root);
        roots.add(root);
        count++;
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
    long endTime = timeStartMs + 20000L;
    // todo start time is double
    System.out.println(timeStartMs);
    numberToCalculateRootsFor = Long.parseLong("9181531581341931811"); // todo remove
    Pair<Integer, Integer> roots1 = calculateRoots(numberToCalculateRootsFor);
    System.out.println("stopped");
    long currentTimeAfter = System.currentTimeMillis();

    System.out.println(endTime);
    System.out.println(currentTimeAfter);

    if (currentTimeAfter > endTime ){
      System.out.println("Didn't finish calc");
    }
    else{
      if (isFinishedCalculation){
        System.out.println("found the pair: " + roots1.first + " " + roots1.second);
      }
      else{
        System.out.println("prime number");
        System.out.println("found the pair: " + "1" + " " + numberToCalculateRootsFor);
      }
    }

  // todo check not found roots because a prime number and not because lack of time

  }
}