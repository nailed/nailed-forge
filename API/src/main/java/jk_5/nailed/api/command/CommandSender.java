package jk_5.nailed.api.command;

import jk_5.nailed.api.map.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public interface CommandSender {

    void sendMessage(String message);

    void sendMessage(String... messages);

    void sendMessage(Iterable<String> messages);

    String getName();

    Map getMap();
}
