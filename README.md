# 📱 Mobile TurcoEspañolNiños

Aplicación móvil educativa e interactiva diseñada para la enseñanza bilingüe (Turco - Español) orientada a niños, desarrollada en Android nativo con Kotlin y Jetpack Compose.

---

## 👥 Integrantes del Equipo
* **Jacqueline Pérez**
* **Carolina Gavilán**
* **Hans Román**

---

## 📝 Descripción del Proyecto
**TurcoEspañolNiños** es una herramienta digital de aprendizaje que combina componentes visuales interactivos, animaciones dinámicas y retroalimentación táctil/sonora para facilitar la adquisición de vocabulario en turco y español para niños. Implementa estándares modernos de arquitectura Android para asegurar rendimiento, escalabilidad y una experiencia de usuario fluida.

---

## ✨ Funcionalidades Principales
* **Registro y Gestión de Perfil de Usuario:** Creación de perfil con validaciones en tiempo real (alias obligatorio, rango de edad de 1 a 10 años).
* **Módulo de Vocabulario Interactivo:** Tarjetas bilingües con apoyo visual, audio nativo y práctica de pronunciación.
* **Animaciones e Interfaz Fluida:** Integración de `AnimatedVisibility` para transiciones suaves entre vistas.
* **Persistencia de Datos Local:** Guardado de preferencias de usuario mediante `Preferences DataStore` (`UserPreferences`).
* **Pruebas Unitarias Integradas:** Suite de test unitarios para validación automática de lógica de negocio (`PerfilValidatorTest`, `PronunciacionValidatorTest`).
* **Soporte de Recursos Nativos:** Retroalimentación con vibración nativa del dispositivo, acceso a cámara/galería y síntesis de voz (Text-to-Speech).

---

## 🛠️ Arquitectura y Tecnologías
* **Lenguaje:** Kotlin
* **UI Framework:** Jetpack Compose (Declarativa)
* **Arquitectura:** MVVM (Model - View - ViewModel) con Unidirectional Data Flow (UDF)
* **Gestión de Estado:** `StateFlow` & `LiveData`
* **Persistencia:** Android Jetpack DataStore (`UserPreferences`)
* **Testing:** JUnit & Kotlin Test Framework

---

## 🚀 Pasos para Ejecutar el Proyecto

1. **Clonar el Repositorio:**
   ```bash
   git clone [https://github.com/JacPM95/TurcoEspanolNinos.git](https://github.com/JacPM95/TurcoEspanolNinos.git)