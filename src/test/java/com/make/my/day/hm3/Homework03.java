package com.make.my.day.hm3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;

public class Homework03 {

  @Test
  public void createWithBuilder() {
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

    Stream<Integer> prepared = Stream.concat(intStream, intStream_2);
    Stream<Integer> result = Stream.concat(prepared, intStream_3);

    assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6}, result.toArray());
  }

  @Test
  public void iterateForNineHundredsElements() {

    Stream<Integer> stream = Stream.iterate(100, x -> x + 1).limit(900);

    Integer[] expected = new Integer[900];
    for (int i = 100, j = 0; j < 900; i++, j++) {
      expected[j] = i;
    }

    assertArrayEquals(expected, stream.toArray());
  }

  @Test
  public void createWithArraysMethod() {
    IntStream sut = Arrays.stream(new int[]{'t','u','r','t','l','e'});

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
    Stream<Agent> agents = Stream.generate(Agent::new).limit(3000);

    Agent[] expected = new Agent[3000];
    for (int i = 0; i < 3000; i++) {
      expected[i] = new Agent();
    }

    assertArrayEquals(expected, agents.toArray());
  }

  @Test
  public void mapWordsReverse() {
    Stream<String> words = Stream.of("We", "all", "do", "our", "best");

    words = words.map(str -> new StringBuilder(str).reverse().toString());

    assertArrayEquals(
        new String[]{"eW", "lla", "od", "ruo", "tseb"},
        words.toArray(String[]::new)
    );
  }

  @Test
  public void mapFilterMapTest() {
    IntStream numbers = IntStream.of(1, 7, 4, 6, 3, 13, 2, 6, 8);


    int[] result = numbers
        .map(ele -> ele + 1)
        .filter(ele -> ele % 2 == 0)
        .map(ele -> ele * 2)
        .toArray();

    assertArrayEquals(new int[]{4, 16, 8, 28}, result);
  }

  @Test
  public void sortedByRepeatableChars() {
    List<String> words = Arrays.asList("Privet", "Elevate", "Splendid", "Ssssssuper");

    Function<String, Integer> maxNumber = (s) -> {

      Map<Integer, Integer> charLengths = s.toLowerCase().chars().mapToObj(x -> x)
              .collect(HashMap::new,
                      (Map<Integer, Integer> map, Integer ch) ->
                              map.merge(ch, 1, (valFirst, valSec) -> valFirst + valSec ),
                      Map::putAll);

      return charLengths.values().stream().max(Integer::compareTo).orElse(0);
    };

    String[] result = words.stream()
        .sorted(Comparator.comparingInt(maxNumber::apply).reversed())
        .toArray(String[]::new);

    assertArrayEquals(new String[]{"Ssssssuper", "Elevate", "Splendid", "Privet"}, result);
  }


  @Test
  public void flatMapCheck() {
    String[] words = new String[]{"Hel", "lo", " won", "der", "ful", " ","world", "!"};

    String bigString = Arrays.stream(words)
        .flatMap(Stream::of)
        .collect(Collectors.joining());
    assertEquals("Hello wonderful world!", bigString);
  }

  @Test
  public void uniqueValues() {
    List<Integer> numbers = Arrays.asList(1, 1, 3, 3, 12, 11, 12, 11, 11, 1, 3);

    int [] result = numbers.stream().distinct().mapToInt(x -> x).toArray();

    assertArrayEquals(new int[]{1, 3, 12, 11}, result);
  }

  @Test
  public void getSumWithReduce() {
    List<Integer> numbers = Arrays.asList(4,4,2,2,8,10);
    Integer result = numbers.stream()
        .reduce(0, Integer::sum);

    assertEquals(30, result.intValue());
  }
}
