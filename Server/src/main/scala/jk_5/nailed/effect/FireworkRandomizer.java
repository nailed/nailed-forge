package jk_5.nailed.effect;

import java.util.*;

import jk_5.nailed.api.effect.firework.*;

/**
 * No description given
 *
 * @author jk-5
 */
public final class FireworkRandomizer {

    private static Random random = new Random();

    private FireworkRandomizer(){

    }

    public static FireworkEffect getRandomEffect() {
        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.withColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        if(random.nextInt(2) == 0){
            builder.withFade(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        }
        switch(random.nextInt(4)){
            case 0:
                builder.with(FireworkEffect.Type.BALL);
                break;
            case 1:
                builder.with(FireworkEffect.Type.BALL_LARGE);
                break;
            case 2:
                builder.with(FireworkEffect.Type.BURST);
                break;
            case 3:
                builder.with(FireworkEffect.Type.CREEPER);
                break;
            case 4:
                builder.with(FireworkEffect.Type.STAR);
                break;
        }
        builder.flicker(random.nextInt(2) == 0);
        builder.trail(random.nextInt(2) == 0);
        return builder.build();
    }
}
