package com.mumfrey.worldeditwrapper.reflect;

public enum ObfuscationMapping {
    chunksToUnload("chunksToUnload", "c", "field_73248_b"),
    loadedChunkHashMap("loadedChunkHashMap", "g", "field_73244_f"),
    loadedChunks("loadedChunks", "h", "field_73245_g"),
    currentChunkProvider("currentChunkProvider", "e", "field_73246_d");

    public final String mcpName;
    public final String obfuscatedName;
    public final String seargeName;

    private ObfuscationMapping(String mcpName, String obfuscatedName, String seargeName) {
        this.mcpName = mcpName != null ? mcpName : seargeName;
        this.obfuscatedName = obfuscatedName;
        this.seargeName = seargeName;
    }

    public String getName() {
        return this.mcpName;
        //return ModUtilities.getObfuscatedFieldName(this.mcpName, this.obfuscatedName, this.seargeName);
    }
}
