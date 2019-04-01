package com.make.my.day.hm5;

import java.util.ArrayList;
import java.util.stream.Stream;
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

        Set<String> result = StreamSupport.stream(new BigrammSpliterator(tokens, " "), true)
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

        BigrammSpliterator biGrammSpliterator = new BigrammSpliterator(tokens, " ");
        BigrammSpliterator biGramSpliterator1 = biGrammSpliterator.trySplit();

        assertThat("Spliterator 1 is null", biGramSpliterator1, notNullValue());

        BigrammSpliterator biGramSpliterator2 = biGramSpliterator1.trySplit();

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

  class BigrammSpliterator implements Spliterator<String> {

    private List<String> source;
    private int index = 0;
    private String delimeter;
    //ToDo: Write your own bi-gram spliterator
    //Todo: Should works in parallel

    /**
     * Read about bi and n-grams https://en.wikipedia.org/wiki/N-gram.
     */
    public BigrammSpliterator(List<String> source, String delimeter) {
      this.source = source;
      this.delimeter = delimeter;
    }

    @Override
    public boolean tryAdvance(Consumer<? super String> action) {
      if (index == source.size() - 1) {
        ++index;
        return true;
      } else if (index == source.size()) {
        return false;
      } else {
        action.accept(source.get(index) + " " + source.get(index + 1));
        ++index;
        return true;
      }
    }

    @Override
    public BigrammSpliterator trySplit() {

      if (!hasOnlyBigrams(source)) {
        index = 0;
        List<String> temp = new ArrayList<>();
        Stream.iterate(0, i -> ++i).limit(source.size())
            .forEach(i -> {
              if (i < source.size() - 1) {
                temp.add( source.get(i) + " " + source.get(i + 1));
              }
            });
        source = temp;
      }

      List<String> newOne = source.subList(0, source.size() / 2);
      List<String> newTwo = source.subList(source.size() / 2, source.size());

      System.out.println(newOne);
      System.out.println(newTwo);

      BigrammSpliterator bigrammSpliterator = new BigrammSpliterator(newOne, delimeter);

      index = 0;
      this.source = newTwo;

      return bigrammSpliterator;
    }

    @Override
    public long estimateSize() {
      return 0;
    }

    @Override
    public int characteristics() {
      return 0;
    }

    boolean hasOnlyBigrams(List<String> list){

      long bigramCount = list.stream().flatMapToInt(CharSequence::chars)
          .mapToObj(i -> (char) i)
          .filter(character -> character.equals(' '))
          .count();

      return bigramCount == list.size();
    }
  }
}
