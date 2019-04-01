package com.make.my.day.hm5;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.Test;

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
    //ToDo: Write your own bi-gram spliterator
    //Todo: Should works in parallel

    /**
     * Read about bi and n-grams https://en.wikipedia.org/wiki/N-gram.
     *
     * @param source
     */
    private List<String> source;
    private String delimeter;
    private AtomicInteger currentElem = new AtomicInteger(0);


    public BigrammSpliterator(List<String> source, String delimeter) {
      this.source = source;
      this.delimeter = delimeter;
    }

    @Override
    public boolean tryAdvance(Consumer<? super String> action) {

      if (source.size() - 1 > currentElem.get()) {
        action.accept(String.format("%s%s%s", source.get(currentElem.getAndIncrement()), delimeter,
            source.get(currentElem.get())));
        return true;
      }
      else {
        return false;
      }
    }

    @Override
    public BigrammSpliterator trySplit() {

      if (source.size() - currentElem.get() <= 2) {
        return null;
      }

      int halfOfRemainedElems = (source.size() - currentElem.get()) / 2;
      int middleOfRemained = currentElem.get() + halfOfRemainedElems;

      return new BigrammSpliterator(source.subList(currentElem.getAndSet(middleOfRemained), middleOfRemained + 1),
          delimeter);


    }

    @Override
    public long estimateSize() {
      return source.size() - currentElem.get();
    }

    @Override
    public int characteristics() {
      return SIZED | SUBSIZED | ORDERED | CONCURRENT;
    }


  }


}
