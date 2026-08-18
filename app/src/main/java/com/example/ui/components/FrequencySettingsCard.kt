package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PolishBorder
import com.example.ui.theme.PolishCard
import com.example.ui.theme.PolishPill
import com.example.ui.theme.PolishPrimary
import com.example.ui.theme.PolishPrimaryDark
import com.example.ui.theme.PolishSurface
import com.example.ui.theme.SlackingOrange
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun FrequencySettingsCard(
    frequencyPerDay: Int,
    idleNudgesEnabled: Boolean,
    notificationsEnabled: Boolean,
    totalNudgesDelivered: Int,
    respectPoints: Int,
    onFrequencyChange: (Int) -> Unit,
    onIdleToggle: (Boolean) -> Unit,
    onNotifsToggle: (Boolean) -> Unit,
    onTestNotification: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PolishBorder.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
            .testTag("frequency_settings_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PolishSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = PolishPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NUDGE FREQUENCY",
                        color = PolishPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                Surface(
                    color = PolishPill,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$frequencyPerDay / Day",
                        color = PolishPrimaryDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Segmented Preset Pills (Daily / Frequent / Intense)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(PolishCard)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PresetFrequencyPill(
                    title = "Daily",
                    isSelected = frequencyPerDay == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { onFrequencyChange(1) }
                )
                PresetFrequencyPill(
                    title = "Frequent",
                    isSelected = frequencyPerDay in 2..5,
                    modifier = Modifier.weight(1f),
                    onClick = { onFrequencyChange(3) }
                )
                PresetFrequencyPill(
                    title = "Intense",
                    isSelected = frequencyPerDay >= 6,
                    modifier = Modifier.weight(1f),
                    onClick = { onFrequencyChange(8) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Fine-Tune Messages per Day (Min 1, Max 12)",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            // Slider
            Slider(
                value = frequencyPerDay.toFloat(),
                onValueChange = { onFrequencyChange(it.toInt()) },
                valueRange = 1f..12f,
                steps = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("frequency_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = PolishPrimary,
                    activeTrackColor = PolishPrimary,
                    inactiveTrackColor = PolishBorder.copy(alpha = 0.5f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1/day (Min)", color = TextDim, fontSize = 11.sp)
                Text("3-4/day (Recommended)", color = PolishPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("12/day (Max)", color = TextDim, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Idle & Slacking Off Nudges Switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PolishCard)
                    .border(1.dp, PolishBorder.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = SlackingOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Anti-Slacking Slump Alerts",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Sends urgent wake-up boosts during common afternoon idle slump windows (2:30 PM - 4:00 PM).",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Switch(
                        checked = idleNudgesEnabled,
                        onCheckedChange = onIdleToggle,
                        modifier = Modifier.testTag("idle_nudges_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PolishPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = PolishCard
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Master Notifications Switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PolishCard)
                    .border(1.dp, PolishBorder.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = PolishPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Notification Bar Delivery",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Posts expandable quotes & micro-stories directly into the system notification shade.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = onNotifsToggle,
                        modifier = Modifier.testTag("notifications_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PolishPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = PolishCard
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Test Notifications Action Buttons
            Text(
                text = "Instant Notification Bar Simulator",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onTestNotification(false) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("test_goal_notif_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PolishPill,
                        contentColor = PolishPrimaryDark
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nudge Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onTestNotification(true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("test_idle_notif_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SlackingOrange.copy(alpha = 0.12f),
                        contentColor = SlackingOrange
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Idle Alert", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PresetFrequencyPill(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isSelected) PolishPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextMuted,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
