package jk_5.nailed.updater

import java.security.MessageDigest
import java.security.cert.Certificate

/**
 * No description given
 *
 * @author jk-5
 */
object NailedSanityChecker {

  private val fingerprint = "87401ecb3314a1a18fb267281b2432975a7e2e84"

  def check(){
    val source = NailedSanityChecker.getClass.getProtectionDomain.getCodeSource
    if(source.getLocation.getProtocol == "jar"){
      val certs = source.getCertificates
      if(certs == null){
        UpdatingTweaker.logger.fatal("No fingerprint was found for Nailed")
        throw new RuntimeException("Nailed is not signed! Please get a new one, or contact jk-5")
      }
      certs.foreach(c => {
        val fingerprint = CertificateHelper.getFingerprint(c)
        if(fingerprint == this.fingerprint){
          UpdatingTweaker.logger.info("Found valid fingerprint for Nailed. Certificate fingerprint " + fingerprint)
        }else{
          UpdatingTweaker.logger.fatal("Found invalid fingerprint for Nailed. Certificate fingerprint " + fingerprint)
          throw new RuntimeException("Nailed is not signed! Please get a new one, or contact jk-5")
        }
      })
    }
  }
}

/**
 * No description given
 *
 * @author jk-5
 */
object CertificateHelper {
  private val hexes = "0123456789abcdef"

  def getFingerprint(certificate: Certificate): String = {
    if(certificate == null) {
      return "NO VALID CERTIFICATE FOUND"
    }
    try{
      val md = MessageDigest.getInstance("SHA-1")
      md.update(certificate.getEncoded)
      hexify(md.digest)
    }catch {
      case e: Exception => return null
    }
  }

  private def hexify(chksum: Array[Byte]): String ={
    val hex = new StringBuilder(2 * chksum.length)
    for(b <- chksum){
      hex.append(hexes.charAt((b & 0xF0) >> 4)).append(hexes.charAt(b & 0x0F))
    }
    hex.toString()
  }
}

