package org.openqa.selenium.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class BrowserResponseSequencerUnitTest {

  @Rule public TestName name = new TestName();
  
  private BrowserResponseSequencer seq;
  private List<Integer> numbers;

  @Before
  public void setUp() {
    seq = new BrowserResponseSequencer(name.getMethodName());
    numbers = Collections.synchronizedList( new ArrayList<Integer>());
  }

  @Test
  public void testBrowserResponseSequencer() throws InterruptedException, ExecutionException {

    // we launch them in reverse order, but they get added in sequence
    FutureTask<Void> three = NumberWriter.launchNumberWriter(3, seq, numbers);
    NumberWriter.launchNumberWriter(2, seq, numbers);
    NumberWriter.launchNumberWriter(1, seq, numbers);
    NumberWriter.launchNumberWriter(0, seq, numbers);
    three.get();
    assertEquals("[0, 1, 2, 3]", numbers.toString());
  }

  @Test
  public void testOutOfSequence() throws InterruptedException, ExecutionException {
    // we launch them in reverse order, but they get added in sequence
    long now = System.currentTimeMillis();
    FutureTask<Void> three = NumberWriter.launchNumberWriter(3, seq, numbers);
    NumberWriter.launchNumberWriter(1, seq, numbers);
    NumberWriter.launchNumberWriter(0, seq, numbers);
    three.get();
    long diff = System.currentTimeMillis() - now;
    assertEquals("[0, 1, 3]", numbers.toString());
    assertTrue(diff >= 4995);
  }

  private static class NumberWriter implements Callable<Void> {
    final int num;
    final BrowserResponseSequencer seq;
    final List<Integer> numbers;

    public NumberWriter(int num, BrowserResponseSequencer seq, List<Integer> numbers) {
      this.num = num;
      this.seq = seq;
      this.numbers = numbers;
    }

    public Void call() throws Exception {
      try {
        seq.waitUntilNumIsAtLeast(num);
        // System.out.println(num);
        numbers.add(num);
        seq.increaseNum();
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
      return null;
    }

    public FutureTask<Void> launchThread() {
      FutureTask<Void> ft = new FutureTask<Void>(this);
      new Thread(ft, NumberWriter.class.getName() + '-' + num).start();  // Thread safety reviewed
      return ft;
    }

    public static FutureTask<Void> launchNumberWriter(int num, BrowserResponseSequencer seq,
        List<Integer> numbers) {
      return new NumberWriter(num, seq, numbers).launchThread();
    }
  }

}
