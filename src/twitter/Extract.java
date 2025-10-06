/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        // handle empty input defensively
        if (tweets.isEmpty()) {
            throw new IllegalArgumentException("tweet list must not be empty");
        }

        // initialize with first tweet’s timestamp
        Instant start = tweets.get(0).getTimestamp();
        Instant end = start;

        // find earliest and latest timestamps
        for (Tweet t : tweets) {
            Instant time = t.getTimestamp();
            if (time.isBefore(start)) {
                start = time;
            }
            if (time.isAfter(end)) {
                end = time;
            }
        }

        return new Timespan(start, end);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> mentionedUsers = new HashSet<>();

        // Regular expression to match valid mentions
        // (?<![A-Za-z0-9_]) ensures the '@' is not part of a longer word
        // ([A-Za-z0-9_]+) captures the username itself
        // (?![A-Za-z0-9_]) ensures it is not followed by another valid username char
        Pattern mentionPattern = Pattern.compile("(?<![A-Za-z0-9_])@([A-Za-z0-9_]+)(?![A-Za-z0-9_])");

        for (Tweet t : tweets) {
            String text = t.getText();
            Matcher matcher = mentionPattern.matcher(text);
            while (matcher.find()) {
                // usernames are case-insensitive → store lowercase
                String username = matcher.group(1).toLowerCase();
                mentionedUsers.add(username);
            }
        }

        return mentionedUsers;
    }
}
