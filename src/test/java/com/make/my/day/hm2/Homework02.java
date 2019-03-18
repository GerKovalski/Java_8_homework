package com.make.my.day.hm2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.Test;

public class Homework02 {

  @Test
  public void concatenateChars() {
    Function<Character[], String> charConcatenator = (c)->{
      StringBuilder result = new StringBuilder();
      for (Character character : c) {
        result.append(character);
      }
      return result.toString();
    };

    String result_1 = charConcatenator.apply(new Character[]{'a', 'b', 'c'});
    String result_2 = charConcatenator.apply(new Character[]{'H', 'e', 'l', 'l', 'o'});
    String result_3 = charConcatenator.apply(new Character[]{'T', 'u', 'r', 't', 'l', 'e'});

    assertEquals("abc", result_1);
    assertEquals("Hello", result_2);
    assertEquals("Turtle", result_3);
  }

  @Test
  public void reversedWord() {
    Predicate<String> isReversedStringTheSame = (s -> {
      StringBuilder sb = new StringBuilder(s);
      sb.reverse();
      return sb.toString().equals(s);
    });

    boolean result_1 = isReversedStringTheSame.test("abccba");
    boolean result_2 = isReversedStringTheSame.test("level");
    boolean result_3 = isReversedStringTheSame.test("cow");
    boolean result_4 = isReversedStringTheSame.test("radar");
    boolean result_5 = isReversedStringTheSame.test("mellow");
    boolean result_6 = isReversedStringTheSame.test("madam");

    assertTrue(result_1);
    assertTrue(result_2);
    assertFalse(result_3);
    assertTrue(result_4);
    assertFalse(result_5);
    assertTrue(result_6);
  }

  private class Counter {

    private Function<String, Integer> transform;
    private BinaryOperator<Integer> summarizer;

    public Counter(Function<String, Integer> transform,
        BinaryOperator<Integer> summarizer) {
      this.transform = transform;
      this.summarizer = summarizer;
    }

    public int getSum(String firstInput, String secondInput) {
      int firstTransformedNumber = transform.apply(firstInput);
      int secondTransformedNumber = transform.apply(secondInput);
      return summarizer.apply(firstTransformedNumber, secondTransformedNumber);
    }
  }

  @Test
  public void transformAndProvideSumWithCounter() {
    Function<String, Integer> transform = Integer::parseInt;
    BinaryOperator<Integer> increment = ((i1, i2) -> i1 + i2);

    Counter sut_1 = new Counter(transform, increment);
    Counter sut_2 = new Counter(transform, increment);
    Counter sut_3 = new Counter(transform, increment);

    assertEquals(5, sut_1.getSum("3", "2"));
    assertEquals(10, sut_2.getSum("5", "5"));
    assertEquals(57, sut_3.getSum("24", "33"));
  }

  @Test
  public void consumerPrintInPrintStream() {
    final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    final PrintStream original = System.out;
    System.setOut(new PrintStream(outContent));

    Consumer<Object> printHelloInSystemOut = System.out::print;

    printHelloInSystemOut.accept("hello");
    assertEquals("hello", outContent.toString());

    outContent.reset();

    printHelloInSystemOut.accept("epam");
    assertEquals("epam", outContent.toString());

    outContent.reset();

    printHelloInSystemOut.accept(12345);
    assertEquals("12345", outContent.toString());

    System.setOut(original);
  }

  @Test
  public void testPredicateScenario() {

    Predicate<String> isStartsWithXX = (s -> s.startsWith("XX"));
    Predicate<String> isEndWithZZZ = isStartsWithXX.and(s -> s.endsWith("ZZZ"));
    Predicate<String> haveFiveStarsInRow = isEndWithZZZ.or(s -> s.matches("^.*\\*{5}.*$"));

    assertFalse(haveFiveStarsInRow.test("Xnot_rightZZZ"));
    assertTrue(haveFiveStarsInRow.test("XXsuperduperZZZ"));
    assertFalse(haveFiveStarsInRow.test("XXnotrighttooZZ"));
    assertTrue(haveFiveStarsInRow.test("one*two**three***four****five*****"));
    assertFalse(haveFiveStarsInRow.test("this**will**fail*"));
  }

