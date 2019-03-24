package com.make.my.day.hm3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;

public class Homework03 {

  @Test
  public void createWithBuilder() {
    // uncomment and add entities
    Stream<String> sut = Stream.<String>builder()
        .add("Hello")
        .add("Wonderful")
        .add("Word")
        .build();

    List<String> resultList = sut.collect(Collectors.toList());

    assertArrayEquals(new String[]{"Hello", "Wonderful", "Word"},
        resultList.toArray());
  }

  @Test
  public void concatStreams() {
    Stream<Integer> intStream = Stream.of(1, 2);
    Stream<Integer> intStream_2 = Stream.of(3, 4);
    Stream<Integer> intStream_3 = Stream.of(5, 6);

    // Concat streams correctly
    Stream<Integer> prepared = Stream.concat(intStream, intStream_2);
    Stream<Integer> result = Stream.concat(prepared, intStream_3);

    assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6}, result.toArray());
  }

  @Test
  public void iterateForNineHundredsElements() {
    // Add correctly realization of iterate
    Stream<Integer> stream = Stream.iterate(100, x -> x + 1)
        .limit(900);

    Integer[] expected = new Integer[900];
    for (int i = 100, j = 0; j < 900; i++, j++) {
      expected[j] = i;
    }

    assertArrayEquals(expected, stream.toArray());
  }

  @Test
  public void createWithArraysMethod() {
    // Create realization with Arrays.stream
    IntStream sut = Arrays.stream(new int[]{'t', 'u', 'r', 't', 'l', 'e'});

    assertArrayEquals(new int[]{'t', 'u', 'r', 't', 'l', 'e'}, sut.toArray());
  }

  private class Agent {

    private final String name = "Smith";

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Agent agent = (Agent) o;
      return Objects.equals(name, agent.name);
    }
  }

  @Test
  public void provideStreamWithGenerate() {
    // Generate 3000 agents
    Stream<Agent> agents = Stream.generate(() -> new Agent()).limit(3000);

    Agent[] expected = new Agent[3000];
    for (int i = 0; i < 3000; i++) {
      expected[i] = new Agent();
    }

    assertArrayEquals(expected, agents.toArray());
  }

  @Test
  public void mapWordsReverse() {
    Stream<String> words = Stream.of("We", "all", "do", "our", "best");

    //  Create "map" realization
    words = words.map(s -> new StringBuilder(s).reverse().toString());

    assertArrayEquals(
        new String[]{"eW", "lla", "od", "ruo", "tseb"},
        words.toArray(String[]::new)
    );
  }

  @Test
  public void mapFilterMapTest() {
    IntStream numbers = IntStream.of(1, 7, 4, 6, 3, 13, 2, 6, 8);

    //  1) increment each element
    //  2) filter on even numbers
    // 3) each element multiply on 2

    int[] result = numbers
        .map(x -> x + 1)
        .filter(x -> x % 2 == 0)
        .map(x -> x * 2)
        .toArray();

    assertArrayEquals(new int[]{4, 16, 8, 28}, result);
  }

  @Test
  public void sortedByRepeatableChars() {
    List<String> words = Arrays.asList("Privet", "Elevate", "Splendid", "Ssssssuper");

    String[] result = words.stream()
        // : Add realization
        .sorted((s1, s2) -> {
          int count1 = (int) (s1.length() - s1.chars().distinct().count());
          int count2 = (int) (s2.length() - s2.chars().distinct().count());
          return count2 - count1;
        })
        .toArray(String[]::new);

    // : For example "Twitter" and "Hello" -> there 3 "t" chars and 2 "l" chars 3 > 2
    // : So the first word will be Twitter then Hello
    assertArrayEquals(new String[]{"Ssssssuper", "Elevate", "Splendid", "Privet"}, result);
  }


  @Test
  public void flatMapCheck() {
    String[] words = new String[]{"Hel", "lo", " won", "der", "ful", " ", "world", "!"};

    //  Uncomment and add correct realization of flatMap
    String bigString = Arrays.stream(words)
        .flatMap((Stream::of))
        .collect(Collectors.joining());

    assertEquals("Hello wonderful world!", bigString);
  }

  @Test
  public void uniqueValues() {
    List<Integer> numbers = Arrays.asList(1, 1, 3, 3, 12, 11, 12, 11, 11, 1, 3);

    //  Use numbers.stream()... add realization to get unique values
    int[] result = numbers.stream()
        .distinct()
        .mapToInt(Integer::new)
        .toArray();

    assertArrayEquals(new int[]{1, 3, 12, 11}, result);
  }

  @Test
  public void getSumWithReduce() {
    List<Integer> numbers = Arrays.asList(4, 4, 2, 2, 8, 10);
    Integer result = numbers.stream()
        // Add realization
        .reduce(0, (x, y) -> x + y);

    assertEquals(30, result.intValue());
  }
}
