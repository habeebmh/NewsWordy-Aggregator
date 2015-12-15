
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import java.io.*;
import java.util.*;

/**
 * @author habeeb
 * 6/29/15
 * Copyright (c) 2015 Habeeb Hooshmand
 */
public class Main {

    static String feedFile = "";
    static String outFilePath = "";
    // static String badwordsText = "";


    public static void main(String[] args) {
        Main main = new Main();
        feedFile = args[0];
        outFilePath = args[1];
        try {
            main.gatherNews();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void gatherNews() throws FileNotFoundException {
        String date = Calendar.getInstance().getTime().toString();
        System.out.println(date);
        System.out.println("Feed: " + feedFile);
        System.out.println("Gathering Articles");
        FeedBuilder ultimateFeed = new FeedBuilder(new File(feedFile));
        List<String> ignored = getIgnoredWords();

        SyndFeed ultimate = ultimateFeed.build();
        String text = "";
        int total = 0;

        Set<Map.Entry<String, Integer>> entrySet = countedTitleWords(ultimate).entrySet();

        for (Map.Entry<String, Integer> e : entrySet)
            if (e.getValue() >= 10 && !ignored.contains(e.getKey())) {
                text += e.getKey() + "::" + e.getValue() + " ";
                total++;
            }

        try {
            makeFile(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Feeds: " + ultimateFeed.getUrls().size());
        System.out.println("Articles: " + ultimate.getEntries().size());
        System.out.println("Words: " + entrySet.size() + " (" + total + ")");
    }


    private void makeFile(String text) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new FileOutputStream(outFilePath));
        out.print(text);
        out.close();
        out.flush();

        System.out.println("Made file " + outFilePath);
    }

    public String[] splitTitle(SyndEntry entry) throws FileNotFoundException {
        String text = entry.getTitle()
                .toLowerCase()
                .replaceAll("[^a-zA-z ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        String[] split = text.split(" ");
        for (int i = 0; i < split.length; i++) {
            String word = split[i];
            if (word.length() < 2)
                split[i] = "";
        }

        return split;
    }

    private HashMap<String, Integer> countedTitleWords(SyndFeed feed) {
        HashMap<String, Integer> titleWords = new HashMap<>();

        try {
            for (Object o : feed.getEntries()) {
                SyndEntry entry = (SyndEntry) o;
                String[] words = splitTitle(entry);

                for (String word : words)
                    if (!word.isEmpty())
                        if (titleWords.get(word) != null) {
                            int i = titleWords.get(word);
                            titleWords.put(word, i + 1);
                        } else titleWords.put(word, 1);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return titleWords;
    }

    public List<String> getIgnoredWords() throws FileNotFoundException {
        return Arrays.asList(BadWords.BAD_WORDS);
    }

}
