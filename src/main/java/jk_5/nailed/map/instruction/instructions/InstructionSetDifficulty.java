package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.map.mappack.MappackMetadata;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class InstructionSetDifficulty implements IInstruction {

    private EnumDifficulty difficulty;

    @Override
    public void injectArguments(String args) {
        this.difficulty = EnumDifficulty.func_151523_a(Integer.parseInt(args));
    }

    @Override
    public IInstruction cloneInstruction() {
        return new InstructionSetDifficulty(this.difficulty);
    }

    @Override
    public void execute(GameController controller) {
        World server = controller.getMap().getWorld();
        MappackMetadata meta = controller.getMap().getMappack().getMappackMetadata();
        server.difficultySetting = this.difficulty;
        server.setAllowedSpawnTypes(meta.isSpawnHostileMobs() && this.difficulty.func_151525_a() > 0, meta.isSpawnFriendlyMobs());
    }
}
