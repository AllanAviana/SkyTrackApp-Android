package com.example.skytrackapp_android.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skytrackapp_android.viewmodel.WeatherViewModel
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.State
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.skytrackapp_android.R
import com.example.skytrackapp_android.data.model.HomeUiState
import com.example.skytrackapp_android.data.model.remote.fiveDayForecast.WeatherData
import com.example.skytrackapp_android.presentation.navigation.Routes
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: WeatherViewModel, navController: NavHostController) {
    val homeUiState = viewModel.homeUiState.collectAsState()

    if (homeUiState.value.isLoading) {
        LoadingScreen()
    } else if (homeUiState.value.isSuccessful) {
        SuccessScreen(homeUiState, navController, viewModel)
    }
}

@Composable
fun LoadingScreen() {
    Column {

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuccessScreen(
    homeUiState: State<HomeUiState>,
    navController: NavHostController,
    viewModel: WeatherViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF70BEFF))
    ) {
        Image(
            painter = painterResource(id = homeUiState.value.image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "${ homeUiState.value.weatherResponse?.city?.name }",
                fontSize = 40.sp,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
            ) {
                Text(
                    text = "${homeUiState.value.temperature}",
                    fontSize = 90.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                Text(
                    text = "°",
                    fontSize = 70.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.3f),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "H:${homeUiState.value.maxTemperature}°", fontSize = 20.sp, color = Color.White)
                Text(text = "L:${homeUiState.value.minTemperature}°", fontSize = 20.sp, color = Color.White)
            }
        }
        Image(
            painter = painterResource(id = R.drawable.bottomimage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .zIndex(1f)
                .alpha(0.26f),
            contentScale = ContentScale.Crop
        )

        Button(
            onClick = {
                viewModel.resetSearchUiState()
                navController.navigate(Routes.SearchScreen.route)
                      },
            colors = ButtonDefaults.buttonColors(Color.White),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 44.dp)
                .size(70.dp)
                .border(
                    width = 5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF000000).copy(alpha = 0.46f),
                            Color(0xFFFFFFFF).copy(alpha = 0.26f)
                        ),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    ),
                    shape = RoundedCornerShape(50)
                )
                .zIndex(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
        }


        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {
            ExpandableBottomCard(
                temps = homeUiState.value.temps
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpandableBottomCard(temps: List<WeatherData>) {

    val collapsedHeight = 340.dp
    val inputFmt  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val outputFmt = DateTimeFormatter.ofPattern("h a", Locale.US)

    var currentHeight by remember { mutableStateOf(collapsedHeight) }
    val animatedHeight by animateDpAsState(
        targetValue = currentHeight.coerceIn(
            collapsedHeight,
            LocalConfiguration.current.screenHeightDp.dp * 0.7f
        )
    )

    val timeCards = remember(temps) {
        val cards = mutableListOf<Triple<String, String, String>>()

        if (temps.isNotEmpty()) {
            var cursor = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
            val first  = LocalDateTime.parse(temps.first().dt_txt, inputFmt)

            while (cursor.isBefore(first)) {
                cards += Triple(
                    cursor.format(outputFmt),
                    temps.first().main.temp.toString(),
                    temps.first().weather[0].icon
                )
                cursor = cursor.plusHours(1)
            }
        }

        temps.forEach { w ->
            val base = LocalDateTime.parse(w.dt_txt, inputFmt)
            for (off in 2 downTo 0) cards += Triple(
                base.minusHours(off.toLong()).format(outputFmt),
                w.main.temp.toString(),
                w.weather[0].icon
            )
        }

        cards
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .background(
                Brush.linearGradient(
                    0f to Color(0xFF2E335A).copy(alpha = .26f),
                    1f to Color(0xFF1C1B33).copy(alpha = .26f),
                    start = Offset.Zero,
                    end   = Offset(1000f, 1000f)
                ),
                RoundedCornerShape(topStart = 44.dp, topEnd = 44.dp)
            )
            .draggable(
                state = rememberDraggableState { delta ->
                    currentHeight -= delta.dp
                },
                orientation = Orientation.Vertical
            )

    ) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
                .size(width = 40.dp, height = 4.dp)
                .background(Color.White.copy(.30f), RoundedCornerShape(2.dp))
        )

        LazyRow(
            Modifier.padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(timeCards.size) { i ->
                val (time, temp, icon) = timeCards[i]
                TimeCard(time, temp, icon, Modifier.padding(top = 34.dp))
            }
        }
    }
}


@Composable
fun TimeCard(
    time: String,
    temperature: String,
    icon: String,
    modifier: Modifier,
) {
    val formattedTime = try {
        val inputFormat = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("h a", java.util.Locale.getDefault())
        val date = inputFormat.parse(time.substringAfter(" "))
        outputFormat.format(date)
    } catch (e: Exception) {
        time
    }

    val formattedTemperature = try {
        temperature.substringBefore(".")
    } catch (e: Exception) {
        temperature
    }

    Column(
        modifier = modifier
            .padding(horizontal = 5.dp)
            .width(60.dp)
            .height(146.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(30.dp)
            )
            .clip(RoundedCornerShape(30.dp))
            .background(color = Color(0xFF1B1140).copy(alpha = 0.26f))
            .padding(vertical = 16.dp)
            ,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formattedTime,
            fontSize = 15.sp,
            color = Color.White
        )

        AsyncImage(
            model = "https://openweathermap.org/img/wn/$icon@2x.png",
            contentDescription = null,
            modifier = Modifier.size(44.dp).background(Color.Transparent)
        )

        Text(
            text = "${formattedTemperature}º",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.offset(x = 3.dp)
        )
    }
}




