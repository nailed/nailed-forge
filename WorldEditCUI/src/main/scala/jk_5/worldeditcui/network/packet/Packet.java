package jk_5.worldeditcui.network.packet;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class Packet {

    public Packet(){} //Keep the default constructor for reflection stuff

    private int minArguments;
    private int maxArguments;
    private String[] args;

    protected final void setNumberOfArguments(int min, int max){
        this.minArguments = min;
        this.maxArguments = max;
    }

    public final void processPacket(String[] args){
        this.args = args;
        if((this.maxArguments == this.minArguments && this.args.length != maxArguments) || (this.maxArguments != this.minArguments && (this.args.length > this.maxArguments || this.args.length < this.minArguments))){
            throw new PacketException("Received invalid number of arguments");
        }else{
            this.process();
        }
        this.process();
    }

    protected final int getIntArgument(int index){
        return Integer.parseInt(this.args[index]);
    }

    protected final double getDoubleArgument(int index){
        return Double.parseDouble(this.args[index]);
    }

    protected final String getStringArgument(int index){
        return this.args[index];
    }

    protected abstract void setupArguments();
    protected abstract void process() throws PacketException;
}
