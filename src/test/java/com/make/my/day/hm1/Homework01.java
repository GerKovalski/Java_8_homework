package com.make.my.day.hm1;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;

public class Homework01 {

  @FunctionalInterface
  private interface Test01 {

    /**
     * Generate whole word from a char array
     */
    String createMessage(char[] chars);
  }

  @Test
  public void concatenateChars() {
    Test01 sut = chars -> new String(chars);

    String result_1 = sut.createMessage(new char[]{'a', 'b', 'c'});
    String result_2 = sut.createMessage(new char[]{'H', 'e', 'l', 'l', 'o'});
    String result_3 = sut.createMessage(new char[]{'T', 'u', 'r', 't', 'l', 'e'});

    assertEquals("abc", result_1);
    assertEquals("Hello", result_2);
    assertEquals("Turtle", result_3);
  }

  @FunctionalInterface
  private interface Test02 {

    /**
     * Check if word is palindrome exm: "word" == "drow" -> false exm2: "eye" == "eye"  -> true
     */
    boolean isReversedStringTheSame(String word);
  }

  @Test
  public void isWordPalindrome() {
    Test02 sut = word -> word.equals(new StringBuilder(word).reverse().toString());

    boolean result_1 = sut.isReversedStringTheSame("abccba");
    boolean result_2 = sut.isReversedStringTheSame("level");
    boolean result_3 = sut.isReversedStringTheSame("cow");
    boolean result_4 = sut.isReversedStringTheSame("radar");
    boolean result_5 = sut.isReversedStringTheSame("mellow");
    boolean result_6 = sut.isReversedStringTheSame("madam");

    assertTrue(result_1);
    assertTrue(result_2);
    assertFalse(result_3);
    assertTrue(result_4);
    assertFalse(result_5);
    assertTrue(result_6);
  }

  @FunctionalInterface
  private interface Transform {

    /**
     * Convert String to Integer type
     */
    int convert(String input);
  }

  @FunctionalInterface
  private interface Summarizer {

    /**
     * Sum two numbers
     */
    int makeSum(int firstNumber, int secondNumber);
  }

  private class Counter {

    private Transform transform;
    private Summarizer summarizer;

    public Counter(Transform transform,
        Summarizer summarizer) {
      this.transform = transform;
      this.summarizer = summarizer;
    }

    public int getSum(String firstInput, String secondInput) {
      int firstTransformedNumber = transform.convert(firstInput);
      int secondTransformedNumber = transform.convert(secondInput);
      return summarizer.makeSum(firstTransformedNumber, secondTransformedNumber);
    }
  }

  @Test
  public void transformAndProvideSumWithCounter() {

    Transform transform = str -> (str == null) ? 0 : Integer.valueOf(str);

    Summarizer increment = (int fn, int sn) -> fn + sn;

    Counter sut_1 = new Counter(transform, increment);
    Counter sut_2 = new Counter(transform, increment);
    Counter sut_3 = new Counter(transform, increment);

    assertEquals(5, sut_1.getSum("3", "2"));
    assertEquals(10, sut_2.getSum("5", "5"));
    assertEquals(57, sut_3.getSum("24", "33"));
  }

  @Test
  public void sortByNameLength() {

    String[] names = {"Fred", "Maggy", "Suzan", "Loid", "Nir", "Lo", "Stefan", "Maximilian"};

    Arrays.sort(names, (fStr, sStr) -> fStr.length() - sStr.length());

    String[] expectedSortedNames = {"Lo", "Nir", "Fred", "Loid", "Maggy",
        "Suzan", "Stefan", "Maximilian"};

    assertArrayEquals(expectedSortedNames, names);
  }
}


