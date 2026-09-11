package com.example.ch04starter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ch04starter.ui.theme.Ch04StarterTheme

// ============================================================================
// TODO Pertemuan 4 — BmiScreen (soal #1 & #4 di Tugas Pertemuan 4)
//
// Base kalkulator BMI di bawah ini SUDAH BERFUNGSI (baseline dari praktikum:
// rememberSaveable untuk berat/tinggi/status hitung, derivedStateOf untuk
// bmi & kategori). Tugas kalian menambahkan tiga hal ke atasnya:
//
// [ ] 1a. Tabel interpretasi BMI di bawah kartu hasil — 4 baris:
//         Kurus (<18.5) / Normal (18.5–24.9) / Gemuk (25–29.9) / Obesitas (≥30)
// [ ] 1b. Tombol "Reset" yang mengembalikan berat & tinggi ke nilai default
//         (60 kg / 165 cm) dan menyembunyikan kartu hasil lagi
// [ ] 4.  (Tantangan) Tampilkan Snackbar berisi kategori BMI selama 3 detik
//         setiap kali tombol "Hitung BMI" ditekan, pakai LaunchedEffect
//
// Cari marker "TODO" di file ini untuk lokasi persisnya.
// ============================================================================

@Composable
fun BmiScreen() {
    // rememberSaveable: bertahan saat rotasi layar
    var beratKg    by rememberSaveable { mutableFloatStateOf(60f) }
    var tinggiCm   by rememberSaveable { mutableFloatStateOf(165f) }
    var isDihitung by rememberSaveable { mutableStateOf(false) }

    // derivedStateOf: hanya recompose saat nilai BMI benar-benar berubah
    var hitungTrigger by rememberSaveable { mutableIntStateOf(0) }

    val bmi by remember {
        derivedStateOf{
            val tinggiM = tinggiCm / 100f
            beratKg / (tinggiM * tinggiM)
        }
    }

    val (kategori, warna) = remember(bmi) {
        when {
            bmi < 18.5f -> "Berat Badan Kurang" to Color(0xFF1565C0)
            bmi < 25.0f -> "Berat Badan Normal" to Color(0xFF2E7D32)
            bmi < 30.0f -> "Kelebihan Berat"    to Color(0xFFE65100)
            else        -> "Obesitas"            to Color(0xFFC62828)
        }
    }

    // TODO 4: siapkan SnackbarHostState di sini (remember { SnackbarHostState() })
    //         lalu tempatkan Scaffold(snackbarHost = { SnackbarHost(...) }) di
    //         pemanggil, ATAU pindahkan BmiScreen jadi menerima SnackbarHostState
    //         sebagai parameter (state hoisting!) dari MainScreen.

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(hitungTrigger) {
        if(hitungTrigger > 0) {
            snackbarHostState.showSnackbar(
                message = "Kategori BMI: $kategori",
            )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
            Text(
                text = "Kalkulator BMI",
                style = MaterialTheme.typography.headlineMedium
            )

            InputSlider(
                label = "Berat Badan",
                value = beratKg,
                unit = "kg",
                range = 30f..150f,
                onValueChange = { beratKg = it; isDihitung = false }
            )

            InputSlider(
                label = "Tinggi Badan",
                value = tinggiCm,
                unit = "cm",
                range = 100f..220f,
                onValueChange = { tinggiCm = it; isDihitung = false }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        isDihitung = true
                        // TODO 4: trigger tampilan Snackbar di sini (lewat LaunchedEffect
                        //         yang mengamati sebuah key, bukan langsung showSnackbar()
                        //         di dalam onClick — kenapa? lihat lagi slide LaunchedEffect)
                        hitungTrigger++
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Hitung BMI")
                }

                // TODO 1b: ganti blok di bawah ini jadi tombol Reset sungguhan.
                //          Reset harus mengembalikan beratKg -> 60f, tinggiCm -> 165f,
                //          dan isDihitung -> false.
                Button(
                    onClick = {/* TODO: reset ke nilai default */
                        beratKg = 60f
                        tinggiCm = 165f
                        isDihitung = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
            }

            // AnimatedVisibility: fade-in saat isDihitung = true
            AnimatedVisibility(visible = isDihitung) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = warna.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "%.1f".format(bmi),
                                style = MaterialTheme.typography.displayMedium,
                                color = warna
                            )
                            Text(
                                text = kategori,
                                style = MaterialTheme.typography.titleMedium,
                                color = warna
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Berat: ${"%.0f".format(beratKg)} kg  " +
                                        "Tinggi: ${"%.0f".format(tinggiCm)} cm",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // TODO 1a: panggil composable tabel interpretasi BMI di sini,
                    //          misalnya InterpretasiBmiTable() — buat function-nya
                    //          sendiri di bawah, 4 baris (Kurus/Normal/Gemuk/Obesitas)
                    //          plus rentang nilainya masing-masing.

                    InterpretasiBmiTable()
                }
            }
        }
    }
}

@Composable
fun InputSlider(
    label:         String,
    value:         Float,
    unit:          String,
    range:         ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text       = "${"%.0f".format(value)} $unit",
                style      = MaterialTheme.typography.bodyLarge,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value         = value,
            onValueChange = onValueChange,
            valueRange    = range,
            modifier      = Modifier.fillMaxWidth()
        )
    }
}

// TODO 1a: implementasikan composable ini.
// @Composable
// fun InterpretasiBmiTable() {
//     // 4 baris: Kurus / Normal / Gemuk / Obesitas + rentang nilai masing-masing.
//     // Boleh pakai Column + Row manual, atau lihat lagi cara render_table di
//     // README chapter lain sebagai inspirasi tata letak (bukan kode Compose,
//     // itu untuk generator slide — hanya untuk ide visual tabel).
// }

@Composable
fun InterpretasiBmiTable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Interpretasi BMI",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Kategori",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Rentang BMI",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Kurus",
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "< 18.5",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Normal",
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "18.5 - 24.9",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Gemuk",
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "25 - 29.9",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Obesitas",
                modifier = Modifier.weight(1f)
            )

            Text(
                text = ">= 30",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BmiScreenPreview() {
    Ch04StarterTheme {
        BmiScreen()
    }
}
