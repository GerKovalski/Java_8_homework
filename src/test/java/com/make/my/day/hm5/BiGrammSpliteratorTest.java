package com.make.my.day.hm5;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class BiGrammSpliteratorTest {

    @Test
    public void biGramSplitTest() throws Exception {
        List<String> tokens = Arrays.asList("I should never try to implement my own spliterator".split(" "));

        Set<String> result = StreamSupport.stream(new BigramSpliterator(tokens, " "), true)
                .collect(Collectors.toSet());

        Set<String> expected = Arrays.stream(new String[]{
                "I should",
                "should never",
                "never try",
                "try to",
                "to implement",
                "implement my",
                "my own",
                "own spliterator"
        }).collect(Collectors.toSet());

        assertThat("Incorrect result", result, is(expected));

    }

    @Test
    public void biGramSplitTestSplit() throws Exception {
        List<String> tokens = Arrays.asList("I should never try to implement my own spliterator".split(" "));

        BigramSpliterator biGrammSpliterator = new BigramSpliterator(tokens, " ");
        BigramSpliterator biGramSpliterator1 = biGrammSpliterator.trySplit();

        assertThat("Spliterator 1 is null", biGramSpliterator1, notNullValue());

        BigramSpliterator biGramSpliterator2 = biGramSpliterator1.trySplit();

        assertThat("Spliterator 2 is null", biGramSpliterator2, notNullValue());
        Consumer<String> consumer = (String s) -> {
        };
        int count = 0;
        while (biGrammSpliterator.tryAdvance(consumer)) {
            count++;
        }

        assertThat("Incorrect Spliterator 0 size", count, is(4));

        count = 0;
        while (biGramSpliterator1.tryAdvance(consumer)) {
            count++;
        }

        assertThat("Incorrect Spliterator 1 size", count, is(2));

        count = 0;
        while (biGramSpliterator2.tryAdvance(consumer)) {
            count++;
        }

        assertThat("Incorrect Spliterator 2 size", count, is(2));

    }

    class BigramSpliterator implements Spliterator<String> {

        private List<String> source;
        private String delimiter;
        private AtomicInteger current;
        private int to;
        /**
         * Read about bi and n-grams https://en.wikipedia.org/wiki/N-gram.
         *
         * @param source
         */
        public BigramSpliterator(List<String> source, String delimiter) {
            this.source = source;
            this.delimiter = delimiter;
            current = new AtomicInteger(0);
        }

        @Override
        public boolean tryAdvance(Consumer<? super String> action) {

            if (current.get() < source.size() - 1) {

                String first = source.get(current.getAndIncrement());
                String second = source.get(current.get());

                String strResult = first + delimiter + second;
                action.accept(strResult);
                return true;
            }
            else {
                return false;
            }
        }

        @Override
        public BigramSpliterator trySplit() {

            if (estimateSize() <= 2)
                return  null;

            int halfOfRemained = (source.size() - current.get()) / 2;
            int middle = current.get() + halfOfRemained;

            List<String> subList = source.subList(current.get(), middle + 1);

            BigramSpliterator another = new BigramSpliterator(subList, delimiter);
            current.set(middle);

            return another;
        }

        @Override
        public long estimateSize() {
            return source.size() - current.get();
        }

        @Override
        public int characteristics() {
            return  source.stream().spliterator().characteristics() & (SIZED | SUBSIZED | CONCURRENT | IMMUTABLE | ORDERED);
        }
    }
}
