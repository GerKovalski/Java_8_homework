package com.make.my.day.hm5;

import javafx.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MergeSortJoinTest {

  @Test
  public void spliteratorTest() {
    List<String> listLeft = Arrays.asList("a b c c o f g h k l".split(" "));
    Collections.shuffle(listLeft);
    Stream<String> left = listLeft.stream();
    List<String> listRight = Arrays.asList("aa bb cc ca cb cd ce dd pp ee ff gg hh kk".split(" "));
    Collections.shuffle(listRight);
    Stream<String> right = listRight.stream();

    List<String> result = StreamSupport.stream(new MergeSortInnerJoinSpliterator<>(left,
        right, Function.identity(), s -> s.substring(0, 1), false), false)
                                       .map(pair -> pair.getKey() + " " + pair.getValue())
                                       .collect(Collectors.toList());
    List<String> expected = Stream.of(
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
    )
                                  .collect(Collectors.toList());

    assertThat("Incorrect result", new HashSet<>(result), is(new HashSet<>(expected)));
    assertThat("Incorrect result order",
        result.stream()
              .map(s -> s.substring(0, 3))
              .collect(Collectors.toList()),
        is(expected.stream()
                   .map(s -> s.substring(0, 3))
                   .collect(Collectors.toList()))
    );
  }

  @Test
  public void spliteratorIntTest() {
    Stream<Integer> left = IntStream.iterate(1, i -> i + 1)
                                    .limit(10)
                                    .boxed();
    Stream<String> right = Arrays.stream("0x 1a 2b 3c 4e 5g 9l".split(" "));
    //2b 3c 4e 5g 9l
    List<String> result = StreamSupport.stream(new MergeSortInnerJoinSpliterator<>(left,
        right, String::valueOf, s -> s.substring(0, 1), false), false)
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


  @Test
  public void spliteratorMemoryTest() {
    Stream<Integer> left = IntStream.iterate(1, i -> i + 1)
                                    .limit(Integer.MAX_VALUE >> 2)
                                    .boxed();
    Stream<Integer> right = IntStream.iterate(1, i -> i + 1)
                                     .limit(Integer.MAX_VALUE >> 2)
                                     .boxed();

    long count = StreamSupport.stream(new MergeSortInnerJoinSpliterator<>(left,
        right, Function.identity(), Function.identity(), true), false)
                              .count();
    assertThat("Incorrect result", count, is((long) Integer.MAX_VALUE >> 2));

  }

  public static class MergeSortInnerJoinSpliterator<C extends Comparable<C>, L, R> implements
      Spliterator<Pair<L, R>> {


    Iterator<L> leftIterator;
    Iterator<R> rightIterator;
    Function<L, C> keyExtractorLeft;
    Function<R, C> keyExtractorRight;
    Integer flag = 0;
    L nextLeft;
    R nextRight;
    L prevLeft;
    boolean last = true;

    public MergeSortInnerJoinSpliterator(Stream<L> left,
                                         Stream<R> right,
                                         Function<L, C> keyExtractorLeft,
                                         Function<R, C> keyExtractorRight,
                                         boolean isSorted) {

      if (!isSorted) {
        this.leftIterator = left.sorted()
                                .iterator();
        this.rightIterator = right.sorted()
                                  .iterator();
      } else {
        this.leftIterator = left.iterator();
        this.rightIterator = right.iterator();
      }
      this.keyExtractorLeft = keyExtractorLeft;
      this.keyExtractorRight = keyExtractorRight;

      if (leftIterator.hasNext()) {
        nextLeft = leftIterator.next();
      }

    }

    @Override
    public boolean tryAdvance(Consumer<? super Pair<L, R>> action) {
      if (leftIterator.hasNext() || rightIterator.hasNext() || last) {
        if (flag == 0) {
          if (leftIterator.hasNext() && rightIterator.hasNext()) {
            prevLeft = nextLeft;
            nextLeft = leftIterator.next();
            nextRight = rightIterator.next();
          }
        } else if (flag == -1) {
          if (leftIterator.hasNext()) {
            prevLeft = nextLeft;
            nextLeft = leftIterator.next();
          } else if (last) {
            prevLeft = nextLeft;
            nextLeft = null;
            last = false;
          } else {
            return false;
          }
        } else {
          if (rightIterator.hasNext()) {
            nextRight = rightIterator.next();
          } else {
            return false;
          }
        }

        if (isLeftEqualsRight()) {
          if (nextLeft != null && keyExtractorLeft.apply(prevLeft)
                                                  .equals(keyExtractorLeft.apply(nextLeft))) {
            action.accept(new Pair<>(prevLeft, nextRight));
          }
          action.accept(new Pair<>(prevLeft, nextRight));
          flag = 1;
          return true;
        } else {
          return true;
        }
      }

      return false;
    }

    private boolean isLeftEqualsRight() {
      int compare = keyExtractorLeft.apply(prevLeft)
                                    .compareTo(keyExtractorRight.apply(nextRight));
      if (compare < 0) {
        flag = -1;
        return false;
      }
      if (compare > 0) {
        flag = 1;
        return false;
      }
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
      return SIZED | SUBSIZED | CONCURRENT | IMMUTABLE | ORDERED;
    }
  }

}