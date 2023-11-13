
package com.example.racetracker

import com.example.racetracker.ui.RaceParticipant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test


/*
Para definir la prueba, después de la definición de raceParticipant, crea una función
 raceParticipant_RaceStarted_ProgressUpdated() y anótala con la anotación @Test. Dado que el bloque
 de prueba debe colocarse en el compilador de runTest, usa la sintaxis de expresión para mostrar el bloque
 runTest() como resultado de la prueba.
 */

@OptIn(ExperimentalCoroutinesApi::class)
class RaceParticipantTest {
    private val raceParticipant = RaceParticipant(
        name = "Test",
        maxProgress = 100,
        progressDelayMillis = 500L,
        initialProgress = 0,
        progressIncrement = 1
    )

    /*
    Agrega una variable expectedProgress de solo lectura y establécela en 1.
     */

    @Test
    fun raceParticipant_RaceStarted_ProgressUpdated() = runTest {
        val expectedProgress = 1

        /*
        Usa el compilador de launch para iniciar una corrutina nueva y agregar una llamada a la función raceParticipant.run().
         */
        launch { raceParticipant.run() }

        /*
        Con el fin de simular el final de la carrera, usa la función advanceTimeBy() para adelantar el tiempo del despachador
        en raceParticipant.maxProgress * raceParticipant.progressDelayMillis:
         */
        advanceTimeBy(raceParticipant.progressDelayMillis)

        /*
        Agrega una llamada a la función runCurrent() a los efectos de ejecutar las tareas pendientes.
         */
        runCurrent()
        assertEquals(expectedProgress, raceParticipant.currentProgress)
    }

    /*Para simular el inicio de la carrera, usa el compilador de launch a fin de iniciar una
     corrutina nueva y llamar a la función raceParticipant.run().
     */

    @Test
    fun raceParticipant_RaceFinished_ProgressUpdated() = runTest {
        launch { raceParticipant.run() }

        advanceTimeBy(raceParticipant.maxProgress * raceParticipant.progressDelayMillis)
        runCurrent()

        /*
        Para garantizar que el progreso se actualice correctamente, agrega una llamada a la función assertEquals()
        a fin de verificar si el valor de la propiedad raceParticipant.currentProgress es igual a 100.
         */
        assertEquals(100, raceParticipant.currentProgress)
    }

    @Test
    fun raceParticipant_RacePaused_ProgressUpdated() = runTest {
        val expectedProgress = 5
        val racerJob = launch { raceParticipant.run() }
        /*
        Usa la función auxiliar advanceTimeBy() para aumentar el tiempo según el valor de raceParticipant.progressDelayMillis.
        La función advanceTimeBy() ayuda a reducir el tiempo de ejecución de la prueba.
         */
        advanceTimeBy(expectedProgress * raceParticipant.progressDelayMillis)
        /*Dado que advanceTimeBy() no ejecuta la tarea programada en la duración determinada, debes llamar a la función
        runCurrent(). Esta función ejecuta las tareas pendientes del momento actual.
         */
        runCurrent()
        racerJob.cancelAndJoin()

        /*Para garantizar que se actualice el progreso, agrega una llamada a la función assertEquals() a fin de verificar
        si el valor de la propiedad raceParticipant.currentProgress coincide con el de la variable expectedProgress.
         */
        assertEquals(expectedProgress, raceParticipant.currentProgress)
    }

    @Test
    fun raceParticipant_RacePausedAndResumed_ProgressUpdated() = runTest {
        val expectedProgress = 5

        repeat(2) {
            val racerJob = launch { raceParticipant.run() }
            advanceTimeBy(expectedProgress * raceParticipant.progressDelayMillis)
            runCurrent()
            racerJob.cancelAndJoin()
        }

        assertEquals(expectedProgress * 2, raceParticipant.currentProgress)
    }

    @Test(expected = IllegalArgumentException::class)
    fun raceParticipant_ProgressIncrementZero_ExceptionThrown() = runTest {
        RaceParticipant(name = "Progress Test", progressIncrement = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun raceParticipant_MaxProgressZero_ExceptionThrown() {
        RaceParticipant(name = "Progress Test", maxProgress = 0)
    }
}
