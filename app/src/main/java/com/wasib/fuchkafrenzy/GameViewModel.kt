package com.wasib.fuchkafrenzy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Ingredient(val emoji: String, val label: String) {
    SHELL("🥣", "Puri"),
    FILLING("🥔", "Potato"),
    TOK("🍋", "Tok"),       // Changed to Lemon (Unicode 6.0)
    ONION("🌶️", "Chili")     // Changed to Hot Pepper (Unicode 7.0)
}

class GameViewModel : ViewModel() {

    var score by mutableStateOf(0)
        private set

    var timeLeft by mutableStateOf(30)
        private set

    var currentOrder by mutableStateOf(generateRandomOrder())
        private set

    var playerInput by mutableStateOf(listOf<Ingredient>())
        private set

    var isGameOver by mutableStateOf(true) // Start with game over so we can show a "Start" button
        private set

    private var timerJob: Job? = null

    private fun generateRandomOrder(): List<Ingredient> {
        val orderLength = (3..5).random()
        return List(orderLength) { Ingredient.values().random() }
    }

    // Starts a fresh game
    fun startGame() {
        score = 0
        timeLeft = 30
        isGameOver = false
        currentOrder = generateRandomOrder()
        playerInput = emptyList()
        startTimer()
    }

    // Handles the 1-second countdown loop
    private fun startTimer() {
        timerJob?.cancel() // Stop any existing timer
        timerJob = viewModelScope.launch {
            while (timeLeft > 0) {
                delay(1000L) // Wait exactly 1 second
                timeLeft -= 1
            }
            isGameOver = true
        }
    }

    // Called whenever the player taps an ingredient button
    fun onIngredientTapped(ingredient: Ingredient) {
        if (isGameOver) return

        // Add the tapped ingredient to the list
        playerInput = playerInput + ingredient

        // If the player has tapped enough ingredients, check if they match
        if (playerInput.size == currentOrder.size) {
            checkOrder()
        }
    }

    // Validates the order
    private fun checkOrder() {
        if (playerInput == currentOrder) {
            // Correct! Add score and bonus time
            score += 10
            timeLeft += 2
            currentOrder = generateRandomOrder() // Give them a new order
        } else {
            // Wrong! Penalty time
            timeLeft -= 3
            if (timeLeft < 0) timeLeft = 0
        }
        // Clear the tray for the next attempt
        playerInput = emptyList()
    }
}