package jk_5.nailed.api.effect.firework;

import java.util.*;

import com.google.common.collect.*;

import org.apache.commons.lang3.*;

import net.minecraft.nbt.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class FireworkEffect {

    private static final String FLICKER = "flicker";
    private static final String TRAIL = "trail";
    private static final String COLORS = "colors";
    private static final String FADE_COLORS = "fade-colors";
    private static final String TYPE = "type";

    private final boolean flicker;
    private final boolean trail;
    private final ImmutableList<Color> colors;
    private final ImmutableList<Color> fadeColors;
    private final Type type;
    private String string = null;

    FireworkEffect(boolean flicker, boolean trail, ImmutableList<Color> colors, ImmutableList<Color> fadeColors, Type type) {
        if(colors.isEmpty()){
            throw new IllegalStateException("Cannot make FireworkEffect without any color");
        }
        this.flicker = flicker;
        this.trail = trail;
        this.colors = colors;
        this.fadeColors = fadeColors;
        this.type = type;
    }

    /**
     * The type or shape of the effect.
     */
    public enum Type {
        /**
         * A small ball effect.
         */
        BALL,
        /**
         * A large ball effect.
         */
        BALL_LARGE,
        /**
         * A star-shaped effect.
         */
        STAR,
        /**
         * A burst effect.
         */
        BURST,
        /**
         * A creeper-face effect.
         */
        CREEPER
    }

    /**
     * Construct a firework effect.
     *
     * @return A utility object for building a firework effect
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * This is a builder for FireworkEffects.
     *
     * @see FireworkEffect#builder()
     */
    public static final class Builder {

        private boolean flicker = false;
        private boolean trail = false;
        private final ImmutableList.Builder<Color> colors = ImmutableList.builder();
        private ImmutableList.Builder<Color> fadeColors = null;
        private Type type = Type.BALL;

        private Builder() {
        }

        /**
         * Specify the type of the firework effect.
         *
         * @param type The effect type
         * @return This object, for chaining
         * @throws IllegalArgumentException If type is null
         */
        public Builder with(Type type) throws IllegalArgumentException {
            Validate.notNull(type, "Cannot have null type");
            this.type = type;
            return this;
        }

        /**
         * Add a flicker to the firework effect.
         *
         * @return This object, for chaining
         */
        public Builder withFlicker() {
            flicker = true;
            return this;
        }

        /**
         * Set whether the firework effect should flicker.
         *
         * @param flicker true if it should flicker, false if not
         * @return This object, for chaining
         */
        public Builder flicker(boolean flicker) {
            this.flicker = flicker;
            return this;
        }

        /**
         * Add a trail to the firework effect.
         *
         * @return This object, for chaining
         */
        public Builder withTrail() {
            trail = true;
            return this;
        }

        /**
         * Set whether the firework effect should have a trail.
         *
         * @param trail true if it should have a trail, false for no trail
         * @return This object, for chaining
         */
        public Builder trail(boolean trail) {
            this.trail = trail;
            return this;
        }

        /**
         * Add a primary color to the firework effect.
         *
         * @param color The color to add
         * @return This object, for chaining
         * @throws IllegalArgumentException If color is null
         */
        public Builder withColor(Color color) throws IllegalArgumentException {
            Validate.notNull(color, "Cannot have null color");

            colors.add(color);

            return this;
        }

        /**
         * Add several primary colors to the firework effect.
         *
         * @param colors The colors to add
         * @return This object, for chaining
         * @throws IllegalArgumentException If colors is null
         * @throws IllegalArgumentException If any color is null (may be
         *                                  thrown after changes have occurred)
         */
        public Builder withColor(Color... colors) throws IllegalArgumentException {
            Validate.notNull(colors, "Cannot have null colors");
            if(colors.length == 0){
                return this;
            }

            ImmutableList.Builder<Color> list = this.colors;
            for(Color color : colors){
                Validate.notNull(color, "Color cannot be null");
                list.add(color);
            }

            return this;
        }

        /**
         * Add several primary colors to the firework effect.
         *
         * @param colors An iterable object whose iterator yields the desired
         *               colors
         * @return This object, for chaining
         * @throws IllegalArgumentException If colors is null
         * @throws IllegalArgumentException If any color is null (may be
         *                                  thrown after changes have occurred)
         */
        public Builder withColor(Iterable<?> colors) throws IllegalArgumentException {
            Validate.notNull(colors, "Cannot have null colors");

            ImmutableList.Builder<Color> list = this.colors;
            for(Object color : colors){
                if(!(color instanceof Color)){
                    throw new IllegalArgumentException(color + " is not a Color in " + colors);
                }
                list.add((Color) color);
            }

            return this;
        }

        /**
         * Add a fade color to the firework effect.
         *
         * @param color The color to add
         * @return This object, for chaining
         * @throws IllegalArgumentException If colors is null
         * @throws IllegalArgumentException If any color is null (may be
         *                                  thrown after changes have occurred)
         */
        public Builder withFade(Color color) throws IllegalArgumentException {
            Validate.notNull(color, "Cannot have null color");

            if(fadeColors == null){
                fadeColors = ImmutableList.builder();
            }

            fadeColors.add(color);

            return this;
        }

        /**
         * Add several fade colors to the firework effect.
         *
         * @param colors The colors to add
         * @return This object, for chaining
         * @throws IllegalArgumentException If colors is null
         * @throws IllegalArgumentException If any color is null (may be
         *                                  thrown after changes have occurred)
         */
        public Builder withFade(Color... colors) throws IllegalArgumentException {
            Validate.notNull(colors, "Cannot have null colors");
            if(colors.length == 0){
                return this;
            }

            ImmutableList.Builder<Color> list = this.fadeColors;
            if(list == null){
                list = this.fadeColors = ImmutableList.builder();
            }

            for(Color color : colors){
                Validate.notNull(color, "Color cannot be null");
                list.add(color);
            }

            return this;
        }

        /**
         * Add several fade colors to the firework effect.
         *
         * @param colors An iterable object whose iterator yields the desired
         *               colors
         * @return This object, for chaining
         * @throws IllegalArgumentException If colors is null
         * @throws IllegalArgumentException If any color is null (may be
         *                                  thrown after changes have occurred)
         */
        public Builder withFade(Iterable<?> colors) throws IllegalArgumentException {
            Validate.notNull(colors, "Cannot have null colors");

            ImmutableList.Builder<Color> list = this.fadeColors;
            if(list == null){
                list = this.fadeColors = ImmutableList.builder();
            }

            for(Object color : colors){
                if(!(color instanceof Color)){
                    throw new IllegalArgumentException(color + " is not a Color in " + colors);
                }
                list.add((Color) color);
            }

            return this;
        }

        /**
         * Create a {@link FireworkEffect} from the current contents of this
         * builder.
         * <p/>
         * To successfully build, you must have specified at least one color.
         *
         * @return The representative firework effect
         */
        public FireworkEffect build() {
            return new FireworkEffect(
                    flicker,
                    trail,
                    colors.build(),
                    fadeColors == null ? ImmutableList.<Color>of() : fadeColors.build(),
                    type
            );
        }
    }

    /**
     * Get whether the firework effect flickers.
     *
     * @return true if it flickers, false if not
     */
    public boolean hasFlicker() {
        return flicker;
    }

    /**
     * Get whether the firework effect has a trail.
     *
     * @return true if it has a trail, false if not
     */
    public boolean hasTrail() {
        return trail;
    }

    /**
     * Get the primary colors of the firework effect.
     *
     * @return An immutable list of the primary colors
     */
    public List<Color> getColors() {
        return colors;
    }

    /**
     * Get the fade colors of the firework effect.
     *
     * @return An immutable list of the fade colors
     */
    public List<Color> getFadeColors() {
        return fadeColors;
    }

    /**
     * Get the type of the firework effect.
     *
     * @return The effect type
     */
    public Type getType() {
        return type;
    }

    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>of(
                FLICKER, flicker,
                TRAIL, trail,
                COLORS, colors,
                FADE_COLORS, fadeColors,
                TYPE, type.name()
        );
    }

    @Override
    public String toString() {
        final String string = this.string;
        if(string == null){
            return this.string = "FireworkEffect:" + serialize();
        }
        return string;
    }

    @Override
    public int hashCode() {
        /**
         * TRUE and FALSE as per boolean.hashCode()
         */
        final int prime = 31, tr = 1231, fl = 1237;
        int hash = 1;
        hash = hash * prime + (flicker ? tr : fl);
        hash = hash * prime + (trail ? tr : fl);
        hash = hash * prime + type.hashCode();
        hash = hash * prime + colors.hashCode();
        hash = hash * prime + fadeColors.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }

        if(!(obj instanceof FireworkEffect)){
            return false;
        }

        FireworkEffect that = (FireworkEffect) obj;
        return this.flicker == that.flicker
                && this.trail == that.trail
                && this.type == that.type
                && this.colors.equals(that.colors)
                && this.fadeColors.equals(that.fadeColors);
    }

    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Flicker", (byte) (this.flicker ? 1 : 0));
        tag.setByte("Trail", (byte) (this.trail ? 1 : 0));
        tag.setByte("Type", (byte) this.type.ordinal());
        int[] colors = new int[this.colors.size()];
        for(int i = 0; i < colors.length; i++){
            colors[i] = this.colors.get(i).asRGB();
        }
        tag.setIntArray("Colors", colors);
        int[] fadeColors = new int[this.fadeColors.size()];
        for(int i = 0; i < fadeColors.length; i++){
            fadeColors[i] = this.fadeColors.get(i).asRGB();
        }
        tag.setIntArray("FadeColors", fadeColors);
        return tag;
    }

    public Firework toFirework() {
        Firework firework = new Firework();
        firework.setPower(1);
        firework.addEffect(this);
        return firework;
    }

    public Firework toFirework(int power) {
        Firework firework = new Firework();
        firework.setPower(power);
        firework.addEffect(this);
        return firework;
    }
}
