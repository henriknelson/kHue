package nu.cliffords.khue.classes

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

/**
 * Created by Henrik Nelson on 2017-08-10.
 */

class Light(val state: LightState,val type: String, val name: String, val modelid: String, val manufacturername: String, val uniqueid: String, val swversion: String, val swconfigid: String, val productid: String) {

    data class LightState(var on: Boolean,var bri:Int,var hue:Int,var sat:Int,var effect:String,var xy:Array<Float>,var ct:Int,var alert:String,var colormode:String,var reachable:String) {
    }

    var lightId: String? = ""

    //Light Deserializer
    class ListDeserializer : ResponseDeserializable<List<Light>> {
        override fun deserialize(content: String) : List<Light>{
            val lightListType = object : TypeToken<Map<String, Light>>(){}.type
            val lights = Gson().fromJson<Map<String,Light>>(content, lightListType)
            lights.forEach { lightId, light ->
                light.lightId = lightId
            }
            return lights.values.toList()
        }
    }

    class Deserializer : ResponseDeserializable<Light> {
        override fun deserialize(content: String) : Light{
            val light = Gson().fromJson<Light>(content, Light::class.java)
            return light
        }
    }

    fun setState(on: Boolean? = null, brightness: Int? = null, saturation: Int? = null, ct:Int? = null, xy:List<Float>? = null, transitiontime:Int = 4,
                 listener: (JSONObject) -> Unit)
    {

        val paramsJson = Gson().toJson(
                mapOf("on" to on,"bri" to brightness,"sat" to saturation, "ct" to ct, "xy" to xy, "transitiontime" to transitiontime ).
                    filterValues { param -> param != null })

        Fuel.put("/lights/$lightId/state").body(paramsJson).responseJson { request, response, result ->
            val (jsonResponse, error) = result
            if (jsonResponse != null)
                listener(jsonResponse.obj())
            else
                Log.d("Bridge","Error")
            }
    }

    fun setName(name: String,
                listener: (JSONObject) -> Unit) {

        val paramsJson = Gson().toJson(name)

        Fuel.put("/lights/$lightId").body(paramsJson).responseJson { request, response, result ->
            val (jsonResponse, error) = result
            if (jsonResponse != null)
                listener(jsonResponse.obj())
            else
                Log.d("Bridge","Error")
        }
    }

}