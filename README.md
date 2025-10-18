# 🤖 Chat AI - Android App

Una aplicación Android moderna para chatear con múltiples modelos de inteligencia artificial usando OpenRouter.

## 📱 Características Principales

### ✅ Funcionalidades Implementadas
- **Onboarding inicial** con pantalla de bienvenida
- **Configuración de API Key** con enlace clicable a OpenRouter
- **Arquitectura limpia** siguiendo Clean Architecture y MVVM
- **Navegación fluida** entre pantallas
- **Tema Material 3** con soporte para tema claro/oscuro

### 🔄 Funcionalidades Planificadas
- Chat con múltiples modelos de IA (GPT-4, Claude 3, Llama, etc.)
- Streaming de respuestas en tiempo real
- Gestión de conversaciones (CRUD)
- Búsqueda en conversaciones
- Comparación de múltiples modelos
- Seguimiento de tokens y costos
- Exportar conversaciones
- Sistema de favoritos

## 🏗️ Arquitectura Técnica

### Stack Tecnológico
- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Arquitectura**: Clean Architecture + MVVM
- **DI**: Hilt (Dependency Injection)
- **Async**: Coroutines + Flow
- **Navegación**: Navigation Compose
- **Base de datos**: Room (futuro)
- **Networking**: Retrofit + OkHttp (futuro)

### Estructura del Proyecto
```
app/
├── src/main/java/com/example/chatai/
│   ├── data/                    # Capa de datos
│   │   └── local/              # Repositorios locales
│   ├── domain/                 # Capa de dominio
│   │   ├── repository/         # Interfaces de repositorios
│   │   └── usecase/           # Casos de uso
│   ├── presentation/           # Capa de presentación
│   │   ├── screens/           # Pantallas de la aplicación
│   │   ├── navigation/        # Gestión de navegación
│   │   ├── components/        # Componentes reutilizables
│   │   └── ChatAiApp.kt       # Composable principal
│   └── di/                    # Dependency Injection
├── ChatAiApplication.kt        # Application class
├── ChatAiViewModel.kt          # ViewModel principal
└── MainActivity.kt             # Activity principal
```

### Patrones de Desarrollo
- **Clean Architecture** con separación en 3 capas
- **MVVM** para la capa de presentación
- **Repository Pattern** para acceso a datos
- **Use Cases** para lógica de negocio
- **Dependency Injection** con Hilt

## 🚀 Instalación y Configuración

### Prerrequisitos
- Android Studio Hedgehog o superior
- JDK 11 o superior
- Android SDK 36
- Dispositivo Android o emulador con API 24+

### Configuración
1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/luisiscander/ChatAi.git
   cd ChatAi
   ```

2. **Abrir en Android Studio**:
   - Abrir Android Studio
   - Seleccionar "Open an existing project"
   - Navegar a la carpeta del proyecto

3. **Configurar API Key**:
   - Obtener API key de [OpenRouter](https://openrouter.ai/keys)
   - Configurar en la aplicación al primer uso

### Compilación
```bash
# Compilar solo código Kotlin (desarrollo)
./gradlew compileDebugKotlin

# Compilar APK completo
./gradlew assembleDebug

