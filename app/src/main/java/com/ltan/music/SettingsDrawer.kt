package com.ltan.music

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingScreen() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(),
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth(1.0f),
            textAlign = TextAlign.Start
        )
        AppHeader()
        Spacer(modifier = Modifier.size(16.dp))
        SettingItemCard()
        Spacer(modifier = Modifier.size(64.dp))
        Button(onClick = {}, Modifier.fillMaxHeight()) {
            Text(text = "退出登录", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AppHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(1.0f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_user),
            contentDescription = "me",
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = "Nickname",
            style = TextStyle(fontSize = 14.sp),
            modifier = Modifier.weight(1.0f)
        )
        Image(
            painter = painterResource(R.drawable.ic_scan),
            contentDescription = "scan",
            alignment = Alignment.BottomEnd,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
fun SettingItemCard() {
    Column(Modifier
        .fillMaxWidth()
        .background(color = Color.Gray, shape = RoundedCornerShape(8.dp))
        .padding(8.dp)
    ) {
        SettingItemRow("我的消息", "+99")
        SettingItemRow("云贝中心", "签到")
        SettingItemRow("创作者中心", "")
    }
}

@Composable
fun SettingItemRow(title: String, summary: String) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 2.dp)) {
        Row(modifier = Modifier.weight(1.0f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_email),
                contentDescription = title,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = title, style = TextStyle(color = Color.Black, fontSize = 16.sp))
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = summary, style = TextStyle(color = Color.Red, fontSize = 12.sp))
    }
}

@Preview
@Composable
fun SettingScreenPreview() {
    SettingScreen()
}
