package jk_5.nailed.map;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.util.MathUtil;
import net.minecraft.util.ChunkCoordinates;

import javax.annotation.Nonnull;

/**
 * Represents a 3-dimensional location in a world
 *
 * @author jk-5
 */
public class Location extends Point implements Cloneable {

    private float yaw;
    private float pitch;

    /**
     * Constructs a new Location with the given coordinates
     *
     * @param x The x-coordinate of this new location
     * @param y The y-coordinate of this new location
     * @param z The z-coordinate of this new location
     */
    public Location(final double x, final double y, final double z){
        this(x, y, z, 0, 0);
    }

    /**
     * Constructs a new Location with the given coordinates and direction
     *
     * @param x The x-coordinate of this new location
     * @param y The y-coordinate of this new location
     * @param z The z-coordinate of this new location
     * @param yaw The absolute rotation on the x-plane, in degrees
     * @param pitch The absolute rotation on the y-plane, in degrees
     */
    public Location(final double x, final double y, final double z, final float yaw, final float pitch){
        super(x, y, z);
        this.pitch = pitch;
        this.yaw = yaw;
    }

    /**
     * Constructs a new Location from an already existing Location
     *
     * @param location The location to get all the properties from
     */
    public Location(final Location location){
        super(location);
        this.pitch = location.pitch;
        this.yaw = location.yaw;
    }

    /**
     * Constructs a new Location from {@link ChunkCoordinates}
     *
     * @param coords The {@link ChunkCoordinates} to copy
     */
    public Location(final ChunkCoordinates coords){
        this(coords, 0, 0);
    }

    /**
     * Constructs a new Location from {@link ChunkCoordinates}
     *
     * @param coords The {@link ChunkCoordinates} to copy
     * @param yaw The absolute rotation on the x-plane, in degrees
     * @param pitch The absolute rotation on the y-plane, in degrees
     */
    public Location(final ChunkCoordinates coords, final float yaw, final float pitch){
        super(coords);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Sets the yaw of this location, measured in degrees.
     * <ul>
     * <li>A yaw of 0 or 360 represents the positive z direction.
     * <li>A yaw of 180 represents the negative z direction.
     * <li>A yaw of 90 represents the negative x direction.
     * <li>A yaw of 270 represents the positive x direction.
     * </ul>
     * Increasing yaw values are the equivalent of turning to your
     * right-facing, increasing the scale of the next respective axis, and
     * decreasing the scale of the previous axis.
     *
     * @param yaw new rotation's yaw
     */
    public void setYaw(float yaw){
        this.yaw = yaw;
    }

    /**
     * Gets the yaw of this location, measured in degrees.
     * <ul>
     * <li>A yaw of 0 or 360 represents the positive z direction.
     * <li>A yaw of 180 represents the negative z direction.
     * <li>A yaw of 90 represents the negative x direction.
     * <li>A yaw of 270 represents the positive x direction.
     * </ul>
     * Increasing yaw values are the equivalent of turning to your
     * right-facing, increasing the scale of the next respective axis, and
     * decreasing the scale of the previous axis.
     *
     * @return the rotation's yaw
     */
    public float getYaw(){
        return yaw;
    }

    /**
     * Sets the pitch of this location, measured in degrees.
     * <ul>
     * <li>A pitch of 0 represents level forward facing.
     * <li>A pitch of 90 represents downward facing, or negative y
     *     direction.
     * <li>A pitch of -90 represents upward facing, or positive y direction.
     * <ul>
     * Increasing pitch values the equivalent of looking down.
     *
     * @param pitch new incline's pitch
     */
    public void setPitch(float pitch){
        this.pitch = pitch;
    }

    /**
     * Sets the pitch of this location, measured in degrees.
     * <ul>
     * <li>A pitch of 0 represents level forward facing.
     * <li>A pitch of 90 represents downward facing, or negative y
     *     direction.
     * <li>A pitch of -90 represents upward facing, or positive y direction.
     * <ul>
     * Increasing pitch values the equivalent of looking down.
     *
     * @return the incline's pitch
     */
    public float getPitch(){
        return pitch;
    }

    /**
     * Adds the location by another.
     *
     * @param vec The other location
     * @return the same location
     * @throws NullPointerException when vec is null
     */
    public Location add(@Nonnull Location vec){
        Preconditions.checkNotNull(vec, "vec");

        super.add(vec);
        return this;
    }

    /**
     * Adds the location by another. Not world-aware.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return the same location
     */
    public Location add(double x, double y, double z) {
        super.add(x, y, z);
        return this;
    }

    /**
     * Subtracts the location by another.
     *
     * @param vec The other location
     * @return the same location
     * @throws NullPointerException when vec is null
     */
    public Location subtract(@Nonnull Location vec) {
        Preconditions.checkNotNull(vec, "vec");
        super.subtract(vec);
        return this;
    }

    /**
     * Subtracts the location by another. Not world-aware and
     * orientation independent.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return the same location
     */
    public Location subtract(double x, double y, double z) {
        super.subtract(x, y, z);
        return this;
    }

    /**
     * Get the distance between this location and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the location's magnitude. NaN will
     * be returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @param o The other location
     * @return the distance
     * @throws NullPointerException when o is null
     */
    public double distance(@Nonnull Location o) {
        Preconditions.checkNotNull(o, "o");

        return super.distance(o);
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same location
     */
    @Override
    public Location multiply(double m) {
        super.multiply(m);
        return this;
    }

    /**
     * Zero this location's components.
     *
     * @return the same location
     */
    @Override
    public Location zero() {
        super.zero();
        return this;
    }

    public ChunkCoordinates toChunkCoordinates(){
        return new ChunkCoordinates(this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;

        if (!super.equals(obj)){
            return false;
        }
        if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
            return false;
        }
        if (Float.floatToIntBits(this.yaw) != Float.floatToIntBits(other.yaw)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 19 * hash + Float.floatToIntBits(this.pitch);
        hash = 19 * hash + Float.floatToIntBits(this.yaw);
        return hash;
    }

    @Override
    public String toString() {
        return "Location{x=" + this.getX() + ",y=" + this.getY() + ",z=" + this.getZ() + ",pitch=" + pitch + ",yaw=" + yaw + '}';
    }

    @Override
    public Location clone() {
        return new Location(this.getX(), this.getY(), this.getZ(), this.pitch, this.yaw);
    }

    public static Location readFrom(JsonObject json){
        double x = json.has("x") ? json.get("x").getAsDouble() : 0;
        double y = json.has("y") ? json.get("y").getAsDouble() : 64;
        double z = json.has("z") ? json.get("z").getAsDouble() : 0;
        float yaw = json.has("yaw") ? json.get("yaw").getAsFloat() : 0;
        float pitch = json.has("pitch") ? json.get("pitch").getAsFloat() : 0;
        return new Location(x, y, z, yaw, pitch);
    }

    public static Location read(ByteBuf buffer) {
        return new Location(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat(), buffer.readFloat());
    }

    @Override
    public void write(ByteBuf buffer) {
        super.write(buffer);
        buffer.writeFloat(this.yaw);
        buffer.writeFloat(this.pitch);
    }
}
