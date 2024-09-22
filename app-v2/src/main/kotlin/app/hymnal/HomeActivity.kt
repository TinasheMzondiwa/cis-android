package app.hymnal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuitx.android.rememberAndroidScreenAwareNavigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecoration
import dagger.hilt.android.AndroidEntryPoint
import libraries.hymnal.ui.HymnalTheme
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var circuit: Circuit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           HymnalTheme {
               val backstack = rememberSaveableBackStack(HomeScreen)
               val circuitNavigator = rememberCircuitNavigator(backstack)
               val navigator = rememberAndroidScreenAwareNavigator(circuitNavigator, this)

               CircuitCompositionLocals(circuit = circuit) {
                   ContentWithOverlays {
                       NavigableCircuitContent(
                           navigator,
                           backstack,
                           Modifier,
                           circuit,
                           decoration = GestureNavigationDecoration {
                               navigator.pop()
                           }
                       )
                   }
               }
            }
        }
    }
}