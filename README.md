# Aplicación Educativa: Aprendizaje Bilingüe Turco - Español para Niños

## 👥 Integrantes del Equipo
* **Hans Roman** -
* **Jacqueline Pérez**
* **Carolina Gavilan**

---

## 📌 Descripción del Proyecto
Aplicación móvil desarrollada en **Android (Kotlin)** para que niños y niñas de habla turca, de 1 a 10 años, aprendan vocabulario inicial en español. El turco se utiliza como idioma de apoyo. La experiencia usa perfiles ficticios, navegación por niveles, audio en español, retroalimentación visual y progreso local.

> Privacidad: el MVP no requiere datos personales reales. La cámara y la galería se incluyen como demostración académica de recursos nativos y solo deben utilizarse con avatares, objetos o imágenes ficticias, públicas o autorizadas; nunca con fotografías reales de menores. La aplicación no conserva grabaciones de voz; el reconocimiento es procesado por el servicio de voz configurado en el dispositivo.

---

## 🚀 Funcionalidades Implementadas (Versión Base)

* **Arquitectura MVVM:** Separación limpia de capas entre la UI (`Jetpack Compose`), el estado reactivo (`StateFlow`) y la lógica de negocio (`ViewModel`).
* **Ruta de cuatro niveles:** Animales, naturaleza, objetos cotidianos, alimentos y colores.
* **Vocabulario bilingüe:** Español como idioma objetivo y turco como apoyo visual.
* **Audio nativo:** Text-to-Speech configurado en español para escuchar y repetir cada palabra.
* **Práctica de pronunciación:** Reconocimiento de voz en español con mensajes infantiles, reintentos y tolerancia a pequeñas variaciones.
* **Progreso y recompensas:** Una estrella por palabra, cinco extra por nivel y medalla final.
* **Modo de repaso:** Al finalizar, permite comenzar nuevamente conservando las estrellas acumuladas.
* **Persistencia local con DataStore:** Perfil ficticio, edad, nivel, palabras y estrellas.
* **Validaciones:** Alias obligatorio y acceso permitido únicamente entre 1 y 10 años inclusive.
* **Recursos nativos:** Cámara, galería, vibración y Text-to-Speech.
* **Adaptabilidad:** Formulario desplazable, teclado numérico y ajuste frente al teclado virtual.

---

## 🏗️ Estructura Modular del Proyecto

```text
com.example.turcoespanolninos/
├── data/
│   ├── ContenidoAprendizaje.kt  # Contenido bilingüe de los cuatro niveles
│   └── UserPreferences.kt       # Persistencia local con DataStore
├── model/
│   ├── Aprendizaje.kt           # Modelos de niveles, palabras y progreso
│   ├── PronunciacionValidator.kt # Comparación tolerante de la pronunciación
│   └── PerfilUsuarioUiState.kt  # Estado del formulario
├── viewmodel/
│   ├── AprendizajeViewModel.kt  # Progreso, estrellas y desbloqueo
│   └── PerfilViewModel.kt       # Perfil y validaciones
└── ui/
    ├── theme/                  # Configuración de estilos y temas
    └── screen/
        ├── RegistroPerfilScreen.kt # Pantalla de registro y formulario
        └── VocabularioScreen.kt    # Ruta con TTS, micrófono y recompensas
```

## ▶️ Ejecución

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Ejecutar en un dispositivo o emulador Android API 33 o superior.
4. Usar únicamente un perfil y recursos visuales ficticios o autorizados.
