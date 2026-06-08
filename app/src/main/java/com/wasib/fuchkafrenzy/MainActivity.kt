package com.wasib.fuchkafrenzy


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    // 1. Connect our logic to the UI
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 2. Load the main game router
                    GameScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    // 3. Conditional Rendering (just like React)
    if (viewModel.isGameOver) {
        GameOverScreen(
            score = viewModel.score,
            onStartClick = { viewModel.startGame() }
        )
    } else {
        PlayScreen(viewModel = viewModel)
    }
}

@Composable
fun GameOverScreen(score: Int, onStartClick: () -> Unit) {
    // A Column stacks items vertically
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Fuchka Frenzy!", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (score > 0) {
            Text("Final Score: $score", fontSize = 24.sp, color = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = onStartClick, modifier = Modifier.size(width = 150.dp, height = 50.dp)) {
            Text("Play", fontSize = 20.sp)
        }
    }
}

@Composable
fun PlayScreen(viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- TOP BAR: Score & Timer ---
        // A Row places items horizontally next to each other
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Score: ${viewModel.score}", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // Turn the timer red if they are running out of time!
            val timerColor = if (viewModel.timeLeft <= 5) Color.Red else MaterialTheme.colorScheme.onBackground
            Text("Time: ${viewModel.timeLeft}s", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = timerColor)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- THE CUSTOMER'S ORDER ---
        Text("Customer Wants:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            viewModel.currentOrder.forEach { ingredient ->
                Text(text = ingredient.emoji, fontSize = 40.sp, modifier = Modifier.padding(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- THE PLAYER'S TRAY ---
        Text("Your Tray:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFFFFF9C4), shape = RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            viewModel.playerInput.forEach { ingredient ->
                Text(text = ingredient.emoji, fontSize = 40.sp, modifier = Modifier.padding(4.dp))
            }
        }

        // 'weight(1f)' acts like a spring, pushing everything below it to the bottom of the screen.
        // This ensures the buttons look good on ANY screen size.
        Spacer(modifier = Modifier.weight(1f))

        // --- INGREDIENT BUTTONS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Ingredient.values().forEach { ingredient ->
                Button(
                    onClick = { viewModel.onIngredientTapped(ingredient) },
                    modifier = Modifier.size(75.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = ingredient.emoji, fontSize = 28.sp)
                        Text(text = ingredient.label, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}