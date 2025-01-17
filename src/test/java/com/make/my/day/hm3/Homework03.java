package com.make.my.day.hm3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;

public class Homework03 {

  @Test
  public void createWithBuilder() {
    // TODO: uncomment and add entities
    Stream<String> sut = Stream.<String>builder().add("Hello").add("Wonderful").add("Word").build();

    List<String> resultList = sut.collect(Collectors.toList());

    assertArrayEquals(new String[]{"Hello", "Wonderful", "Word"},
        resultList.toArray());
  }

  @Test
  public void concatStreams() {
    Stream<Integer> intStream = Stream.of(1, 2);
    Stream<Integer> intStream_2 = Stream.of(3, 4);
    Stream<Integer> intStream_3 = Stream.of(5, 6);

    // TODO: Concat streams correctly
    Stream<Integer> prepared = Stream.concat(intStream, intStream_2);
    Stream<Integer> result = Stream.concat(prepared, intStream_3);

    assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6}, result.toArray());
  }

  @Test
  public void iterateForNineHundredsElements() {
    // TODO: Add correctly realization of iterate
    Stream<Integer> stream = Stream.iterate(100, n -> n + 1).limit(900);

    Integer[] expected = new Integer[900];
    for (int i = 100, j = 0; j < 900; i++, j++) {
      expected[j] = i;
    }

    assertArrayEquals(expected, stream.toArray());
  }

  @Test
  public void createWithArraysMethod() {
    // TODO: Create realization with Arrays.stream
    IntStream sut = Arrays.stream(new int[]{'t', 'u', 'r', 't', 'l', 'e'});

    assertArrayEquals(new int[]{'t', 'u', 'r', 't', 'l', 'e'}, sut.toArray());
  }

  private class Agent{
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
    // TODO: Generate 3000 agents
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

    // TODO: Create "map" realization
    words = words.map(p -> new StringBuilder(p).reverse().toString());

    assertArrayEquals(
        new String[]{"eW", "lla", "od", "ruo", "tseb"},
        words.toArray(String[]::new)
    );
  }

  @Test
  public void mapFilterMapTest() {
    IntStream numbers = IntStream.of(1, 7, 4, 6, 3, 13, 2, 6, 8);

    // TODO: 1) increment each element
    // TODO: 2) filter on even numbers
    // TODO: 3) each element multiply on 2

    int[] result = numbers.filter(s -> s%2 != 0).map(p -> (p + 1) * 2)
        .toArray();

    assertArrayEquals(new int[]{4, 16, 8, 28}, result);
  }

  @Test
  public void sortedByRepeatableChars() {
    List<String> words = Arrays.asList("Privet", "Elevate", "Splendid", "Ssssssuper");

    String[] result = words.stream()
        // TODO: Add realization
        .sorted((o1, o2) -> {
          SortedMap<String, Integer> o1Map = Arrays.stream(o2.split("")).collect(Collectors.toMap((p) -> p, (p) -> 1, (o, n) -> o + 1, TreeMap::new));
          SortedMap<String, Integer> o2Map = Arrays.stream(o2.split("")).collect(Collectors.toMap((p) -> p, (p) -> 1, (o, n) -> o + 1, TreeMap::new));
          System.out.println(o1Map);
          return o1Map.get(o1Map.firstKey()) - o2Map.get(o2Map.firstKey());
        })
        .toArray(String[]::new);

    System.out.println(result);

    // TODO: For example "Twitter" and "Hello" -> there 3 "t" chars and 2 "l" chars 3 > 2
    // TODO: So the first word will be Twitter then Hello
    assertArrayEquals(new String[]{"Ssssssuper", "Elevate", "Splendid", "Privet"}, result);
  }


  @Test
  public void flatMapCheck() {
    String[] words = new String[]{"Hel", "lo", " won", "der", "ful", " ","world", "!"};

    // TODO: Uncomment and add correct realization of flatMap 
    String bigString = null; /*Arrays.stream(words)
        .flatMap(null)
        .collect(Collectors.joining());
        */
    assertEquals("Hello wonderful world!", bigString);
  }

  @Test
  public void uniqueValues() {
    List<Integer> numbers = Arrays.asList(1, 1, 3, 3, 12, 11, 12, 11, 11, 1, 3);

    // TODO: Use numbers.stream()... add realization to get unique values
    int [] result = null;

    assertArrayEquals(new int[]{1, 3, 12, 11}, result);
  }

  @Test
  public void getSumWithReduce() {
    List<Integer> numbers = Arrays.asList(4,4,2,2,8,10);
    Integer result = numbers.stream()
        // TODO: Add realization
        .reduce(0, null);

    assertEquals(30, result.intValue());
  }
}
