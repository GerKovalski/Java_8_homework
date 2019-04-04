package com.make.my.day.hm5;

import javafx.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
        ).collect(Collectors.toList());

        assertThat("Incorrect result", new HashSet<>(result), is(new HashSet<>(expected)));
        assertThat("Incorrect result order",
                result.stream()
                        .map(s -> s.substring(0,3))
                        .collect(Collectors.toList()),
                is(expected.stream()
                        .map(s -> s.substring(0,3))
                        .collect(Collectors.toList()))
                );
    }

    @Test
    public void spliteratorIntTest() {
        Stream<Integer> left = IntStream.iterate(1, i -> i + 1).limit(10).boxed();
        Stream<String> right = Arrays.stream("0x 1a 2b 3c 4e 5g 9l".split(" "));

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
        Stream<Integer> left = IntStream.iterate(1, i -> i + 1).limit(Integer.MAX_VALUE >> 2).boxed();
        Stream<Integer> right = IntStream.iterate(1, i -> i + 1).limit(Integer.MAX_VALUE >> 2).boxed();

        long count = StreamSupport.stream(new MergeSortInnerJoinSpliterator<>(left,
                right, Function.identity(), Function.identity(), true), false)
                .count();
        assertThat("Incorrect result", count, is((long)Integer.MAX_VALUE >> 2));
    }

    //ToDo: Implement your own merge sort inner join spliterator. See https://en.wikipedia.org/wiki/Sort-merge_join
    public static class MergeSortInnerJoinSpliterator<C extends Comparable<C>, L, R> implements Spliterator<Pair<L, R>> {
        Stream<L> left;
        Stream<R> right;
        Function<L, C> keyExtractorLeft;
        Function<R, C> keyExtractorRight;
        L cLeft;
        R cRight;
        Iterator<L> iterLeft;
        Iterator<R> iterRight;
        ArrayList<L> bufferLeft = new ArrayList<>();
        ArrayList<R> bufferRight = new ArrayList<>();
        Integer indLeft;
        Integer indRight;
        Integer mark;

        public MergeSortInnerJoinSpliterator(Stream<L> left,
                                             Stream<R> right,
                                             Function<L, C> keyExtractorLeft,
                                             Function<R, C> keyExtractorRight,
                                             boolean isSorted) {
            if (!isSorted) {
                this.iterLeft = left.sorted().iterator();
                this.iterRight = right.sorted().iterator();
            } else {
                this.iterLeft = left.iterator();
                this.iterRight = right.iterator();
            }
            this.left = left;
            this.right = right;
            this.keyExtractorLeft = keyExtractorLeft;
            this.keyExtractorRight = keyExtractorRight;
            if (iterLeft.hasNext()) {
                cLeft = iterLeft.next();
            }
            if (iterRight.hasNext()) {
                cRight = iterRight.next();
            }
            bufferLeft.add(cLeft);
            bufferRight.add(cRight);
            indLeft = 0;
            indRight = 0;
        }

        private int compare() {
            C left = keyExtractorLeft.apply(cLeft);
            C right = keyExtractorRight.apply(cRight);
            return left.compareTo(right);
        }

        private boolean advanceLeft() {
            if (bufferLeft.size() > indLeft + 1) {
                cLeft = bufferLeft.get(indLeft + 1);
                indLeft++;
            } else if (iterLeft.hasNext()) {
                cLeft = iterLeft.next();
                if (bufferLeft.size() > 10) {
                    bufferLeft.remove(0);
                    bufferLeft.add(cLeft);
                } else {
                    bufferLeft.add(cLeft);
                    indLeft++;
                }
            } else {
                return false;
            } return true;
        }

        private boolean advanceRight() {
            if (bufferRight.size() > indRight + 1) {
                cRight = bufferRight.get(indRight + 1);
                indRight++;
            } else if (iterRight.hasNext()) {
                cRight = iterRight.next();
                if (bufferRight.size() > 10) {
                    bufferRight.remove(0);
                    bufferRight.add(cRight);
                } else {
                    bufferRight.add(cRight);
                    indRight++;
                }
            } else {
                return false;
            } return true;
        }

        private boolean isLast() {
            return !iterLeft.hasNext();
        }

        @Override
        public boolean tryAdvance(Consumer<? super Pair<L, R>> action) {
            if (iterLeft.hasNext() || iterRight.hasNext() || isLast()) {
                if (mark == null) {
                    while (compare() < 0){
                        if (!advanceLeft()) {
                            return false;
                        }
                    }
                    mark = indRight;
                }
                if (compare() == 0) {
                    action.accept(new Pair<>(bufferLeft.get(indLeft), bufferRight.get(indRight)));
                    if (!advanceRight()) {
                        return false;
                    }
                } else {
                    indRight = mark;
                    cRight = bufferRight.get(indRight);
                    if (!advanceLeft()) {
                        return false;
                    } mark = null;
                }
            } else {
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
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return ORDERED | IMMUTABLE;
        }
    }

}
