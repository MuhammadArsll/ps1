/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.ArrayList;
import java.util.List;

public class Filter {

    /**
     * Find tweets written by a particular user.
     * 
     * @param tweets list of tweets with distinct ids, not modified by this method.
     * @param username Twitter username, required to be a valid Twitter username (but
     *                 not necessarily appearing in the list of tweets).
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     */
    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        List<Tweet> result = new ArrayList<>();
        for (Tweet tweet : tweets) {
            if (tweet.getAuthor().equalsIgnoreCase(username)) {
                result.add(tweet);
            }
        }
        return result;
    }

    /**
     * Find tweets that were sent during a particular timespan.
     * 
     * @param tweets list of tweets with distinct ids, not modified by this method.
     * @param timespan timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     */
    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        List<Tweet> result = new ArrayList<>();
        for (Tweet tweet : tweets) {
            if (!tweet.getTimestamp().isBefore(timespan.getStart()) &&
                !tweet.getTimestamp().isAfter(timespan.getEnd())) {
                result.add(tweet);
            }
        }
        return result;
    }

    /**
     * Find tweets that contain at least one of the specified words.
     * 
     * @param tweets list of tweets with distinct ids, not modified by this method.
     * @param words list of words to search for, case-insensitive.
     * @return all and only the tweets in the list that contain at least one of the
     *         words (in any case), in the same order as in the input list.
     */
    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        List<Tweet> result = new ArrayList<>();
        for (Tweet tweet : tweets) {
            String text = tweet.getText().toLowerCase();
            for (String word : words) {
                if (text.contains(word.toLowerCase())) {
                    result.add(tweet);
                    break; // move to next tweet
                }
            }
        }
        return result;
    }
}
