
package com.example.stepcounter.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.stepcounter.R
import com.example.stepcounter.presentation.theme.StepCounterTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StepCounterTheme {
                WearFitnessApp()
            }
        }
    }
}

@Composable
fun WearFitnessApp() {
    val navController = rememberNavController()

    var steps by remember { mutableIntStateOf(0) }
    var stepsGoal by remember { mutableIntStateOf(10000) }
    var calories by remember { mutableIntStateOf(0) }
    var caloriesGoal by remember { mutableIntStateOf(500) }

    SwipeNavigationController(navController = navController) {
        NavHost(
            navController = navController,
            startDestination = "progress"
        ) {
            composable("progress") {
                DailyProgressScreen(
                    steps = steps,
                    stepsGoal = stepsGoal,
                    calories = calories,
                    caloriesGoal = caloriesGoal,
                    onAddStep = {
                        steps++
                        calories++
                    }
                )
            }

            composable("heart") {
                HeartRateScreen()
            }

            composable("goals") {
                ModifyGoalScreen(
                    stepsGoal = stepsGoal,
                    caloriesGoal = caloriesGoal,
                    onDecreaseStepsGoal = { stepsGoal -= 500 },
                    onIncreaseStepsGoal = { stepsGoal += 500 },
                    onDecreaseCaloriesGoal = { caloriesGoal -= 50 },
                    onIncreaseCaloriesGoal = { caloriesGoal += 50 }
                )
            }
        }

    }
}

    @Composable
    fun SwipeNavigationController(
        navController: NavHostController,
        content: @Composable () -> Unit
    ){
        val routes = listOf("progress", "heart", "goals")
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route ?: "progress"
        val currentIndex = routes.indexOf(currentRoute)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(currentRoute) {
                    var totalDrag = 0f
                    detectHorizontalDragGestures(
                        onDragStart = {
                            totalDrag = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            totalDrag  += dragAmount
                        },
                        onDragEnd = {
                            if (totalDrag < -60 && currentIndex < routes.lastIndex) {
                           navController.navigate(routes[currentIndex + 1]){
                               launchSingleTop = true
                           }
                            }

                            if (totalDrag > 60 && currentIndex > 0) {
                                navController.navigate(routes[currentIndex - 1]){
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ){
            content()
        }
    }

    @Composable
    fun DailyProgressScreen(
        steps: Int,
        stepsGoal: Int,
        calories: Int,
        caloriesGoal: Int,
        onAddStep: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Daily Progress",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Steps", color = Color.White)
            Text(
                text = "$steps / $stepsGoal",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Calories", color = Color.White)
            Text(
                text = "$calories / $caloriesGoal",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onAddStep) {
                Text("Add Step")
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Swipe ->",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    @Composable
    fun ModifyGoalScreen(
        stepsGoal: Int,
        caloriesGoal: Int,
        onDecreaseStepsGoal: () -> Unit,
        onIncreaseStepsGoal: () -> Unit,
        onDecreaseCaloriesGoal: () -> Unit,
        onIncreaseCaloriesGoal: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Modify Goals",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Steps", color = Color.White)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onDecreaseStepsGoal) {
                    Text("-")
                }
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stepsGoal.toString(),
                    color = Color.White,
                )

                Button(onClick = onIncreaseStepsGoal) {
                    Text("+")
                }
            }

            Text(text = "Calories", color = Color.White)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onDecreaseCaloriesGoal) {
                    Text("-")
                }
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = caloriesGoal.toString(),
                    color = Color.White,
                )

                Button(onClick = onIncreaseCaloriesGoal) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Swipe ->",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    @Composable
    fun HeartRateScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Heart Rate",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "72 BP<",
                color = Color.White,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Swipe ->",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }


