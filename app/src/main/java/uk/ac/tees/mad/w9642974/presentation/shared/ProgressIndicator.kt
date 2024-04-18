package uk.ac.tees.mad.w9642974.presentation.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.tees.mad.w9642974.ui.theme.success

@Composable
fun ProgressIndicator(
    progress: Float = 0.0f,
    size: Dp,
    strokeWidth: Dp = 8.dp,
    fontSize: TextUnit = 16.sp
) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { 1f },
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round,
            color = Color.LightGray.copy(0.3f),
            modifier = Modifier.size(size)
        )
        CircularProgressIndicator(
            progress = { progress/100 },
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round,
            color = success,
            modifier = Modifier.size(size)
        )
        Text(
            text = "${(progress).toInt()}%", fontSize = fontSize,
            style = MaterialTheme.typography.titleSmall
        )
    }
}