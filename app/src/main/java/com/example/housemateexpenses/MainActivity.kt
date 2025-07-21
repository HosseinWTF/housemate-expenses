package com.example.housemateexpenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.housemateexpenses.ui.theme.HouseMateExpensesTheme
import com.google.firebase.FirebaseApp
import com.yourpackage.ui.AppNavGraph
import com.yourpackage.ui.screens.expense.ExpenseListScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
                HouseMateExpensesTheme {
                    val navController = rememberNavController()

                    Surface(color = MaterialTheme.colorScheme.background) {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            AppNavGraph(navController = navController,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HouseMateExpensesTheme {
    }
}