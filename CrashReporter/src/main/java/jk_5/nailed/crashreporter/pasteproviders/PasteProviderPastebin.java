package jk_5.nailed.crashreporter.pasteproviders;

import com.google.common.collect.Maps;
import jk_5.nailed.crashreporter.HttpUtils;
import jk_5.nailed.crashreporter.PasteProvider;

import java.net.URL;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class PasteProviderPastebin implements PasteProvider {

    @Override
    public String paste(String title, String text) throws PasteException{
        Map<String, String> vars = Maps.newHashMap();
        vars.put("api_dev_key", "70550f0bde9c48dc70fb37153c042348");
        vars.put("api_option", "paste");
        vars.put("api_paste_code", text);
        vars.put("api_paste_private", "1");
        vars.put("api_paste_name", title);
        vars.put("api_paste_expire_date", "N");
        vars.put("api_paste_format", "text");
        vars.put("api_user_key", "");

        try{
            return HttpUtils.post(new URL("http://pastebin.com/api/api_post.php"), vars).text.replaceAll("\n", "");
        }catch(Throwable e){
            throw new PasteException(e);
        }
    }
}
