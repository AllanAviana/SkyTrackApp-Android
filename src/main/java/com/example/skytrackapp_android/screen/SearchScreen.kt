package com.example.skytrackapp_android.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    val focusManager       = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF63AFF5), Color(0xFF0C467C)),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
            .padding(top = 24.dp)
    ) {
        WeatherTopBar()

        SearchBar(
            value         = query,
            onValueChange = { query = it },
            onSearch      = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )

        LazyColumn {
            item {
                WeatherCard(
                    tempC = 25,
                    location = "São Paulo, Brazil"
                )
                WeatherCard(
                    tempC = 25,
                    location = "São Paulo, Brazil"
                )
                WeatherCard(
                    tempC = 25,
                    location = "São Paulo, Brazil"
                )
                WeatherCard(
                    tempC = 25,
                    location = "São Paulo, Brazil"
                )
                WeatherCard(
                    tempC = 25,
                    location = "São Paulo, Brazil"
                )
            }
        }
    }
}


@Composable
fun WeatherTopBar(
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 12.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .clickable { }
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Weather",
            color = Color.White,
            fontSize = 24.sp
        )
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { Text("Search for a city or airport") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF5C3F3F).copy(alpha = 0.26f),
                unfocusedContainerColor = Color(0xFF5C3F3F).copy(alpha = 0.26f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() })
        )
    }


}

@Composable
fun WeatherCard(
    tempC: Int,
    location: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50.dp)

    val glassBrush = Brush.radialGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.40f),
            Color.White.copy(alpha = 0f)
        ),
        center = Offset(300f, 0f),
        radius = 900f
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center
    ){
        Box(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .height(214.dp)
                .clip(shape)
                .background(glassBrush)
                .border(1.dp, Color.White.copy(alpha = 0.25f), shape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "$tempC°  ",
                    fontSize = 56.sp,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = location,
                        fontSize = 32.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp)
                        )
                }
            }
        }
    }
}


@Preview
@Composable
fun SearchScreenPreview(){
    SearchScreen()
}