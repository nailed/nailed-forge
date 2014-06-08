package jk_5.nailed.server.command;

import javax.annotation.Nonnull;

import net.minecraft.command.ICommand;

/**
 * No description given
 *
 * @author jk-5
 */
abstract class ComparedCommand implements ICommand {

    @Override
    public final int compareTo(@Nonnull Object o) {
        return this.compareTo((ICommand) o);
    }

    public final int compareTo(ICommand command) {
        return this.getCommandName().compareTo(command.getCommandName());
    }
}
