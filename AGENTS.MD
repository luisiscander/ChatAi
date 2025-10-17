# AGENTS.md - Chat AI Android App

## 📱 Stack Tecnológico

### Frontend: Android Nativo
- **Kotlin** con Coroutines y Flow
- **Jetpack Compose** con BOM 2024+
- **Material 3** con theming dinámico
- **Navigation Compose** 2.7+ con type-safe navigation
- **Hilt** para Dependency Injection
- **MVVM** como patrón de presentación
- **Clean Architecture** (3 capas: Data, Domain, Presentation)

### Backend: Node.js
- **Express.js** o Fastify (a elección)
- **OpenAI SDK** integrado
- **PostgreSQL** + Prisma ORM
- **JWT** para autenticación

---

## 📋 Resumen de Requisitos

### Épicas y Historias de Usuario

#### 🚀 ÉPICA 1: Onboarding y Configuración Inicial
- **HU-001**: Primer uso de la aplicación (3 pts)
- **HU-002**: Configurar API Key de OpenRouter (5 pts)

#### 💬 ÉPICA 2: Gestión de Conversaciones
- **HU-003**: Ver lista de conversaciones (5 pts)
- **HU-004**: Crear nueva conversación (3 pts)
- **HU-005**: Eliminar conversación (3 pts)
- **HU-006**: Archivar conversación (3 pts)
- **HU-007**: Buscar conversaciones (5 pts)

#### 🤖 ÉPICA 3: Chat y Mensajería
- **HU-008**: Enviar mensaje a IA (8 pts)
- **HU-009**: Recibir respuesta con streaming (8 pts)
- **HU-010**: Ver historial de mensajes (5 pts)
- **HU-011**: Eliminar mensaje individual (3 pts)

#### 🔧 ÉPICA 4: Gestión de Modelos
- **HU-012**: Ver modelos disponibles (5 pts)
- **HU-013**: Cambiar modelo durante conversación (5 pts)
- **HU-014**: Configurar parámetros del modelo (5 pts)

#### ⚙️ ÉPICA 5: Configuración y Preferencias
- **HU-015**: Gestionar API Key (3 pts)
- **HU-016**: Cambiar tema de la aplicación (3 pts)
- **HU-017**: Configurar modelo por defecto (2 pts)
- **HU-018**: Exportar conversaciones (5 pts)
- **HU-019**: Eliminar todos los datos (3 pts)

#### 🚀 ÉPICA 6: Funcionalidades Avanzadas
- **HU-020**: Comparar respuestas de múltiples modelos (8 pts)
- **HU-021**: Ver uso de tokens y costos (5 pts)
- **HU-022**: Modo offline - Ver conversaciones guardadas (3 pts)
- **HU-023**: Compartir conversación (3 pts)
- **HU-024**: Sistema de favoritos (3 pts)

#### ⚡ ÉPICA 7: Rendimiento y Optimización
- **HU-025**: Caché de modelos disponibles (3 pts)
- **HU-026**: Paginación de mensajes (5 pts)

#### 🔒 ÉPICA 8: Seguridad y Privacidad
- **HU-027**: Almacenamiento seguro de API Key (5 pts)
- **HU-028**: Encriptación de conversaciones sensibles (8 pts)

---

## 📊 Estadísticas del Proyecto

- **Total Historias de Usuario**: 28
- **Total Puntos de Estimación**: ~135 puntos
- **Sprint Size sugerido**: 20-25 puntos (sprints de 2 semanas)

### Priorización

#### 🔥 Prioridad ALTA (MVP) - 37 puntos
- Onboarding y Configuración Inicial
- Gestión Básica de Conversaciones
- Chat y Mensajería Core
- Modelos Disponibles
- Gestión de API Key
- Seguridad API Key

#### 📈 Prioridad MEDIA (Post-MVP) - 45 puntos
- Búsqueda y Archivo
- Gestión Avanzada de Modelos
- Preferencias UI
- Estadísticas y Uso
- Modo Offline
- Optimizaciones

#### 🎨 Prioridad BAJA (Nice-to-Have) - 53 puntos
- Comparación de Modelos
- Exportar/Compartir
- Favoritos
- Eliminar Mensajes
- Privacidad Avanzada

---

## 🎯 Características Principales

### Funcionalidades Core
- ✅ Chat con múltiples modelos de IA (OpenRouter)
- ✅ Streaming de respuestas en tiempo real
- ✅ Gestión de conversaciones (CRUD)
- ✅ Configuración segura de API Key
- ✅ Tema claro/oscuro
- ✅ Búsqueda en conversaciones

### Funcionalidades Avanzadas
- 🔄 Comparación de múltiples modelos
- 📊 Seguimiento de tokens y costos
- 📤 Exportar conversaciones
- ⭐ Sistema de favoritos
- 🔒 Encriptación de conversaciones
- 📱 Modo offline

---

## 🛠️ Tecnologías de Desarrollo

### Android
- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Arquitectura**: Clean Architecture + MVVM
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Navegación**: Navigation Compose
- **Base de datos**: Room
- **Networking**: Retrofit + OkHttp

### Backend (Opcional)
- **Runtime**: Node.js
- **Framework**: Express.js/Fastify
- **Base de datos**: PostgreSQL
- **ORM**: Prisma
- **Autenticación**: JWT
- **APIs**: OpenAI SDK

---

## 📝 Notas para Desarrolladores

⚠️ **IMPORTANTE**: No hacer push o commit sin preguntar primero.

### Estructura del Proyecto
```
app/
├── src/main/java/com/example/chatai/
│   ├── data/           # Capa de datos
│   ├── domain/         # Capa de dominio
│   ├── presentation/   # Capa de presentación
│   └── di/            # Dependency Injection
```

### Patrones de Desarrollo
- **Clean Architecture** con separación de responsabilidades
- **MVVM** para la capa de presentación
- **Repository Pattern** para acceso a datos
- **Use Cases** para lógica de negocio

### APIs Externas
- **OpenRouter API**: Para acceso a modelos de IA
- **Modelos soportados**: GPT-4, Claude 3, Llama 3, Gemini Pro

---

**Última actualización**: $(date)
**Versión del proyecto**: 1.0.0
**Estado**: En desarrollo
