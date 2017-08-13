package nu.cliffords.khue

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Created by Henrik Nelson on 2017-08-12.
 */


class Config(val name: String, val zigbeechannel: Int, val bridgeid: String, val mac: String,
             val dhcp: Boolean, val ipaddress: String, val netmask: String, val gateway: String,
             val proxyaddress: String, val proxyport: String, val UTC: String, val localtime: String,
             val timezone: String, val modelid: String, val datastoreversion: String, swversion: String,
             val apiversion: String, val swupdate: SWUpdate, val linkbutton: Boolean,
             val portalservices: Boolean, val portalconnection: Boolean, val portalstate: PortalState,
             val internetservices: InternetServices, val factorynew: Boolean, val replacesbridgeid: Int?,
             val backup: Backup, val starterkitid: String, val whitelist: Map<String, WhiteList>) {

    //Config Deserializer
    class Deserializer : ResponseDeserializable<Config> {
        override fun deserialize(content: String) : Config {
            val config = Gson().fromJson<Config>(content, Config::class.java)
            return config
        }
    }

    data class DeviceTypes(val bridge: Boolean, val lights: Array<Int>, val sensors: Array<Int>)

    data class SWUpdate(val updatestate: Int, val checkforupdates: Boolean, val deviceTypes: DeviceTypes,
                        val url:String, val text: String, val notify: Boolean){
        companion object {

        }
    }

    data class PortalState(val signedon: Boolean, val incoming: Boolean, val outgoing: Boolean,val communication: String)

    data class InternetServices(val remoteaccess: String)

    data class Backup(val status: String, val errorcode: Int)

    data class WhiteList(
            @SerializedName("last use date") val last_use_date: String,
            @SerializedName("create date") val create_date: String,
            val name: String)
}