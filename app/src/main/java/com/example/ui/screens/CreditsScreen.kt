package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.FuturisticAudioSynth
import com.example.data.model.CreditTransaction
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreditsScreen(
    credits: Int,
    usedToday: Int,
    transactions: List<CreditTransaction>,
    onRedeemCoupon: (String) -> Result<Int>,
    onPurchasePackage: (name: String, credits: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var couponInput by remember { mutableStateOf("") }
    var couponMessage by remember { mutableStateOf<String?>(null) }
    var isCouponSuccess by remember { mutableStateOf(false) }

    val suggestedCoupons = listOf("WELCOME50", "FRIEND100", "SAVE200", "CYBER2026", "FUTUREAI")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .testTag("credits_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CyberCard(tag = "balance_hud_card") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(CyberSurfaceVariant, Color(0xFF1E1B4B))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CREDIT VAULT STATUS",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            CyberBadge(text = "FREE TIER", color = WarningAmber)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$credits",
                                color = TextPrimary,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CREDITS",
                                color = NeonCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Used Today: $usedToday CR • Auto-refills to 2,400 daily credits at midnight",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            CyberCard(tag = "coupon_card") {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "REDEEM PROMO CODE",
                            color = WarningAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = couponInput,
                            onValueChange = { couponInput = it.uppercase() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("coupon_input"),
                            placeholder = {
                                Text(text = "Enter code (e.g. CYBER2026)", color = TextMuted, fontSize = 12.sp)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CyberSurfaceVariant,
                                unfocusedContainerColor = CyberSurfaceVariant,
                                focusedBorderColor = WarningAmber,
                                unfocusedBorderColor = CyberCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        CyberButton(
                            text = "APPLY",
                            onClick = {
                                val res = onRedeemCoupon(couponInput)
                                res.onSuccess { added ->
                                    isCouponSuccess = true
                                    couponMessage = "Success! Added +$added credits to your vault."
                                    couponInput = ""
                                    FuturisticAudioSynth.playCompletionChord()
                                }.onFailure { err ->
                                    isCouponSuccess = false
                                    couponMessage = err.message ?: "Failed to redeem code"
                                    FuturisticAudioSynth.playBlip(400f, 60)
                                }
                            },
                            enabled = couponInput.isNotBlank(),
                            tag = "redeem_coupon_btn"
                        )
                    }

                    if (couponMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = couponMessage!!,
                            color = if (isCouponSuccess) NeonGreen else DangerCoral,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Quick Codes:",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(suggestedCoupons) { code ->
                            CyberBadge(
                                text = code,
                                color = WarningAmber,
                                modifier = Modifier.clickable {
                                    couponInput = code
                                    FuturisticAudioSynth.playBlip(800f, 30)
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "CREDIT PACKAGES (OPTIONAL BOOST)",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PackageCard(
                    title = "Starter",
                    credits = 5000,
                    price = "$9.99",
                    isPopular = false,
                    modifier = Modifier.weight(1f),
                    onBuy = {
                        onPurchasePackage("Starter", 5000)
                        FuturisticAudioSynth.playCompletionChord()
                    }
                )
                PackageCard(
                    title = "Pro Studio",
                    credits = 15000,
                    price = "$24.99",
                    isPopular = true,
                    modifier = Modifier.weight(1f),
                    onBuy = {
                        onPurchasePackage("Pro Studio", 15000)
                        FuturisticAudioSynth.playCompletionChord()
                    }
                )
                PackageCard(
                    title = "Enterprise",
                    credits = 50000,
                    price = "$79.99",
                    isPopular = false,
                    modifier = Modifier.weight(1f),
                    onBuy = {
                        onPurchasePackage("Enterprise", 50000)
                        FuturisticAudioSynth.playCompletionChord()
                    }
                )
            }
        }

        item {
            Text(
                text = "TRANSACTION LEDGER",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (transactions.isEmpty()) {
            item {
                Text(
                    text = "No credit transactions recorded yet.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        } else {
            items(transactions) { tx ->
                TransactionRow(tx = tx)
            }
        }
    }
}

@Composable
fun PackageCard(
    title: String,
    credits: Int,
    price: String,
    isPopular: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit
) {
    Card(
        modifier = modifier
            .border(
                1.dp,
                if (isPopular) NeonCyan else CyberCardBorder,
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isPopular) {
                CyberBadge(text = "MOST POPULAR", color = NeonCyan)
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "+${credits / 1000}k CR",
                color = WarningAmber,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = price,
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            CyberButton(
                text = "GET",
                isPrimary = isPopular,
                onClick = onBuy,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TransactionRow(tx: CreditTransaction) {
    val dateStr = remember(tx.createdAt) {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(tx.createdAt))
    }
    val isPositive = tx.creditsChange > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.note,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$dateStr • Balance: ${tx.balanceAfter} CR",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = if (isPositive) "+${tx.creditsChange}" else "${tx.creditsChange}",
                color = if (isPositive) NeonGreen else DangerCoral,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
