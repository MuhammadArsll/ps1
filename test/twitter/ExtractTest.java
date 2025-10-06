/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * Testing strategy
     * 
     * getTimespan():
     *  tweets.size() = 1
     *  tweets.size() > 1 (in order and reversed order)
     *  all timestamps same
     * 
     * getMentionedUsers():
     *  no mentions
     *  one mention
     *  multiple mentions
     *  mention at start, middle, and end of text
     *  mixed case usernames
     *  ignore email-like text (e.g. alice@mit.edu)
     *  repeated mentions (should appear once)
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T09:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // ---------- getTimespan() tests ----------

    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Collections.singletonList(tweet1));

        assertEquals("start == end for one tweet", d1, timespan.getStart());
        assertEquals("start == end for one tweet", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanOutOfOrderTweets() {
        Tweet earlier = new Tweet(3, "bob", "morning tweet", d3);
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, earlier));

        assertEquals("expected earliest start", d3, timespan.getStart());
        assertEquals("expected latest end", d2, timespan.getEnd());
    }

    @Test
    public void testGetTimespanSameTimestamps() {
        Tweet t1 = new Tweet(4, "user1", "same time", d1);
        Tweet t2 = new Tweet(5, "user2", "same time", d1);
        Timespan timespan = Extract.getTimespan(Arrays.asList(t1, t2));

        assertEquals("start == end when same timestamp", d1, timespan.getStart());
        assertEquals("start == end when same timestamp", d1, timespan.getEnd());
    }

    // ---------- getMentionedUsers() tests ----------

    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersSingleMention() {
        Tweet t = new Tweet(6, "alice", "Hello @bob!", d1);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue("expected mention of bob", mentionedUsers.contains("bob"));
        assertEquals("expected one mention", 1, mentionedUsers.size());
    }

    @Test
    public void testGetMentionedUsersMultipleMentions() {
        Tweet t = new Tweet(7, "bob", "@alice and @charlie are awesome!", d2);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue(mentionedUsers.contains("alice"));
        assertTrue(mentionedUsers.contains("charlie"));
        assertEquals(2, mentionedUsers.size());
    }

    @Test
    public void testGetMentionedUsersCaseInsensitive() {
        Tweet t = new Tweet(8, "alice", "Hi @BOB and @bob", d2);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue("mentions should be case-insensitive", mentionedUsers.contains("bob"));
        assertEquals(1, mentionedUsers.size());
    }

    @Test
    public void testGetMentionedUsersIgnoreEmail() {
        Tweet t = new Tweet(9, "bob", "Contact me at bob@mit.edu", d2);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue("should not treat email as mention", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersRepeatedMention() {
        Tweet t = new Tweet(10, "bob", "Thanks @alice and again @Alice!", d2);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(t));
        assertTrue(mentionedUsers.contains("alice"));
        assertEquals("mentions should not repeat", 1, mentionedUsers.size());
    }
}
