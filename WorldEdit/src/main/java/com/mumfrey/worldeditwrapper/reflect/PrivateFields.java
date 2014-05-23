package com.mumfrey.worldeditwrapper.reflect;

import java.util.*;

import net.minecraft.util.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;

public class PrivateFields<P, T> {

    public static final PrivateFields<ChunkProviderServer, Set> chunksToUnload = new PrivateFields<ChunkProviderServer, Set>(ChunkProviderServer.class, ObfuscationMapping.chunksToUnload);
    public static final PrivateFields<ChunkProviderServer, LongHashMap> loadedChunkHashMap = new PrivateFields<ChunkProviderServer, LongHashMap>(ChunkProviderServer.class, ObfuscationMapping.loadedChunkHashMap);
    public static final PrivateFields<ChunkProviderServer, List<Chunk>> loadedChunks = new PrivateFields<ChunkProviderServer, List<Chunk>>(ChunkProviderServer.class, ObfuscationMapping.loadedChunks);
    public static final PrivateFields<ChunkProviderServer, IChunkProvider> currentChunkProvider = new PrivateFields<ChunkProviderServer, IChunkProvider>(ChunkProviderServer.class, ObfuscationMapping.currentChunkProvider);

    public final Class<P> parentClass;

    private boolean errorReported;

    private final String fieldName;

    protected PrivateFields(Class<P> owner, ObfuscationMapping mapping) {
        this.parentClass = owner;
        this.fieldName = mapping.getName();
    }

    @SuppressWarnings("unchecked")
    public T get(P instance) {
        try{
            return (T) Reflection.getPrivateValue(this.parentClass, instance, this.fieldName);
        }catch(final Exception ex){
            if(!this.errorReported){
                this.errorReported = true;
                ex.printStackTrace();
            }
            return null;
        }
    }

    public T set(P instance, T value) {
        try{
            Reflection.setPrivateValue(this.parentClass, instance, this.fieldName, value);
        }catch(final Exception ex){
            if(!this.errorReported){
                this.errorReported = true;
                ex.printStackTrace();
            }
        }

        return value;
    }
}
