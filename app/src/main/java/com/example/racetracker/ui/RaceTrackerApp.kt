
package com.example.racetracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.racetracker.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun RaceTrackerApp() {
      val playerOne = remember {
        RaceParticipant(name = "Player 1", progressIncrement = 1)
    }
    val playerTwo = remember {
        RaceParticipant(name = "Player 2", progressIncrement = 2)
    }

    /*
    Navega al elemento componible RaceTrackerApp() y agrega una llamada al elemento componible LaunchedEffect()
    en la línea siguiente a la definición de raceInProgress.
     */
    var raceInProgress by remember { mutableStateOf(false) }

    if (raceInProgress) {

        /*
        Para asegurarte de que, si las instancias de playerOne o playerTwo se reemplazan por instancias diferentes,
        LaunchedEffect() debe cancelar y reiniciar las corrutinas subyacentes, agrega los objetos playerOne y playerTwo
        como key del LaunchedEffect. Así como se recompone un elemento componible Text() cuando cambia su valor de texto,
         si cambia alguno de los argumentos clave de LaunchedEffect(), la corrutina subyacente se cancela y se reinicia.
         */
        LaunchedEffect(playerOne, playerTwo) {

            /*
            Para garantizar que la función run() de playerOne y playerTwo complete su ejecución antes de actualizar
            la marca raceInProgress, une ambos compiladores con un bloque de coroutineScope.
             */
            coroutineScope {
                //Agrega una llamada a las funciones playerOne.run() y playerTwo.run().
                launch { playerOne.run() }
                launch { playerTwo.run() }
            }
            raceInProgress = false
        }
    }
    RaceTrackerScreen(
        playerOne = playerOne,
        playerTwo = playerTwo,
        isRunning = raceInProgress,
        onRunStateChange = { raceInProgress = it }
    )
}

@Composable
private fun RaceTrackerScreen(
    playerOne: RaceParticipant,
    playerTwo: RaceParticipant,
    isRunning: Boolean,
    onRunStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.run_a_race),
            style = MaterialTheme.typography.headlineLarge,
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_walk),
                contentDescription = null,

                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_medium)),
            )
            StatusIndicator(
                participantName = playerOne.name,
                currentProgress = playerOne.currentProgress,
                maxProgress = stringResource(
                    R.string.progress_percentage,
                    playerOne.maxProgress
                ),
                progressFactor = playerOne.progressFactor
            )
            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_large)))
            StatusIndicator(
                participantName = playerTwo.name,
                currentProgress = playerTwo.currentProgress,
                maxProgress = stringResource(
                    R.string.progress_percentage,
                    playerTwo.maxProgress
                ),
                progressFactor = playerTwo.progressFactor
            )
            Spacer(modifier = Modifier.size(dimensionResource(R.dimen.padding_large)))
            RaceControls(
                isRunning = isRunning,
                onRunStateChange = onRunStateChange,
                onReset = {
                    playerOne.reset()
                    playerTwo.reset()
                    onRunStateChange(false)
                }
            )
        }
    }
}

@Composable
private fun StatusIndicator(
    participantName: String,
    currentProgress: Int,
    maxProgress: String,
    progressFactor: Float,
    modifier: Modifier = Modifier
) {
    Row {
        Text(participantName, Modifier.padding(end = dimensionResource(R.dimen.padding_small)))

        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
        ) {
            LinearProgressIndicator(
                progress = progressFactor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.progress_indicator_height))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.progress_indicator_corner_radius)))
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.progress_percentage, currentProgress),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = maxProgress,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RaceControls(
    onRunStateChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    isRunning: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onRunStateChange(!isRunning) }) {
            Text(if (isRunning) stringResource(R.string.pause) else stringResource(R.string.start))
        }

        Button(onClick = onReset) {
            Text(stringResource(R.string.reset))
        }
    }
}

@Preview
@Composable
fun RaceTrackerAppPreview() {
    MaterialTheme {
        RaceTrackerApp()
    }
}