# Ejecutar tests
./gradlew test
```

## 📋 Historias de Usuario

### 🚀 ÉPICA 1: Onboarding y Configuración Inicial
- ✅ **HU-001**: Primer uso de la aplicación (3 pts)
- 🔄 **HU-002**: Configurar API Key de OpenRouter (5 pts)

### 💬 ÉPICA 2: Gestión de Conversaciones
- 🔄 **HU-003**: Ver lista de conversaciones (5 pts)
- 🔄 **HU-004**: Crear nueva conversación (3 pts)
- 🔄 **HU-005**: Eliminar conversación (3 pts)
- 🔄 **HU-006**: Archivar conversación (3 pts)
- 🔄 **HU-007**: Buscar conversaciones (5 pts)

### 🤖 ÉPICA 3: Chat y Mensajería
- 🔄 **HU-008**: Enviar mensaje a IA (8 pts)
- 🔄 **HU-009**: Recibir respuesta con streaming (8 pts)
- 🔄 **HU-010**: Ver historial de mensajes (5 pts)
- 🔄 **HU-011**: Eliminar mensaje individual (3 pts)

### 🔧 ÉPICA 4: Gestión de Modelos
- 🔄 **HU-012**: Ver modelos disponibles (5 pts)
- 🔄 **HU-013**: Cambiar modelo durante conversación (5 pts)
- 🔄 **HU-014**: Configurar parámetros del modelo (5 pts)

### ⚙️ ÉPICA 5: Configuración y Preferencias
- 🔄 **HU-015**: Gestionar API Key (3 pts)
- 🔄 **HU-016**: Cambiar tema de la aplicación (3 pts)
- 🔄 **HU-017**: Configurar modelo por defecto (2 pts)
- 🔄 **HU-018**: Exportar conversaciones (5 pts)
- 🔄 **HU-019**: Eliminar todos los datos (3 pts)

### 🚀 ÉPICA 6: Funcionalidades Avanzadas
- 🔄 **HU-020**: Comparar respuestas de múltiples modelos (8 pts)
- 🔄 **HU-021**: Ver uso de tokens y costos (5 pts)
- 🔄 **HU-022**: Modo offline - Ver conversaciones guardadas (3 pts)
- 🔄 **HU-023**: Compartir conversación (3 pts)
- 🔄 **HU-024**: Sistema de favoritos (3 pts)

### ⚡ ÉPICA 7: Rendimiento y Optimización
- 🔄 **HU-025**: Caché de modelos disponibles (3 pts)
- 🔄 **HU-026**: Paginación de mensajes (5 pts)

### 🔒 ÉPICA 8: Seguridad y Privacidad
- 🔄 **HU-027**: Almacenamiento seguro de API Key (5 pts)
- 🔄 **HU-028**: Encriptación de conversaciones sensibles (8 pts)

## 📊 Estadísticas del Proyecto

- **Total Historias de Usuario**: 28
- **Total Puntos de Estimación**: ~135 puntos
- **Sprint Size sugerido**: 20-25 puntos (sprints de 2 semanas)
- **Estado actual**: MVP en desarrollo

### Priorización
- 🔥 **Prioridad ALTA (MVP)**: 37 puntos
- 📈 **Prioridad MEDIA (Post-MVP)**: 45 puntos
- 🎨 **Prioridad BAJA (Nice-to-Have)**: 53 puntos

## 🔧 APIs Externas

### OpenRouter API
- **URL**: https://openrouter.ai/
- **Propósito**: Acceso a múltiples modelos de IA
- **Modelos soportados**: GPT-4, Claude 3, Llama 3, Gemini Pro, y más
- **Autenticación**: API Key

## 📝 Desarrollo

### Instrucciones para Desarrolladores

⚠️ **IMPORTANTE**: No hacer push o commit sin preguntar primero.

**COMPILACIÓN OBLIGATORIA**: Cada vez que se hagan cambios en el código, se debe compilar el proyecto usando `./gradlew compileDebugKotlin`. Si hay errores de compilación, se deben resolver inmediatamente antes de continuar.

### Flujo de Desarrollo
1. Crear rama feature desde main
2. Implementar funcionalidad
3. Compilar y verificar sin errores
4. Crear pull request
5. Revisar y mergear
6. Eliminar rama feature

### Convenciones de Código
- **Naming**: camelCase para variables, PascalCase para clases
- **Packages**: com.example.chatai.capa.funcionalidad
- **Composables**: PascalCase con sufijo Screen/Component
- **ViewModels**: PascalCase con sufijo ViewModel
- **Use Cases**: PascalCase con sufijo UseCase

## 🧪 Testing

### Estructura de Tests
```
app/src/test/           # Unit tests
app/src/androidTest/    # Integration tests
```

### Comandos de Testing
```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests de una clase específica
./gradlew test --tests "com.example.chatai.ChatAiViewModelTest"

# Ejecutar tests con coverage
./gradlew testDebugUnitTestCoverage
```

## 📱 Screenshots

### Pantalla de Onboarding
- Logo de la aplicación
- Mensaje de bienvenida
- Botón "Comenzar"

### Pantalla de Configuración de API Key
- Instrucciones claras
- Campo de entrada para API key
- Enlace clicable a OpenRouter

### Pantalla Principal
- Interfaz preparada para chat
- Navegación fluida

## 🤝 Contribución

### Cómo Contribuir
1. Fork el repositorio
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

### Estándares de Código
- Seguir las convenciones de Kotlin
- Escribir tests para nueva funcionalidad
- Documentar funciones públicas
- Mantener arquitectura limpia

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Autores

- **luisiscander** - *Desarrollo inicial* - [GitHub](https://github.com/luisiscander)

## 🙏 Agradecimientos

- **OpenRouter** por proporcionar acceso a múltiples modelos de IA
- **Jetpack Compose** por el framework de UI moderno
- **Material Design** por el sistema de diseño
- **Hilt** por la inyección de dependencias

---

**Última actualización**: $(date)
**Versión del proyecto**: 1.0.0
**Estado**: En desarrollo activo

Para más información, consulta la [documentación del proyecto](AGENTS.md).
