package jk_5.nailed.updater;

import org.junit.Ignore;

/**
 * No description given
 *
 * @author jk-5
 */
@Ignore
public class TestUtils {

    public static String shuffleCase(String input){
        char[] chars = input.toCharArray();
        for(int i = 0; i < chars.length; i += 2){
            chars[i] = Character.toUpperCase(chars[i]);
        }
        return new String(chars);
    }
}
