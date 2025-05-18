package com.example.divorceparty

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.divorceparty.ui.theme.DivorcePartyTheme
import androidx.compose.ui.unit.sp
import com.example.divorceparty.ui.theme.PoetsenOne

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DivorcePartyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets.systemBars
                    ) { innerPadding ->
                        GreetingText(
                            message = "Divorce Party",
                            from = "Te invito a mi: ",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }

    }
}


@Composable
fun GreetingText(message: String, from: String, modifier: Modifier = Modifier) {
    Column  (
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .background(color = Color(0xFF631A8A)) )  {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // altura de la imagen de fondo
                .background(Color(0xFF452280)) // color de fondo de la imagen de fondo
        ) {
            Image(
                painter = painterResource(id = R.drawable.party_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop // recorta la imagen para cubrir todo el contenedor
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = from,
                    fontFamily = FontFamily.Cursive,
                    fontSize = 55.sp,
                    color = Color.hsl(270f, 1f, 0.85f),
                )

                Text(
                    text = message,
                    fontSize = 70.sp,
                    fontFamily = PoetsenOne,
                    color = Color.hsl(270f, 1f, 0.85f),
                    lineHeight = 66.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        // Nueva información
        InfoSection()
    }

}

@Preview(showBackground  = true)
@Composable
fun DivorcePartyPreview() {
    DivorcePartyTheme {
        GreetingText(message = "Divorce Party", from = "Te invito a: ")
    }
}


@Composable
fun InfoItem(iconResId: Int, iconColor: Color, text: String, onClick: () -> Unit) {
    val context = LocalContext.current
    iconColor
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp) // Espaciado alrededor
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 8.dp)
                .clickable { onClick() }
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 28.sp,
            fontFamily = PoetsenOne,
            lineHeight = 28.sp,

            )
    }
}

@Composable
fun InfoSection() {
    val context = LocalContext.current

    Column {
        InfoItem(
            iconResId = R.drawable.ic_location,
            iconColor = Color(0xFF2196F3),
            text = "Lugar: Universidad Central del Ecuador",
            onClick = {
                val gmmIntentUri = Uri.parse("geo:0,0?q=Universidad Central del Ecuador")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            }
        )


        InfoItem(
            iconResId = R.drawable.ic_calendar,
            iconColor = Color(0xFFFFC107), // Azul
            text = "Fecha: 23 de Mayo, 2025",
            onClick = {
                // Opcional: abrir calendario, pero normalmente no es interactivo
            }
        )

        InfoItem(
            iconResId = R.drawable.ic_share,
            text = "Dress Code: Party dress",
            iconColor = Color(0xFFF8F4F3),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://ar.pinterest.com/search/pins/?q=Party%20dress%20negro&rs=typed")
                }
                context.startActivity(intent)
            }
        )


        InfoItem(
            iconResId = R.drawable.instagram, // Usa un ícono adecuado si tienes uno de Instagram
            text = "Instagram: @gabylizeth77",
            iconColor = Color(0xFFE91E63),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://www.instagram.com/gabylizeth77?igsh=MTF6cHZ2MmF3NmFueg&utm_source=qr")
                }
                context.startActivity(intent)
            }
        )


        InfoItem(
            iconResId = R.drawable.ic_phone,
            text = "Teléfono: +593 987654321",
            iconColor = Color(0xFF4CAF50),
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:+593987654321")
                }
                context.startActivity(intent)
            }
        )
    }
}