package nu.cliffords.khue.classes

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

/**
 * Created by henrik on 2017-08-12.
 */
class Group(val name: String, val lights: Array<String>,val type:Type,val action: GroupAction) {

    var groupId: String? = ""

    enum class Class(val className: String) {
        @SerializedName("Living room")  LIVING_ROOM("Living room"),
        @SerializedName("Kitchen")      KITCHEN("Kitchen"),
        @SerializedName("Dining")       DINING("Dining"),
        @SerializedName("Bedroom")      BEDROOM("Bedroom"),
        @SerializedName("Kids bedroom") KIDS_BEDROOM("Kids bedroom"),
        @SerializedName("Bathroom")     BATHROOM("Bathroom"),
        @SerializedName("Nursery")      NURSERY("Nursery"),
        @SerializedName("Recreation")   RECREATION("Recreation"),
        @SerializedName("Office")       OFFICE("Office"),
        @SerializedName("Gym")          GYM("Gym"),
        @SerializedName("Hallway")      HALLWAY("Hallway"),
        @SerializedName("Toilet")       TOILET("Toilet"),
        @SerializedName("Front door")   FRONT_DOOR("Front door"),
        @SerializedName("Garage")       GARAGE("Garage"),
        @SerializedName("Terrace")      TERRACE("Terrace"),
        @SerializedName("Garden")       GARDEN("Garden"),
        @SerializedName("Driveway")     DRIVEWAY("Driveway"),
        @SerializedName("Carport")      CARPORT("Carport"),
        @SerializedName("Other")        OTHER("Other")
    }

    enum class Type(val typeName: String) {
        @SerializedName("Luminaire")    LUMINAIRE("Luminaire"),
        @SerializedName("Lightsource")  LIGHTSOURCE("Lightsource"),
        @SerializedName("LightGroup")   LIGHT_GROUP("LightGroup"),
        @SerializedName("Room")         ROOM("Room")
    }

    data class GroupAction(var on: Boolean,var bri:Int,var hue:Int,var sat:Int,var effect:String,var xy:Array<Float>,var ct:Int,var alert:String,var colormode:String){}

    //Group List Deserializer
    class ListDeserializer : ResponseDeserializable<List<Group>> {
        override fun deserialize(content: String) : List<Group>{
            val groupListType = object : TypeToken<Map<String, Group>>(){}.type
            val groups = Gson().fromJson<Map<String,Group>>(content, groupListType)
            groups.forEach { key, value ->
                value.groupId = key
            }
            return groups.values.toList()
        }
    }

    //Group Deserializer
    class Deserializer : ResponseDeserializable<Group> {
        override fun deserialize(content: String) : Group{
            val group = Gson().fromJson<Group>(content, Group::class.java)
            return group
        }
    }

    //API 2.5
    fun setState(on: Boolean? = null, brightness: Int? = null, saturation: Int? = null, ct:Int? = null, xy:List<Float>? = null, transitiontime:Int = 4 )
    {
        val paramsJson = Gson().toJson(
                mapOf("on" to on,"bri" to brightness,"sat" to saturation, "ct" to ct, "xy" to xy, "transitiontime" to transitiontime ).
                        filterValues { value -> value != null })
        try{
            Fuel.put("/groups/$groupId/action").body(paramsJson).responseJson { request, response, result ->
                val (value, error) = result
                if (value != null) {
                    Log.d("Bridge", value.toString())
                } else {
                    Log.d("Bridge", "ERROR")
                }
            }
        }catch(e:Exception){
            Log.d("Bridge","Error: $e")
        }
    }

}