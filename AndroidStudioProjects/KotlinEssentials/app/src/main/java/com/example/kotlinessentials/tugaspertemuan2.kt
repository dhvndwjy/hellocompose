package com.example.kotlinessentials

data class Mahasiswa(
    val nim: String,
    val nama: String,
    val ipk: Double
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

fun showData(data: List<String>) {
    println("\n----- Mahasiswa Yang Lolos Dengan IPK >= 3.5 -----")
    data.forEachIndexed { index, mhs ->
        println("${index + 1}. $mhs")
    }
}
fun showError(message: String) = println("Show Error: $message")
fun showLoadingSpinner()       = println("Loading...")

val daftar = listOf(
    Mahasiswa("010101", "Harry", 3.89),
    Mahasiswa("010102", "Niall", 3.47),
    Mahasiswa("010103", "Zayn", 3.68),
    Mahasiswa("010104", "Liam", 3.95),
    Mahasiswa("010105", "Louis", 3.34)
)

fun main() {
    println("----- DAFTAR SELURUH NAMA MAHASISWA, NIM & IPK -----")
    daftar.forEachIndexed { index, mhs ->
        println("${index + 1}. ${mhs.nama}\n NIM: ${mhs.nim}, IPK: ${mhs.ipk}\n")
    }

    val hasil = daftar
        .filter { it.ipk >= 3.5 }
        .sortedBy { it.nama }
        .map { "${it.nama}: ${it.ipk}" }

    val state: UiState<List<String>> = if (hasil.isNotEmpty()) {
        UiState.Success(hasil)
    } else {
        UiState.Error("Tidak Ada Mahasiswa Yang Lolos Dengan IPK >= 3.5")
    }

    when (state) {
        is UiState.Success -> showData(state.data)
        is UiState.Error   -> showError(state.message)
        is UiState.Loading -> showLoadingSpinner()
    }
}
