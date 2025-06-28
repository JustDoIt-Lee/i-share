package com.ryun.ishare.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.ryun.ishare.ui.data.curvedCharList
import com.ryun.ishare.ui.theme.BackgroundColor
import com.ryun.ishare.ui.theme.TitleTextColor
import com.ryun.ishare.ui.theme.YolkColor
import com.ryun.ishare.ui.theme.shizuruFont
import com.ryun.ishare.ui.data.FIGMA_WIDTH
import com.ryun.ishare.ui.data.FIGMA_HEIGHT

@Composable
fun MainScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // ✅ "i Share" 절대 위치 배치 (제플린 비율 보정)
        Text(
            text = "i Share",
            fontSize = 88.sp,
            color = TitleTextColor, // ✅ 이렇게 써야 함
            fontFamily = shizuruFont,
            //fontWeight = FontWeight.Normal,
            modifier = Modifier
                .absoluteOffset(x = 39.dp, y = 125.dp) // ✅ 비율 보정된 위치
        )

        // 곡선 텍스트 전체 구성
        curvedCharList.forEach {
            CurvedCharFromFigma(
                char = it.char,
                figmaX = it.x,
                figmaY = it.y,
                figmaWidth = it.width,
                figmaHeight = it.height,
                rotationZ = it.rotation,
                screenWidthDp = screenWidthDp,
                screenHeightDp = screenHeightDp
            )
        }

        val egg = rememberEggPosition(15f, 364f, 345f, 322.04f)
        val yolk = rememberEggPosition(106.5f, 443f, 164.29f, 163.47f)

        // ✅ 흰자 + 버튼 묶음 정확 위치
        Box(
            modifier = Modifier
                .absoluteOffset(x = egg.offsetX.dp, y = egg.offsetY.dp)
                .size(egg.width.dp, egg.height.dp),
            contentAlignment = Alignment.Center
        ) {
            // 계란 흰자
            EggWhite(onClick = {
                navController.navigate("idea_list")
            })
        }
        // ✅ 노른자 버튼
        YolkButton(
            offsetX = yolk.offsetX,
            offsetY = yolk.offsetY,
            width = yolk.width,
            height = yolk.height
        ) {
            navController.navigate("my_idea")
        }
    }
}

@Composable
fun EggWhite(onClick: () -> Unit) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() }
    ) {
        drawEggShape()
    }
}

fun DrawScope.drawEggShape() {
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * (345f / 345f), h * (130.778f / 323f))
        cubicTo(w, h * (221.061f / 323f), w * (323.643f / 345f), h, w * (190.024f / 345f), h)
        cubicTo(w * (99.2913f / 345f), h, 0f, h * (271.193f / 323f), 0f, h * (180.909f / 323f))
        cubicTo(0f, h * (90.6261f / 323f), w * (56.4048f / 345f), h * (13.6227f / 323f), w * (172.5f / 345f), 0f)
        cubicTo(w * (273.81f / 345f), h * (11.988f / 323f), w * (329.667f / 345f), h * (63.2093f / 323f), w, h * (130.778f / 323f))
        close()
    }
    drawPath(path, Color.White)
}

@Composable
fun CurvedCharFromFigma(
    char: Char,
    figmaX: Float,
    figmaY: Float,
    figmaWidth: Float,
    figmaHeight: Float,
    rotationZ: Float,
    screenWidthDp: Float,
    screenHeightDp: Float
) {
    val offsetX = (figmaX / FIGMA_WIDTH) * screenWidthDp
    val offsetY = (figmaY / FIGMA_HEIGHT) * screenHeightDp
    val width = (figmaWidth / FIGMA_WIDTH) * screenWidthDp
    val height = (figmaHeight / FIGMA_HEIGHT) * screenHeightDp

    Box(
        modifier = Modifier
            .absoluteOffset(x = offsetX.dp, y = offsetY.dp)
            .size(width.dp, height.dp)
            .graphicsLayer(
                rotationZ = rotationZ,
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            )
            .zIndex(1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.toString(),
            fontSize = 30.sp,
            fontFamily = shizuruFont,
            color = TitleTextColor,
        )
    }
}

@Composable
fun YolkButton(
    offsetX: Float,
    offsetY: Float,
    width: Float,
    height: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .absoluteOffset(x = offsetX.dp, y = offsetY.dp)
            .size(width.dp, height.dp)
            .background(color = YolkColor, shape = CircleShape)
            .clickable { onClick() }
    )
}

data class EggPosition(
    val offsetX: Float, val offsetY: Float,
    val width: Float, val height: Float
)

@Composable
fun rememberEggPosition(
    figmaX: Float, figmaY: Float,
    figmaWidth: Float, figmaHeight: Float
): EggPosition {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()

    return remember(configuration) {
        EggPosition(
            offsetX = (figmaX / FIGMA_WIDTH) * screenWidthDp,
            offsetY = (figmaY / FIGMA_HEIGHT) * screenHeightDp,
            width = (figmaWidth / FIGMA_WIDTH) * screenWidthDp,
            height = (figmaHeight / FIGMA_HEIGHT) * screenHeightDp
        )
    }
}