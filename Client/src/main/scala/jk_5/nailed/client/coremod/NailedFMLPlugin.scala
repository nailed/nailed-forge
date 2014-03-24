package jk_5.nailed.client.coremod

import cpw.mods.fml.relauncher.IFMLLoadingPlugin.{TransformerExclusions, MCVersion}
import jk_5.nailed.client.Constants
import cpw.mods.fml.relauncher.{IFMLCallHook, IFMLLoadingPlugin}
import java.util
import cpw.mods.fml.common.asm.transformers.AccessTransformer
import jk_5.nailed.NailedLog
import cpw.mods.fml.common.CertificateHelper

/**
 * No description given
 *
 * @author jk-5
 */
@MCVersion(Constants.MCVERSION)
@TransformerExclusions(Array("jk_5.nailed.client.coremod."))
class NailedFMLPlugin extends IFMLLoadingPlugin {
  override def getAccessTransformerClass = "jk_5.nailed.client.coremod.NailedAccessTransformer"
  override def injectData(data: util.Map[String, AnyRef]) = {}
  override def getSetupClass = "jk_5.nailed.client.coremod.NailedSanityChecker"
  override def getModContainerClass = null
  override def getASMTransformerClass = new Array[String](0)
}
class NailedAccessTransformer extends AccessTransformer("nailed_at.cfg")
class NailedSanityChecker extends IFMLCallHook {
  val fingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84"
  override def injectData(data: util.Map[String, AnyRef]){}
  override def call(): Void = {
    val source = this.getClass.getProtectionDomain.getCodeSource
    if(source.getLocation.getProtocol == "jar") {
      val certs = source.getCertificates
      if(certs == null) {
        NailedLog.fatal("No fingerprint was found for Nailed")
        throw new RuntimeException("Nailed is not signed! Please get a new one, or contact jk-5")
      }
      certs.foreach(cert => {
        val fingerprint = CertificateHelper.getFingerprint(cert)
        if(fingerprint == this.fingerprint){
          NailedLog.info("Found valid fingerprint for Nailed. Certificate fingerprint " + fingerprint)
        }else{
          NailedLog.fatal("Found invalid fingerprint for Nailed. Certificate fingerprint " + fingerprint)
          throw new RuntimeException("Nailed is not signed! Please get a new copy, or contact jk-5")
        }
      })
    }
    null
  }
}
