package com.plr.loudworkouttimer

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class Tts {
    var tts: TextToSpeech? = null

    fun textToSpeech(context: Context, text: String){
        tts = TextToSpeech(
            context
        ) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.US
                    txtToSpeech.setSpeechRate(1.0f)
                    txtToSpeech.speak(
                        text,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        null
                    )
                }
            }
        }
    }
}