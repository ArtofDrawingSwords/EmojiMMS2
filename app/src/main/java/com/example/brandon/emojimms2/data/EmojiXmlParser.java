package com.example.brandon.emojimms2.data;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brandon on 8/24/2017.
 */

// Todo: set up xml parser to check version before parsing and replacing document
public class EmojiXmlParser {

    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "emojilist");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("emoji")) {
                entries.add(readEmojiInfo(parser));
            }
        }
        return entries;
    }

    private Emoji readEmojiInfo(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "emoji");
        String name = parser.getAttributeValue(null, "name");
        String url = parser.getAttributeValue(null, "url");
        return new Emoji(name, url);
    }

    public static class Emoji {
        public final String name;
        public final String url;

        private Emoji(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }
}
