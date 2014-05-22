package jk_5.nailed.api.map;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.entity.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class SpawnRules {

    public boolean zombie = true;
    public boolean creeper = true;
    public boolean skeleton = true;
    public boolean slime = true;
    public boolean witch = true;
    public boolean spider = true;
    public boolean cavespider = true;
    public boolean wolf = true;
    public boolean pigzombie = true;
    public boolean blaze = true;
    public boolean ghast = true;
    public boolean magmaCube = true;
    public boolean silverfish = true;
    public boolean witherSkeleton = true;

    public boolean bat = true;
    public boolean cow = true;
    public boolean sheep = true;
    public boolean pig = true;
    public boolean chicken = true;
    public boolean horse = true;
    public boolean ocelot = true;
    public boolean mooshroom = true;
    public boolean squid = true;
    public boolean villager = true;

    private Set<Class<? extends Entity>> allowed = Sets.newHashSet();

    public boolean maySpawn(Entity entity) {
        if(entity instanceof EntitySkeleton){
            //Stupid mojang...
            if(((EntitySkeleton) entity).getSkeletonType() == 1){
                return this.witherSkeleton;
            }else{
                return this.skeleton;
            }
        }
        return this.allowed.contains(entity.getClass());
    }

    public void refresh() {
        this.allowed.clear();
        //CHECKSTYLE.OFF: NeedBraces
        if(this.zombie){
            this.allowed.add(EntityZombie.class);
        }
        if(this.creeper){
            this.allowed.add(EntityCreeper.class);
        }
        if(this.skeleton){
            this.allowed.add(EntitySkeleton.class);
        }
        if(this.slime){
            this.allowed.add(EntitySlime.class);
        }
        if(this.witch){
            this.allowed.add(EntityWitch.class);
        }
        if(this.spider){
            this.allowed.add(EntitySpider.class);
        }
        if(this.cavespider){
            this.allowed.add(EntityCaveSpider.class);
        }
        if(this.wolf){
            this.allowed.add(EntityWolf.class);
        }
        if(this.pigzombie){
            this.allowed.add(EntityPigZombie.class);
        }
        if(this.blaze){
            this.allowed.add(EntityBlaze.class);
        }
        if(this.ghast){
            this.allowed.add(EntityGhast.class);
        }
        if(this.magmaCube){
            this.allowed.add(EntityMagmaCube.class);
        }
        if(this.silverfish){
            this.allowed.add(EntitySilverfish.class);
        }
        if(this.bat){
            this.allowed.add(EntityBat.class);
        }
        if(this.cow){
            this.allowed.add(EntityCow.class);
        }
        if(this.sheep){
            this.allowed.add(EntitySheep.class);
        }
        if(this.pig){
            this.allowed.add(EntityPig.class);
        }
        if(this.chicken){
            this.allowed.add(EntityChicken.class);
        }
        if(this.horse){
            this.allowed.add(EntityHorse.class);
        }
        if(this.ocelot){
            this.allowed.add(EntityOcelot.class);
        }
        if(this.mooshroom){
            this.allowed.add(EntityMooshroom.class);
        }
        if(this.squid){
            this.allowed.add(EntitySquid.class);
        }
        if(this.villager){
            this.allowed.add(EntityVillager.class);
        }
        //CHECKSTYLE.ON: NeedBraces
    }
}
