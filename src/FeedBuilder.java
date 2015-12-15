import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.WireFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * @author habeeb
 * 6/29/15
 * Copyright (c) 2015 Habeeb Hooshmand
 */
public class FeedBuilder {

    private ArrayList<String> mUrls;
    private SyndFeed aggFeed;

    public FeedBuilder() {
        mUrls = new ArrayList<>();
        aggFeed = new SyndFeedImpl();
    }

    public FeedBuilder(List<String> urls) {
        this();
        mUrls.addAll(urls);
    }

    public FeedBuilder(File file) {
        this();
        try {
            addUrlsFromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void aggregateFeeds() {
        aggFeed.setTitle("Aggregated Feed");
        aggFeed.setDescription("Aggregated Feed");
        aggFeed.setAuthor("Habeeb Hooshmand");

        List<Object> entries = new ArrayList<>();

        for (String url : mUrls)
            try {
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(new URL(url)));
                entries.addAll(feed.getEntries());
            } catch (IOException | FeedException e) {
                System.out.println("Error reading: " + url);
            }

        aggFeed.setEntries(entries);
    }

    public SyndFeed build() {
        aggregateFeeds();
        return aggFeed;
    }

    public void addFromOPML(File file) throws IOException {
        WireFeedInput in = new WireFeedInput();
        /*Opml feed = (Opml) in.build(file);
        List<Outline> outlines = feed.getOutlines();
        for (Outline outline : outlines) addUrl(outline.getXmlUrl());*/
    }

    public void addUrl(String url) {
        mUrls.add(url);
    }

    public void addUrls(Collection<String> urls) {
        mUrls.addAll(urls);
    }

    public void setUrls(ArrayList<String> urls) {
        mUrls = urls;
    }

    public void clearUrls() {
        mUrls.clear();
    }

    public void addUrlsFromFile(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String url = sc.nextLine();
            if (!url.isEmpty())
                addUrl(url);
        }
    }

    public Collection<String> getUrls() {
        return mUrls;
    }
}
