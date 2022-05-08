package com.axmor.fsinphone.videomessages.ui.common.animations

import com.axmor.fsinphone.videomessages.R

object LeftToRightTransition: FragmentTransactionAnimation {
    override val enter: Int = R.anim.enter_from_right
    override val exit: Int = R.anim.exit_to_left
    override val popEnter: Int = R.anim.enter_from_left
    override val popExit: Int = R.anim.exit_to_right
}