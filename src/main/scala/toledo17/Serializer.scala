package toledo17

import java.io._
import java.util.Base64
import java.nio.charset.StandardCharsets

// Taken from http://stackoverflow.com/a/34492194/118587
// and modified
object Serializer {

  def serialize[T <: Serializable](obj: T): String = {
    val byteOut = new ByteArrayOutputStream()
    val objOut = new ObjectOutputStream(byteOut)
    objOut.writeObject(obj)
    objOut.close()
    byteOut.close()
    Base64.getEncoder.encodeToString(byteOut.toByteArray)
  }

  def deserialize[T <: Serializable](string:String): T = {
    val bytes = Base64.getDecoder.decode(string)
    val byteIn = new ByteArrayInputStream(bytes)
    val objIn = new ObjectInputStream(byteIn)
    val obj = objIn.readObject().asInstanceOf[T]
    byteIn.close()
    objIn.close()
    obj
  }
}
