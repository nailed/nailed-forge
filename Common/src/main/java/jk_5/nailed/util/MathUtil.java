package jk_5.nailed.util;

/**
 * No description given
 *
 * @author jk-5
 */
public final class MathUtil {
    private MathUtil(){}

    public static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public static double square(double num) {
        return num * num;
    }
}
