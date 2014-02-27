package jk_5.quakecraft;

/**
 * No description given
 *
 * @author jk-5
 */
/*public class InstructionStartQuakecraft implements IInstruction {

    @Override
    public void injectArguments(String args){

    }

    @Override
    public void execute(GameController controller){
        for(Player player : controller.getMap().getPlayers()){
            ItemStack stack = new ItemStack(Items.wooden_hoe, 1);
            stack.setStackDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "Railgun");
            player.getEntity().inventory.setInventorySlotContents(0, stack);
            player.getEntity().addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1000000, 1, true));
        }
        String objectiveName = controller.getMap().getID() + "-kills";
        Scoreboard scoreboard = controller.getMap().getWorld().getScoreboard();
        if(scoreboard.getObjective(objectiveName) != null){
            ScoreObjective objective = scoreboard.getObjective(objectiveName);
            scoreboard.func_96519_k(objective);
        }
        ScoreObjective objective = controller.getMap().getWorld().getScoreboard().addScoreObjective(objectiveName, IScoreObjectiveCriteria.field_96641_b);
        objective.setDisplayName(ChatColor.BOLD + "" + ChatColor.RED + "Leaderboard");
        scoreboard.func_96530_a(1, objective);
        for(Player player : controller.getMap().getPlayers()){
            Score score = scoreboard.func_96529_a(player.getUsername(), objective);
            score.setScorePoints(0);
        }
    }

    @Override
    public IInstruction cloneInstruction(){
        return new InstructionStartQuakecraft();
    }
}*/