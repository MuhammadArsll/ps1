package twitter;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.junit.Test;

public class MySocialNetworkTest {

    private static final Instant time1 = Instant.parse("2020-01-01T12:00:00Z");
    private static final Instant time2 = Instant.parse("2020-01-02T12:00:00Z");
    private static final Instant time3 = Instant.parse("2020-01-03T12:00:00Z");

    /**
     * 1. Empty List of Tweets
     * Ensures that an empty list results in an empty graph.
     */
    @Test
    public void testEmptyTweetList() {
        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of());
        assertTrue("Graph should be empty for empty tweet list", graph.isEmpty());
    }

    /**
     * 2. Tweets Without Mentions
     * Verifies that tweets with no mentions do not add entries to the graph.
     */
    @Test
    public void testTweetsWithoutMentions() {
        Tweet t1 = new Tweet(1, "alice", "Just another sunny day", time1);
        Tweet t2 = new Tweet(2, "bob", "Coding is fun!", time2);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of(t1, t2));
        assertTrue("No mentions should lead to an empty graph", graph.isEmpty());
    }

    /**
     * 3. Single Mention
     * Tests whether a user who mentions someone is correctly added to the graph.
     */
    @Test
    public void testSingleMention() {
        Tweet t1 = new Tweet(1, "alice", "Hi @bob!", time1);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of(t1));
        assertTrue("alice should follow bob", graph.get("alice").contains("bob"));
    }

    /**
     * 4. Multiple Mentions
     * Checks if multiple mentioned users are linked to the tweet author.
     */
    @Test
    public void testMultipleMentions() {
        Tweet t1 = new Tweet(1, "alice", "Hey @bob and @charlie!", time1);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of(t1));
        Set<String> follows = graph.get("alice");

        assertEquals("alice should follow two users", 2, follows.size());
        assertTrue(follows.containsAll(List.of("bob", "charlie")));
    }

    /**
     * 5. Multiple Tweets from One User
     * Ensures that repeated mentions from the same user are captured.
     */
    @Test
    public void testMultipleTweetsFromOneUser() {
        Tweet t1 = new Tweet(1, "alice", "Hey @bob!", time1);
        Tweet t2 = new Tweet(2, "alice", "Also hello @charlie!", time2);

        Map<String, Set<String>> graph = SocialNetwork.guessFollowsGraph(List.of(t1, t2));
        Set<String> follows = graph.get("alice");

        assertTrue(follows.contains("bob"));
        assertTrue(follows.contains("charlie"));
        assertEquals("alice should follow both bob and charlie", 2, follows.size());
    }

    /**
     * 6. Empty Graph for influencers()
     * Verifies that no users yield an empty influencer list.
     */
    @Test
    public void testEmptyGraphInfluencers() {
        Map<String, Set<String>> graph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(graph);

        assertTrue("Empty graph should yield empty influencer list", influencers.isEmpty());
    }

    /**
     * 7. Single User Without Followers
     * Tests that a user without followers yields no influencers.
     */
    @Test
    public void testSingleUserNoFollowers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("alice", Set.of());

        List<String> influencers = SocialNetwork.influencers(graph);
        assertTrue("User with no followers shouldn't appear as influencer", influencers.isEmpty());
    }

    /**
     * 8. Single Influencer
     * Verifies correct identification of the only influencer.
     */
    @Test
    public void testSingleInfluencer() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("bob", Set.of("alice")); // alice follows bob

        List<String> influencers = SocialNetwork.influencers(graph);
        assertEquals("bob should be the only influencer", List.of("alice"), influencers);
    }

    /**
     * 9. Multiple Influencers
     * Tests for correct influencer ordering.
     */
    @Test
    public void testMultipleInfluencers() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("bob", Set.of("alice", "charlie"));
        graph.put("charlie", Set.of("alice"));
        graph.put("david", Set.of("charlie"));

        List<String> influencers = SocialNetwork.influencers(graph);

        // alice has 2 followers, charlie has 2, david has 0
        assertEquals("Influencer list should start with alice or charlie", 
            Set.of("alice", "charlie"), Set.copyOf(influencers.subList(0, 2)));
    }

    /**
     * 10. Tied Influence
     * Ensures equal influencers are handled correctly.
     */
    @Test
    public void testTiedInfluence() {
        Map<String, Set<String>> graph = new HashMap<>();
        graph.put("bob", Set.of("alice"));
        graph.put("charlie", Set.of("alice"));
        graph.put("david", Set.of("bob"));

        List<String> influencers = SocialNetwork.influencers(graph);
        assertTrue("alice and bob both have one follower each",
                influencers.containsAll(List.of("alice", "bob")));
    }
}
