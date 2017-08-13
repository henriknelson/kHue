package nu.cliffords.khue.interfaces

import nu.cliffords.khue.classes.Group
import nu.cliffords.khue.classes.Light

/**
 * Created by Henrik Nelson on 2017-08-11.
 */
interface BridgeReceiver: BaseReceiver {
    fun onLightsFound(lights: List<Light>)
    fun onGroupsFound(groups: List<Group>)
    fun onGroupFound(group: Group)
}