package nu.cliffords.khue

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Henrik Nelson on 2017-08-10.
 */

class Light(val state: LightState, val type: String, val name: String, val modelid: String, val manufacturername: String, val uniqueid: String, val swversion: String, val swconfigid: String, val productid: String) {

    data class LightState(var on: Boolean,var bri:Int,var hue:Int,var sat:Int,var effect:String,var xy:Array<Float>,var ct:Int,var alert:String,var colormode:String,var reachable:String) {
    }

    var lightId: String = ""

    //Light Deserializer
    class ListDeserializer : ResponseDeserializable<List<Light>> {
        override fun deserialize(content: String) : List<Light>{
            val lightListType = object : TypeToken<Map<String, Light>>(){}.type
            val lights = Gson().fromJson<Map<String, Light>>(content, lightListType)
            lights.forEach { lightId, light ->
                light.lightId = lightId
            }
            return lights.values.toList()
        }
    }

    class Deserializer : ResponseDeserializable<Light> {
        override fun deserialize(content: String) : Light {
            val light = Gson().fromJson<Light>(content, Light::class.java)
            return light
        }
    }

}