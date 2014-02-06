package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.MappackMetadata;
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
        this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(args));
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
        server.setAllowedSpawnTypes(meta.isSpawnHostileMobs() && this.difficulty.getDifficultyId() > 0, meta.isSpawnFriendlyMobs());
    }
}
