
package com.example.racetracker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

class RaceParticipant(
    val name: String,
    val maxProgress: Int = 100,
    val progressDelayMillis: Long = 500L,

    /*
    El valor de currentProgress se establece en initialProgress, que es 0. Para simular el progreso del participante,
    aumenta el valor de currentProgress en el valor de la propiedad progressIncrement dentro del bucle while. Ten en cuenta
     que el valor predeterminado de progressIncrement es 1.
     */
    private val progressIncrement: Int = 1,
    initialProgress: Int = 0
) {
    init {
        require(maxProgress > 0) { "maxProgress=$maxProgress; must be > 0" }
        require(progressIncrement > 0) { "progressIncrement=$progressIncrement; must be > 0" }
    }


    var currentProgress by mutableStateOf(initialProgress)
        private set

//Dentro de la clase RaceParticipant, define una nueva función suspend llamada run().
    suspend fun run() {

    /*
    *Para simular el progreso de la carrera, agrega un bucle while que se ejecute hasta que currentProgress
    *  alcance el valor de maxProgress, que se establece en 100.
     */
        while (currentProgress < maxProgress) {

            /*Si deseas simular diferentes intervalos de progreso en la carrera, usa la función de suspensión
            delay(). Pasa el valor de la propiedad progressDelayMillis como argumento.
             */

            //Si observas el código que acabas de agregar, verás un ícono a la izquierda de la llamada a la función delay()
            delay(progressDelayMillis) //Este ícono indica el punto de suspensión en el que la función podría suspenderse y reanudarse más tarde.
            currentProgress += progressIncrement
        }
    }

    fun reset() {
        currentProgress = 0
    }
}

val RaceParticipant.progressFactor: Float
    get() = currentProgress / maxProgress.toFloat()
