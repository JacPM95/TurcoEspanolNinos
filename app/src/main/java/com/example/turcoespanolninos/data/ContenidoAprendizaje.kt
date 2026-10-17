package com.example.turcoespanolninos.data

import com.example.turcoespanolninos.model.NivelAprendizaje
import com.example.turcoespanolninos.model.PalabraAprendizaje

object ContenidoAprendizaje {
    val niveles = listOf(
        NivelAprendizaje(
            numero = 1,
            tituloEspanol = "Animales",
            tituloTurco = "Hayvanlar",
            palabras = listOf(
                PalabraAprendizaje("n1_perro", "Perro", "Köpek", "🐶"),
                PalabraAprendizaje("n1_gato", "Gato", "Kedi", "🐱"),
                PalabraAprendizaje("n1_pajaro", "Pájaro", "Kuş", "🐦"),
                PalabraAprendizaje("n1_caballo", "Caballo", "At", "🐴"),
                PalabraAprendizaje("n1_pez", "Pez", "Balık", "🐟")
            )
        ),
        NivelAprendizaje(
            numero = 2,
            tituloEspanol = "Naturaleza",
            tituloTurco = "Doğa",
            palabras = listOf(
                PalabraAprendizaje("n2_arbol", "Árbol", "Ağaç", "🌳"),
                PalabraAprendizaje("n2_pasto", "Pasto", "Çimen", "🌱"),
                PalabraAprendizaje("n2_montana", "Montaña", "Dağ", "⛰️"),
                PalabraAprendizaje("n2_rio", "Río", "Nehir", "🏞️"),
                PalabraAprendizaje("n2_sol", "Sol", "Güneş", "☀️")
            )
        ),
        NivelAprendizaje(
            numero = 3,
            tituloEspanol = "Objetos cotidianos",
            tituloTurco = "Günlük nesneler",
            palabras = listOf(
                PalabraAprendizaje("n3_mesa", "Mesa", "Masa", "🪑"),
                PalabraAprendizaje("n3_silla", "Silla", "Sandalye", "🪑"),
                PalabraAprendizaje("n3_cama", "Cama", "Yatak", "🛏️"),
                PalabraAprendizaje("n3_puerta", "Puerta", "Kapı", "🚪"),
                PalabraAprendizaje("n3_libro", "Libro", "Kitap", "📘")
            )
        ),
        NivelAprendizaje(
            numero = 4,
            tituloEspanol = "Alimentos y colores",
            tituloTurco = "Yiyecekler ve renkler",
            palabras = listOf(
                PalabraAprendizaje("n4_manzana", "Manzana", "Elma", "🍎"),
                PalabraAprendizaje("n4_pan", "Pan", "Ekmek", "🍞"),
                PalabraAprendizaje("n4_agua", "Agua", "Su", "💧"),
                PalabraAprendizaje("n4_rojo", "Rojo", "Kırmızı", "🔴"),
                PalabraAprendizaje("n4_azul", "Azul", "Mavi", "🔵")
            )
        )
    )
}
