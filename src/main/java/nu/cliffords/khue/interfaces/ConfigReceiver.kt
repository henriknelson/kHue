package nu.cliffords.khue.interfaces

import nu.cliffords.khue.classes.Config
import com.github.kittinunf.fuel.android.core.Json

/**
 * Created by henrik on 2017-08-12.
 */
interface ConfigReceiver: BaseReceiver {
    fun onConfigReceived(config: Config)
}