package nu.cliffords.khue.classes

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelManager
import com.google.gson.Gson
import nu.cliffords.khue.interfaces.BridgeReceiver
import nu.cliffords.khue.interfaces.ConfigReceiver
import org.json.JSONArray

/**
 * Created by Henrik Nelson on 2017-08-10.
 */

class Bridge(val ipAddress: String,val userName: String) {

    init {
        FuelManager.instance.basePath = "http://$ipAddress/api/$userName"
        FuelManager.instance.baseHeaders = mapOf("Content-Type" to "application/json")
        FuelManager.instance.addRequestInterceptor { request ->
            Log.d("Bridge",request.toString())
            request
        }
        FuelManager.instance.addResponseInterceptor { response ->
            Log.d("Bridge",response.toString())
            response
        }
    }

    // Lights

    //API 1.1 - Get all lights
    fun getLights(receiver: BridgeReceiver) {
        Fuel.get("/lights").responseObject(Light.ListDeserializer()) { request, response, result ->
        val (lights, error) = result
        if (lights != null)
            receiver.onLightsFound(lights)
        else
            receiver.onError(error.toString())

        }
    }

    //API 1.2 - Get new lights
    fun getNewLights(listener: (JSONArray) -> Unit) {
        Fuel.get("/lights/new").responseJson { request, response, result ->
            val (json,error) = result
            if (json != null)
                listener(json.array())
        }
    }

    //API 1.3 - Search for new lights
    fun searchForNewLights(deviceid: Array<String>,
                           listener: (JSONArray) -> Unit) {

        val jsonParams = "{\"deviceid\":${Gson().toJson(deviceid)}}"
        Fuel.post("/lights").body(jsonParams).responseJson { request, response, result ->
            val (json,error) = result
            if(json != null)
                listener(json.array())
        }
    }

    //API 1.4 - Get light attribute and state
    fun getLight(lightId: String?,
                 listener: (Light) -> Unit ) {

        Fuel.get("/lights/$lightId").responseObject(Light.Deserializer()) { request, response, result ->

            val (light, error) = result
            if (light != null){
                light.lightId = lightId
                listener(light)
            } else {
                //Todo
            }

        }
    }

    //API 1.7 - Delete lights
    fun deleteLight(lightId: String,
                    listener: (JSONArray) -> Unit) {

        Fuel.delete("/lights/$lightId").responseJson { request, response, result ->
            val (json, error) = result
            if (json != null)
                listener(json.array())
        }
    }

    //Groups

    //API 2.1 - Get all groups
    fun getGroups(receiver: BridgeReceiver) {
        Fuel.get("/groups").responseObject(Group.ListDeserializer()) { request, response, result ->
            val (groups, error) = result
            if (groups != null) {
                receiver.onGroupsFound(groups)
            }else {
                receiver.onError(error.toString())
            }
        }
    }

    //API 2.2 - Create group
    fun createGroup(name: String, type: Group.Type,groupClass: Group.Class,lights: Array<String>,
                    listener: (JSONArray) -> Unit) {

        val jsonParams = Gson().toJson(mapOf("name" to name,"type" to type,"lights" to lights))

        Fuel.post("/groups").body(jsonParams).responseJson { request, response, result ->
            val (json,error) = result
            if (json != null)
                listener(json.array())
        }
    }

    //API 2.3 - Get group attributes
    fun getGroup(groupId: String?,
                 listener: (Group) -> Unit) {

        Fuel.get("/groups/$groupId").responseObject(Group.Deserializer()) { request, response, result ->
            val (group, error) = result
            if (group != null){
                group.groupId = groupId
                listener(group)
            }
        }

    }

    //API 2.4 - Set group attributes
    fun setGroupAttributes(groupId: String?, name: String, lights: Array<String>, groupClass: Group.Class,
                           listener: (JSONArray) -> Unit) {

        val jsonParams = Gson().toJson(mapOf("name" to name, "lights" to lights, "class" to groupClass))

        Fuel.put("/groups/$groupId").body(jsonParams).responseJson { request, response, result ->
            val (json,error) = result
            if (json != null)
                listener(json.array())
        }
    }


    //API 2.6 - Delete group
    fun deleteGroup(groupId: String,
                    listener: (JSONArray) -> Unit) {

        Fuel.delete("/groups/$groupId").responseJson { request, response, result ->
            val (json, error) = result
            if (json != null)
                listener(json.array())
        }
    }



    //Config
    fun getConfig(receiver: ConfigReceiver) {
        Fuel.get("/config").responseObject(Config.Deserializer()) { req, resp, res ->
            val (config,error) = res
            if(config != null)
                receiver.onConfigReceived(config)
            else
                receiver.onError("Could not get configuration for this bridge")
        }
    }

    fun setConfig(proxyport: Int? = null,name:String? = null,swupdate: Config.SWUpdate? = null,
                  proxyaddress: String? = null, linkbutton: Boolean? = null, ipaddress:String? = null,
                  netmask: String? = null, gateway: String? = null, dhcp: Boolean? = null, UTC: String? = null,
                  timezone: String? = null, touchlink: Boolean? = null, zigbeechannel: String? = null)
    {
        val paramsJson = Gson().toJson(
                mapOf("proxyport" to proxyport,"name" to name,"swupdate" to Config.SWUpdate,
                        "proxyaddress" to proxyaddress, "linkbutton" to linkbutton, "ipaddress" to ipaddress,
                        "netmask" to netmask, "gateway" to gateway, "dhcp" to dhcp, "UTC" to UTC, "timezone" to timezone,
                        "touchlink" to touchlink,"zigbeechannel" to zigbeechannel).
                        filterValues { value -> value != null })

        try{
            Fuel.put("/config").body(paramsJson).responseJson { request, response, result ->
                val (value, error) = result
                if (value != null) {
                    Log.d("Bridge", "Config updated ok!")
                    Log.d("Bridge","Config results: " + value.toString())
                } else {
                    Log.d("Bridge", "ERROR - Could not update config")
                }
            }
        }catch(e:Exception){
            Log.d("Bridge","Error: $e")
        }

    }






}


