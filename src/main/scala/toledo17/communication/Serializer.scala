package toledo17.communication

import java.io._
import java.nio.charset.StandardCharsets
import java.util.Base64

// Taken from http://stackoverflow.com/a/34492194/118587
// and modified
private[communication] object Serializer {

  val CHARSET_USED = StandardCharsets.UTF_8

  def serialize[T <: Serializable](obj: T): String = {
    val byteOut = new ByteArrayOutputStream()
    val objOut = new ObjectOutputStream(byteOut)
    objOut.writeObject(obj)
    objOut.close()
    byteOut.close()
    new String(Base64.getEncoder.encode(byteOut.toByteArray()), CHARSET_USED)
  }

  def deserialize[T <: Serializable](string:String): T = {
    val bytes = Base64.getDecoder.decode(string.getBytes(CHARSET_USED))
    val byteIn = new ByteArrayInputStream(bytes)
    val objIn = new ObjectInputStream(byteIn)
    val obj = objIn.readObject().asInstanceOf[T]
    byteIn.close()
    objIn.close()
    obj
  }
}
