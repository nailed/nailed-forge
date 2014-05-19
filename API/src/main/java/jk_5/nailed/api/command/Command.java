package jk_5.nailed.api.command;

import com.google.common.collect.ImmutableList;
import jk_5.nailed.api.plugin.Plugin;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class Command {

    private final String name;
    private final List<String> aliases;
    private Plugin owner;

    protected Command(@Nonnull String name) {
        this.name = "/" + name;
        this.aliases = Arrays.asList();
    }

    protected Command(@Nonnull String name, @Nonnull String... aliases) {
        this.name = "/" + name;
        this.aliases = Arrays.asList(aliases);
    }

    /**
     * Executes the command, returning its success
     *
     * @param sender Source object which is executing this command
     * @param alias The alias of the command used
     * @param args All arguments passed to the command, split via ' '
     */
    public abstract void execute(CommandSender sender, String alias, String[] args);

    /**
     * Executed on tab completion for this command, returning a list of
     * options the player can tab through.
     *
     * @param sender Source object which is executing this command
     * @param alias the alias being used
     * @param args All arguments passed to the command, split via ' '
     * @return a list of tab-completions for the specified arguments. This
     *     will never be null. List may be immutable.
     * @throws IllegalArgumentException if sender, alias, or args is null
     */
    @Nonnull
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        Validate.notNull(args, "Args cannot be null");

        return ImmutableList.of();
    }

    /**
     * Returns the name of this command
     *
     * @return Name of this command
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns a list of active aliases of this command
     *
     * @return List of aliases
     */
    public final List<String> getAliases() {
        return aliases;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.name + ")";
    }

    /**
     * Sets the owner of this command
     *
     * @param owner The owner of this command
     * @throws IllegalArgumentException if the owner is null
     */
    public final void setOwner(@Nonnull Plugin owner) throws IllegalArgumentException {
        Validate.notNull(owner, "Owner may not be null");

        this.owner = owner;
    }

    /**
     * Returns the owner of this command
     *
     * @return Owner of this command
     */
    @Nonnull
    public final Plugin getOwner() {
        return owner;
    }
}
