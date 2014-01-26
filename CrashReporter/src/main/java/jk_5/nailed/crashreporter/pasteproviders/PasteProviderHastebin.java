package jk_5.nailed.crashreporter.pasteproviders;

import jk_5.nailed.crashreporter.HttpUtils;
import jk_5.nailed.crashreporter.PasteProvider;

import java.net.URL;

/**
 * No description given
 *
 * @author jk-5
 */
public class PasteProviderHastebin implements PasteProvider {

    @Override
    public String paste(String title, String text) throws PasteException{
        try {
            String json = HttpUtils.post(new URL("http://hastebin.com/documents"), text).text;
            return "http://hastebin.com/" + json.substring(8, json.length() - 3) + ".hs";
        } catch (Throwable e) {
            throw new PasteException(e);
        }
    }
}
