package twitter;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class FilterTest {

    private static final Instant t1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant t2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant t3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", t1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", t2);
    private static final Tweet tweet3 = new Tweet(3, "Alyssa", "I love programming in Java", t3);

    // --- writtenBy() ---
    @Test
    public void testWrittenBySingleAuthor() {
        List<Tweet> writtenByAlyssa = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");
        assertEquals("expected 2 tweets from alyssa", 2, writtenByAlyssa.size());
        assertTrue(writtenByAlyssa.contains(tweet1));
        assertTrue(writtenByAlyssa.contains(tweet3));
    }

    // --- inTimespan() ---
    @Test
    public void testInTimespanBetweenTwoTimes() {
        Timespan timespan = new Timespan(t1, t2);
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), timespan);
        assertEquals("expected 2 tweets in timespan", 2, inTimespan.size());
        assertTrue(inTimespan.contains(tweet1));
        assertTrue(inTimespan.contains(tweet2));
    }

    // --- containing() ---
    @Test
    public void testContainingWord() {
        List<Tweet> containingWord = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("talk"));
        assertEquals("expected 2 tweets containing 'talk'", 2, containingWord.size());
        assertTrue(containingWord.contains(tweet1));
        assertTrue(containingWord.contains(tweet2));
    }
}
