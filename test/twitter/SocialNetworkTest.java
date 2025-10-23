package twitter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        Map<String, Set<String>> hashtagUsers = new HashMap<>(); // hashtag -> users who used it

        Pattern mentionPattern = Pattern.compile("(?<=@)\\w+(-\\w+)*");
        Pattern hashtagPattern = Pattern.compile("#\\w+");

        for (Tweet t : tweets) {
            String author = t.getAuthor().toLowerCase();
            followsGraph.putIfAbsent(author, new HashSet<>());

            // Extract mentions
            Matcher mentionMatcher = mentionPattern.matcher(t.getText());
            while (mentionMatcher.find()) {
                String mentioned = mentionMatcher.group().toLowerCase();
                if (!mentioned.equals(author)) {
                    followsGraph.get(author).add(mentioned);
                }
            }

            // Extract hashtags
            Matcher hashtagMatcher = hashtagPattern.matcher(t.getText());
            while (hashtagMatcher.find()) {
                String hashtag = hashtagMatcher.group().toLowerCase();
                hashtagUsers.putIfAbsent(hashtag, new HashSet<>());
                hashtagUsers.get(hashtag).add(author);
            }
        }

        // Common hashtags → mutual follow relationship
        for (Set<String> users : hashtagUsers.values()) {
            for (String u1 : users) {
                for (String u2 : users) {
                    if (!u1.equals(u2)) {
                        followsGraph.putIfAbsent(u1, new HashSet<>());
                        followsGraph.get(u1).add(u2);
                    }
                }
            }
        }

        return followsGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCounts = new HashMap<>();

        for (String user : followsGraph.keySet()) {
            followerCounts.putIfAbsent(user, 0);
            for (String followed : followsGraph.get(user)) {
                followerCounts.put(followed, followerCounts.getOrDefault(followed, 0) + 1);
            }
        }

        List<String> users = new ArrayList<>(followerCounts.keySet());
        users.sort((a, b) -> followerCounts.get(b) - followerCounts.get(a));

        return users;
    }
}
