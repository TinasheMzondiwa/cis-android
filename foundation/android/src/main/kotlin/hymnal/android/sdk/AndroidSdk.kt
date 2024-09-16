package hymnal.android.sdk

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(parameter = 0)
fun isAtLeastApi(sdk: Int): Boolean = Build.VERSION.SDK_INT >= sdk
