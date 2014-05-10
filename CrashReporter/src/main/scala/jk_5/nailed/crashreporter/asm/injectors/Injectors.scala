package jk_5.nailed.crashreporter.asm.injectors

import net.minecraft.launchwrapper.Launch
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import com.google.common.collect.Multimap
import org.objectweb.asm.{MethodVisitor, Type}
import jk_5.nailed.crashreporter.asm.{MethodCodeInjector, MethodMatcher}
import java.io.File

/**
 * No description given
 *
 * @author jk-5
 */
object Injectors {

  lazy val isSrg: Boolean = Option(Launch.blackboard.get("fml.deobfuscatedEnvironment").asInstanceOf[java.lang.Boolean]: Boolean).getOrElse(true)

  final val CRASH_REPORT_CLS = "net.minecraft.crash.CrashReport"
  final val TILE_ENTITY_CLS = "net.minecraft.tileentity.TileEntity"
  final val ENTITY_LIST_CLS = "net.minecraft.entity.EntityList"
  final val ENTITY_CLS = "net.minecraft.entity.Entity"
  final val NBT_TAG_COMPOUND_CLS = "net.minecraft.nbt.NBTTagCompound"
  final val WORLD_CLS = "net.minecraft.world.World"
  final val CHUNK_CLS = "net.minecraft.world.chunk.Chunk"
  final val ANVIL_CHUNK_LOADER = "net.minecraft.world.chunk.storage.AnvilChunkLoader"

  def getClassName(name: String): String = {
    val n = name.replace('.', '/')
    if(isSrg) FMLDeobfuscatingRemapper.INSTANCE.unmap(n) else n
  }

  def setupInjectors(injectors: Multimap[String, MethodCodeInjector]){
    val nbtTagCompoundName = this.getClassName(NBT_TAG_COMPOUND_CLS)
    val nbtTagCompoundType = Type.getObjectType(nbtTagCompoundName)
    val worldName = this.getClassName(WORLD_CLS)
    val worldType = Type.getObjectType(worldName)
    val entityName = this.getClassName(ENTITY_CLS)
    val entityType = Type.getObjectType(entityName)
    val tileEntityName = this.getClassName(TILE_ENTITY_CLS)
    val tileEntityType = Type.getObjectType(tileEntityName)
    val chunkName = this.getClassName(CHUNK_CLS)
    val chunkType = Type.getObjectType(chunkName)

    {
      val crashHandlerName = this.getClassName(CRASH_REPORT_CLS)
      val fileType = Type.getType(classOf[File])
      val methodType = Type.getMethodType(Type.BOOLEAN_TYPE, fileType)
      val matcher = new MethodMatcher(crashHandlerName, methodType.getDescriptor, "saveToFile", "func_147149_a")
      injectors.put(CRASH_REPORT_CLS, new MethodCodeInjector("crash_handler", matcher){
        override def createVisitor(parent: MethodVisitor): MethodVisitor = new CrashHandlerInjector(parent)
      })
    }
    {
      val methodType = Type.getMethodType(tileEntityType, nbtTagCompoundType)
      val matcher = new MethodMatcher(tileEntityName, methodType.getDescriptor, "createAndLoadEntity", "func_145827_c")
      injectors.put(TILE_ENTITY_CLS, new MethodCodeInjector("tile_entity_load", matcher){
        override def createVisitor(parent: MethodVisitor): MethodVisitor = new ExceptionHandlerInjector(parent, "tile_entity_construct", "tile_entity_read")
      })
    }
    {
      val entityListName = this.getClassName(ENTITY_LIST_CLS)
      val methodType = Type.getMethodType(entityType, nbtTagCompoundType, worldType)
      val matcher = new MethodMatcher(entityListName, methodType.getDescriptor, "createEntityFromNBT", "func_75615_a")
      injectors.put(ENTITY_LIST_CLS, new MethodCodeInjector("entity_load", matcher){
        override def createVisitor(parent: MethodVisitor): MethodVisitor = new ExceptionHandlerInjector(parent, "entity_construct", "entity_read")
      })
    }
    {
      val chunkLoaderName = this.getClassName(ANVIL_CHUNK_LOADER)
      val methodType = Type.getMethodType(Type.VOID_TYPE, chunkType, worldType, nbtTagCompoundType)
      val matcher = new MethodMatcher(chunkLoaderName, methodType.getDescriptor, "writeChunkToNBT", "func_75820_a")
      injectors.put(ANVIL_CHUNK_LOADER, new MethodCodeInjector("chunk_write", matcher){
        override def createVisitor(parent: MethodVisitor): MethodVisitor = new ExceptionHandlerInjector(parent, "entity_write", "tile_entity_write")
      })
    }
  }
}
