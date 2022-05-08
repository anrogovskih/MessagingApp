package com.axmor.fsinphone.videomessages.ui.common.animations

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes

interface FragmentTransactionAnimation {
    @get:AnimatorRes @get:AnimRes val enter: Int
    @get:AnimatorRes @get:AnimRes val exit: Int
    @get:AnimatorRes @get:AnimRes val popEnter: Int
    @get:AnimatorRes @get:AnimRes val popExit: Int
}