package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          ColorChangerApp(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }
}

// Predefined palette of vibrant, high-contrast, modern designer colors.
private val ColorPalette = listOf(
  Color(0xFF6366F1), // Indigo
  Color(0xFF10B981), // Emerald
  Color(0xFFF43F5E), // Rose / Coral
  Color(0xFFF59E0B), // Amber
  Color(0xFF3B82F6), // Blue
  Color(0xFF8B5CF6), // Purple
  Color(0xFFEC4899)  // Pink
)

@Composable
fun ColorChangerApp(modifier: Modifier = Modifier) {
  var colorIndex by remember { mutableIntStateOf(0) }
  val activeColor = ColorPalette[colorIndex]

  // Smooth color transition animation for a cohesive and high-fidelity feel.
  val animatedColor by animateColorAsState(
    targetValue = activeColor,
    animationSpec = tween(durationMillis = 500),
    label = "TextColorTransition"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          )
        )
      )
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.Center),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Elegant greeting presentation card with a professional depth representation
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
          defaultElevation = 12.dp
        )
      ) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Hello, World!",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp,
            color = animatedColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .testTag("hello_text")
              .padding(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(36.dp))

      // Clickable horizontal indicator dots representing color choice
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 24.dp)
      ) {
        ColorPalette.forEachIndexed { index, color ->
          val isSelected = index == colorIndex
          
          Box(
            modifier = Modifier
              .size(if (isSelected) 36.dp else 28.dp)
              .clip(CircleShape)
              .background(color)
              .clickable(
                onClickLabel = "Select color ${index + 1}"
              ) {
                colorIndex = index
              }
          )
        }
      }

      // Large responsive key action button to cycle color sequentially
      Button(
        onClick = {
          colorIndex = (colorIndex + 1) % ColorPalette.size
        },
        modifier = Modifier
          .testTag("color_change_button")
          .height(56.dp)
          .fillMaxWidth(0.8f),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 4.dp,
          pressedElevation = 8.dp
        )
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh Color",
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Cycle Text Color",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

// Fallback greeting method mapping back to legacy screenshot and unit tests compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = "Hello $name!",
      fontSize = 24.sp,
      fontWeight = FontWeight.Medium,
      textAlign = TextAlign.Center
    )
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme {
    ColorChangerApp()
  }
}
