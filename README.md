# Aplicación Educativa: Aprendizaje Bilingüe Turco - Español para Niños

## 👥 Integrantes del Equipo
* **Hans Roman** -
* **Jacqueline Pérez**
* **Carolina Gavilan**

---

## 📌 Descripción del Proyecto
Aplicación móvil desarrollada en **Android (Kotlin)** enfocada en la enseñanza didáctica del idioma Turco para niños menores de 10 años. Presenta una interfaz declarativa desarrollada con Jetpack Compose, con un flujo de registro simulado, validaciones en tiempo real y componentes interactivos para el aprendizaje de vocabulario bilingüe.

---

## 🚀 Funcionalidades Implementadas (Versión Base)

* **Arquitectura MVVM:** Separación limpia de capas entre la UI (`Jetpack Compose`), el estado reactivo (`StateFlow`) y la lógica de negocio (`ViewModel`).
* **Módulo de Vocabulario Bilingüe:** Tarjetas dinámicas ilustradas para la enseñanza de palabras en español y turco.
* **Integración Nativa de Audio:** Uso de la API nativa **Text-to-Speech (TTS)** configurada en turco (`tr_TR`) para la pronunciación asistida.
* **Persistencia Local con DataStore:** Guardado asíncrono del perfil y sesión del usuario usando `Preferences DataStore`.
* **Validación de Datos:** Lógica en el ViewModel para validar campos de usuario y restringir la edad (menores de 10 años).

---

## 🏗️ Estructura Modular del Proyecto

```text
com.example.turcoespanolninos/
├── data/
│   └── UserPreferences.kt       # Persistencia local con DataStore
├── model/
│   └── PerfilUsuarioUiState.kt  # Estados de la UI y dataclasses
├── viewmodel/
│   └── PerfilViewModel.kt       # Lógica de negocio y validaciones
└── ui/
    ├── theme/                  # Configuración de estilos y temas
    └── screen/
        ├── RegistroPerfilScreen.kt # Pantalla de registro y formulario
        └── VocabularioScreen.kt    # Pantalla interactiva con TTS
