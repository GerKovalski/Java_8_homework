package com.make.my.day.hm5;

import java.util.Iterator;
import javafx.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MergeSortJoinTest {

  @Test
  public void spliteratorTest() {
    Stream<String> left = Arrays.stream("a b c c o f g h k l".split(" "));
    Stream<String> right = Arrays.stream("aa bb cc ca cb cd ce dd pp ee ff gg hh kk".split(" "));

    List<String> result = StreamSupport.stream(new MergeSortInnerJoinSpliterator<>(left,
        right, Function.identity(), s -> s.substring(0, 1)), false)
        .map(pair -> pair.getKey() + " " + pair.getValue())
        .collect(Collectors.toList());
    List<String> expected = Arrays.asList(
        "a aa",
        "b bb",
        "c cc",
        "c ca",
        "c cb",
        "c cd",
        "c ce",
        "c cc",
        "c ca",
        "c cb",
        "c cd",
        "c ce",
        "f ff",
        "g gg",
        "h hh",
        "k kk"
    );

    assertThat("Incorrect result", result, is(expected));
  }

  @Test
  public void spliteratorIntTest() {
    Stream<Integer> left = IntStream.iterate(1, i -> i + 1).limit(10).boxed();
    Stream<String> right = Arrays.stream("0x 1a 2b 3c 4e 5g 9l".split(" "));

    List<String> result = StreamSupport.stream(new MergeSortInnerJoinSpliterator<>(left,
        right, obj -> String.valueOf(obj), s -> s.substring(0, 1)), false)
        .map(pair -> pair.getKey() + " " + pair.getValue())
        .collect(Collectors.toList());
    List<String> expected = Arrays.asList(
        "1 1a",
        "2 2b",
        "3 3c",
        "4 4e",
        "5 5g",
        "9 9l"
    );

    assertThat("Incorrect result", result, is(expected));
  }

  //ToDo: Implement your own merge sort inner join spliterator. See https://en.wikipedia.org/wiki/Sort-merge_join
  public static class MergeSortInnerJoinSpliterator<C extends Comparable<C>, L, R> implements Spliterator<Pair<L, R>> {

    private final List<L> left;
    private final List<R> right;
    private int leftIndex = 0;
    private Function<L, C> keyExtractorLeft;
    private final Function<R, C> keyExtractorRight;

    public MergeSortInnerJoinSpliterator(Stream<L> left,
        Stream<R> right,
        Function<L, C> keyExtractorLeft,
        Function<R, C> keyExtractorRight) {
      this.left = left.collect(Collectors.toList());
      this.right = right.collect(Collectors.toList());
      this.keyExtractorLeft = keyExtractorLeft;
      this.keyExtractorRight = keyExtractorRight;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Pair<L, R>> action) {

      if (leftIndex == left.size()) {
        return false;
      }

      right.stream()
          .peek(System.out::println)
          .filter((r -> keyExtractorLeft.apply(left.get(leftIndex)).equals(keyExtractorRight.apply(r))))
          .peek(System.out::println)
          .map(r -> new Pair<>(left.get(leftIndex), r)).
          peek(System.out::println).
          peek(System.out::println)
          .forEach(action::accept);
      ++leftIndex;
      return true;
    }

    @Override
    public Spliterator<Pair<L, R>> trySplit() {

      return null;
    }

    @Override
    public long estimateSize() {
      return 0;
    }

    @Override
    public int characteristics() {
      return 0;

    }
  }

}
