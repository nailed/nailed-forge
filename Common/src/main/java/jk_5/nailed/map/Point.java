package jk_5.nailed.map;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.util.MathUtil;
import net.minecraft.util.ChunkCoordinates;

import javax.annotation.Nonnull;


/**
 * Created by matthias on 22-5-14.
 */
public class Point implements Cloneable{

    private double x;
    private double y;
    private double z;

    /**
     * Constructs a new Point with the given coordinates
     *
     * @param x The x-coordinate of this new point
     * @param y The y-coordinate of this new point
     * @param z The z-coordinate of this new point
     */
    public Point(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a new Point from an already existing Point
     *
     * @param point The point to get all the properties from
     */
    public Point(Point point){
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
    }

    /**
     * Constructs a new Point from {@link ChunkCoordinates}
     *
     * @param coords The {@link ChunkCoordinates} to copy
     */

    public Point(ChunkCoordinates coords){
        this.x = coords.posX;
        this.y = coords.posY;
        this.z = coords.posZ;
    }

    /**
     * Sets the x-coordinate of this point
     *
     * @param x X-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the x-coordinate of this point
     *
     * @return x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the floored value of the X component, indicating the block that
     * this point is contained with.
     *
     * @return block X
     */
    public int getBlockX(){
        return locToBlock(x);
    }

    /**
     * Sets the y-coordinate of this point
     *
     * @param y y-coordinate
     */
    public void setY(double y){
        this.y = y;
    }

    /**
     * Gets the y-coordinate of this point
     *
     * @return y-coordinate
     */
    public double getY(){
        return y;
    }

    /**
     * Gets the floored value of the Y component, indicating the block that
     * this point is contained with.
     *
     * @return block y
     */
    public int getBlockY(){
        return locToBlock(y);
    }

    /**
     * Sets the z-coordinate of this point
     *
     * @param z z-coordinate
     */
    public void setZ(double z){
        this.z = z;
    }

    /**
     * Gets the z-coordinate of this point
     *
     * @return z-coordinate
     */
    public double getZ(){
        return z;
    }

    /**
     * Gets the floored value of the Z component, indicating the block that
     * this point is contained with.
     *
     * @return block z
     */
    public int getBlockZ(){
        return locToBlock(z);
    }

    /**
     * Adds the point by another.
     *
     * @param vec The other point
     * @return the same point
     * @throws NullPointerException when vec is null
     */
    public Point add(@Nonnull Point vec) {
        Preconditions.checkNotNull(vec, "vec");

        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

        /**
         * Adds the point by another. Not world-aware.
         *
         * @param x X coordinate
         * @param y Y coordinate
         * @param z Z coordinate
         * @return the same point
         */
    public Point add(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    /**
     * Subtracts the point by another. Not world-aware and
     * orientation independent.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return the same point
     */
    public Point subtract(double x, double y, double z){
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    /**
     * Subtracts the point by another.
     *
     * @param vec The other point
     * @return the same point
     * @throws NullPointerException when vec is null
     */
    public Point subtract(Point vec){
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    /**
     * Get the distance between this point and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the point's magnitude. NaN will
     * be returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @param o The other point
     * @return the distance
     * @throws NullPointerException when o is null
     */
    public double distance(@Nonnull Point o) {
        Preconditions.checkNotNull(o, "o");

        return Math.sqrt(distanceSquared(o));
    }

    /**
     * Get the squared distance between this point and another.
     *
     * @param o The other point
     * @return the distance
     * @throws NullPointerException when o is null
     */
    public double distanceSquared(@Nonnull Point o) {
        Preconditions.checkNotNull(o, "o");

        return MathUtil.square(x - o.x) + MathUtil.square(y - o.y) + MathUtil.square(z - o.z);
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same point
     */
    public Point multiply(double m){
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    /**
     * Zero this point's components.
     *
     * @return the same point
     */
    public Point zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        return this;
    }

    /**
     * Safely converts a double (location coordinate) to an int (block
     * coordinate)
     *
     * @param loc Precise coordinate
     * @return Block coordinate
     */
    public static int locToBlock(double loc) {
        return MathUtil.floor(loc);
    }

    public void write(ByteBuf buffer){
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
    }

    @Override
    public int hashCode(){
        int hash = 3;

        hash = 19 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));

        return hash;
    }

    @Override
    public Point clone() {
        return new Point(this.x, this.y, this.z);
    }
}