  @Test
  public void chainOfFunctionInvocation() {
    List<Integer> numbers = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));

    Predicate<Integer> isEven = i -> i%2 == 0;

    List<Integer> evenNumbersList = new ArrayList<>();

    for (Integer num : numbers) {
      if (isEven.test(num)){
        evenNumbersList.add(num);
      }
    }

    assertTrue(evenNumbersList.containsAll(Arrays.asList(2,4,6,8,10)));

    Function<List<Integer>, List<Integer>> sumWithNextElement = (list) -> {
      List<Integer> result = new LinkedList<>();
      for (int i = 0; i < list.size();) {
        int sum = list.get(i++);
        if (i < list.size()){
          sum+=list.get(i);
        }
        else {
          sum+=list.get(0);
        }
        result.add(sum);
      }
      return result;
    };

    List<Integer> newNumbers = sumWithNextElement.apply(evenNumbersList);

    assertTrue(newNumbers.containsAll(Arrays.asList(6,10,14,18,12)));

    Predicate<Integer> moreThanThirteen = integer -> integer > 13;

    List<Integer> numbersMoreThanFifteen = new ArrayList<>();

    for (Integer num : newNumbers) {
      if (moreThanThirteen.test(num)){
        numbersMoreThanFifteen.add(num);
      }
    }

    assertTrue(numbersMoreThanFifteen.containsAll(Arrays.asList(14,18)));
  }

  private static Predicate<String> lengthMoreThanSeven() {
    return s -> s.length() > 7;
  }

  private static Function<Predicate<String>, Function<String, String>> doubleStringOrAddThreeDots() {
    return (predicate) -> s -> predicate.test(s) ? s.concat(s) : s.concat("...");
  }

  private static Function<String, Integer> lengthOfWord(
      Function<Predicate<String>, Function<String, String>> doubledOrWithThreeDots) {
    return s -> doubledOrWithThreeDots.apply(lengthMoreThanSeven()).apply(s).length();
  }

  @Test
  public void checkDeepCallBack() {
    assertFalse(lengthMoreThanSeven().test("no"));
    assertTrue(lengthMoreThanSeven().test("wonderful"));
    assertFalse(lengthMoreThanSeven().test("welcome"));


    assertEquals("no...", doubleStringOrAddThreeDots()
        .apply(lengthMoreThanSeven()).apply("no"));

    assertEquals("wonderfulwonderful", doubleStringOrAddThreeDots()
        .apply(lengthMoreThanSeven()).apply("wonderful"));

    assertEquals("welcome...", doubleStringOrAddThreeDots()
        .apply(lengthMoreThanSeven()).apply("welcome"));


    assertEquals(5, lengthOfWord(doubleStringOrAddThreeDots()).apply("no").intValue());
    assertEquals(18, lengthOfWord(doubleStringOrAddThreeDots()).apply("wonderful").intValue());
    assertEquals(10, lengthOfWord(doubleStringOrAddThreeDots()).apply("welcome").intValue());
  }

  private class LazyProperty {
    private Supplier<Integer> supplier;
    private Integer lazy;

    public LazyProperty(Supplier<Integer> supplier) {
      this.supplier = supplier;
    }

    public int getLazy() {
      if (lazy == null) {
        lazy = supplier.get();
      }
      return lazy;
    }
  }

  @Test
  public void lazyLoading() {

    LazyProperty sut = new LazyProperty(() -> 1);
    LazyProperty sut_2 = new LazyProperty(() -> 2);
    LazyProperty sut_3 = new LazyProperty(() -> 3);

    assertNull(sut.lazy);
    assertNull(sut_2.lazy);
    assertNull(sut_3.lazy);

    assertEquals(1,sut.getLazy());
    assertEquals(2,sut_2.getLazy());
    assertEquals(3,sut_3.getLazy());

    assertEquals(1,sut.lazy.intValue());
    assertEquals(2,sut_2.lazy.intValue());
    assertEquals(3,sut_3.lazy.intValue());
  }

  @Test
  public void andThenTest() {
    Function<Integer, Integer> sumIntegerOnSix = (integer -> integer+6);
    Function<Integer, Integer> thenMinusThree = (integer -> integer-3);
    Function<Integer, Integer> afterMultipleOnFive = (integer -> integer*5);

    Integer result = sumIntegerOnSix
        .andThen(thenMinusThree)
        .andThen(afterMultipleOnFive)
        .apply(10);

    assertEquals(65, result.intValue());
  }
}
