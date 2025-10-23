package twitter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * Evidence sources:
     *  - Mentions (@username)
     *  - Common hashtags (#topic)
     * 
     * The graph maps each user → set of users they might follow.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        Map<String, Set<String>> hashtagUsers = new HashMap<>();

        Pattern mentionPattern = Pattern.compile("(?<=@)\\w+(-\\w+)*");
        Pattern hashtagPattern = Pattern.compile("#\\w+");

        for (Tweet t : tweets) {
            String author = t.getAuthor().toLowerCase();
            Set<String> follows = new HashSet<>();

            // Extract mentions
            Matcher mentionMatcher = mentionPattern.matcher(t.getText());
            while (mentionMatcher.find()) {
                String mentioned = mentionMatcher.group().toLowerCase();
                if (!mentioned.equals(author)) {
                    follows.add(mentioned);
                }
            }

            // Extract hashtags
            Matcher hashtagMatcher = hashtagPattern.matcher(t.getText());
            while (hashtagMatcher.find()) {
                String hashtag = hashtagMatcher.group().toLowerCase();
                hashtagUsers.computeIfAbsent(hashtag, k -> new HashSet<>()).add(author);
            }

            // Merge follows for this author into the global graph (don't overwrite)
            if (!follows.isEmpty()) {
                followsGraph.computeIfAbsent(author, k -> new HashSet<>()).addAll(follows);
            }
        }

        // Add follow edges between users who share hashtags (common interest)
        for (Set<String> users : hashtagUsers.values()) {
            for (String u1 : users) {
                for (String u2 : users) {
                    if (!u1.equals(u2)) {
                        followsGraph.computeIfAbsent(u1, k -> new HashSet<>()).add(u2);
                    }
                }
            }
        }

        return followsGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence.
     * 
     * The influencer list is sorted in descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCounts = new HashMap<>();

        // Count followers
        for (String user : followsGraph.keySet()) {
            for (String followed : followsGraph.get(user)) {
                followerCounts.put(followed, followerCounts.getOrDefault(followed, 0) + 1);
            }
        }

        // Sort by follower count descending
        List<String> users = new ArrayList<>(followerCounts.keySet());
        users.sort(Comparator.<String>comparingInt(followerCounts::get).reversed());

        return users;
    }
}
