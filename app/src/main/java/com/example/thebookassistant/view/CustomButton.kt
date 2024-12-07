package com.example.thebookassistant.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.thebookassistant.ui.theme.CustomButtonTextStyle
import com.example.thebookassistant.ui.theme.Palette2
import com.example.thebookassistant.ui.theme.Palette3

@Composable
 fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
         containerColor = Palette3,
         contentColor = Color.White,
         disabledContainerColor = Palette2,
         disabledContentColor = Color.White
     ),
    buttonShape: Shape = RoundedCornerShape(20.dp),
    buttonSize: DpSize = DpSize(width = 140.dp, height = 45.dp),
    textStyle: TextStyle = CustomButtonTextStyle
 ) {
     Button(
         onClick = onClick,
         colors = buttonColors,
         shape = buttonShape,
         contentPadding = PaddingValues(12.dp),
         modifier = modifier.size(buttonSize),
         enabled = enabled
     ) {
         Text(if (isLoading) "Loading..." else text, style = textStyle)
     }
 }