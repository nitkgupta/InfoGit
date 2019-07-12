package com.nitkarsh.infogit.utils

import android.content.Context
import android.graphics.Typeface

class Fonts {

    companion object {
        private var mavenMedium : Typeface? = null
        private var mavenRegular : Typeface? = null
        private var avenirNext : Typeface? = null

        fun mavenMedium(context: Context): Typeface {
            if(mavenMedium == null) {
                mavenMedium = Typeface.createFromAsset(context.assets,"fonts/maven_pro_medium.ttf")
            }
            return mavenMedium!!
        }

        fun mavenRegular(context: Context): Typeface {
            if(mavenRegular == null) {
                mavenRegular = Typeface.createFromAsset(context.assets,"fonts/maven_pro_regular.otf")
            }
            return mavenRegular!!
        }

        fun avenirNext(context: Context): Typeface {
            if(avenirNext == null) {
                avenirNext = Typeface.createFromAsset(context.assets,"fonts/avenir_next_demi.otf")
            }
            return avenirNext!!
        }
    }
}