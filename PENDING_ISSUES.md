# 📋 Issues Pendientes - Chat AI Android App

**Última Actualización**: 21 de Octubre, 2025  
**Total Issues Abiertas**: 35  
**Punto de Checkpoint**: v1.0-stable (commit 78fdb18)

---

## 📊 Resumen por Épica

| Épica | Issues | Prioridad | Puntos |
|-------|--------|-----------|--------|
| **ÉPICA 6: Funcionalidades Avanzadas** | 18 | Baja | 43 pts |
| **ÉPICA 7: Rendimiento y Optimización** | 8 | Media | 8 pts |
| **ÉPICA 8: Seguridad y Privacidad** | 9 | Alta-Baja | 13 pts |
| **TOTAL** | **35** | **-** | **64 pts** |

---

## 🎯 ÉPICA 6: Funcionalidades Avanzadas (18 issues)

### HU-020: Comparar respuestas de múltiples modelos (5 issues) - 8 puntos
**Prioridad**: Baja  
**Descripción**: Enviar el mismo mensaje a varios modelos simultáneamente para comparar respuestas

- **#107** - Activar modo comparación
  - Implementar interfaz para seleccionar múltiples modelos (hasta 4)
  - Mostrar costo estimado total
  
- **#108** - Enviar mensaje a múltiples modelos
  - Enviar mensaje simultáneamente a todos los modelos seleccionados
  - Crear secciones de respuesta por cada modelo
  
- **#109** - Ver respuestas lado a lado
  - Mostrar respuestas en columnas paralelas
  - Scroll independiente por columna
  - Mostrar modelo, tiempo de respuesta, tokens y costo por respuesta
  
- **#110** - Seleccionar mejor respuesta
  - Marcar respuesta como principal en el historial
  - Guardar respuestas alternativas
  
- **#111** - Desactivar modo comparación
  - Volver al modo normal de chat
  - Usar solo el modelo por defecto

---

### HU-021: Ver uso de tokens y costos (4 issues) - 5 puntos
**Prioridad**: Media  
**Descripción**: Visualizar cuántos tokens se han usado y el costo aproximado

- **#112** - Ver tokens por mensaje
  - Mostrar indicador de tokens usados por mensaje
  - Desglose de input/output tokens y costo
  
- **#113** - Ver estadísticas de conversación
  - Total mensajes, tokens, costo
  - Modelo más usado y tiempo total de chat
  
- **#114** - Ver uso total en configuración
  - Estadísticas totales de la app
  - Desglose por modelo
  - Gráfico de uso por día
  
- **#115** - Alerta de alto uso
  - Configurar límite mensual
  - Notificación al alcanzar 80% del límite

---

### HU-022: Modo offline - Ver conversaciones guardadas (4 issues) - 3 puntos
**Prioridad**: Media  
**Descripción**: Leer conversaciones sin conexión a internet

- **#116** - Abrir app sin conexión
  - Mostrar conversaciones guardadas sin conexión
  - Indicador "Sin conexión"
  
- **#117** - Intentar enviar mensaje sin conexión
  - Mensaje de error claro
  - Opción de guardar como borrador
  
- **#118** - Reconexión automática
  - Notificación de conexión restablecida
  - Opción de enviar mensajes en borrador
  
- **#119** - Sincronización al reconectar
  - Sincronizar cambios offline automáticamente
  - Indicador de sincronización

---

### HU-023: Compartir conversación (3 issues) - 3 puntos
**Prioridad**: Baja  
**Descripción**: Compartir conversaciones o mensajes específicos

- **#120** - Compartir mensaje individual
  - Compartir mensaje con atribución al modelo
  - Usar diálogo de compartir del sistema
  
- **#121** - Compartir conversación completa
  - Opciones de formato: texto, captura, link
  - Diálogo de compartir del sistema
  
- **#122** - Compartir con captura de pantalla
  - Generar imagen de la conversación
  - Diseño atractivo con logo (opcional)

---

### HU-024: Sistema de favoritos (4 issues) - 3 puntos
**Prioridad**: Baja  
**Descripción**: Marcar conversaciones como favoritas

- **#123** - Marcar conversación como favorita
  - Ícono de estrella en conversaciones
  - Indicador visual de estrella dorada
  
- **#124** - Ver solo conversaciones favoritas
  - Filtro de favoritas
  - Contador de favoritas
  
- **#125** - Quitar conversación de favoritos
  - Toggle del ícono de estrella
  - Remover indicador visual
  
- **#126** - Ordenar favoritas primero
  - Favoritas al inicio de la lista
  - Ordenación por fecha dentro de cada grupo

---

## ⚡ ÉPICA 7: Rendimiento y Optimización (8 issues)

### HU-025: Caché de modelos disponibles (4 issues) - 3 puntos
**Prioridad**: Media  
**Descripción**: Cargar modelos rápidamente usando caché local

- **#127** - Primera carga de modelos
  - Descargar modelos desde OpenRouter
  - Guardar en caché local
  - Indicador de carga
  
- **#128** - Cargar modelos desde caché
  - Carga instantánea desde caché
  - Verificación de actualizaciones en background
  
- **#129** - Actualización de modelos en background
  - Actualizar caché automáticamente
  - Notificación de nuevos modelos disponibles
  
- **#130** - Forzar actualización de modelos
  - Pull-to-refresh
  - Reemplazar caché con datos actualizados

---

### HU-026: Paginación de mensajes (3 issues) - 5 puntos
**Prioridad**: Media  
**Descripción**: Cargar mensajes de forma progresiva en conversaciones largas

- **#131** - Cargar conversación con muchos mensajes
  - Cargar solo últimos 50 mensajes inicialmente
  - Carga instantánea
  - Scroll en último mensaje
  
- **#132** - Cargar mensajes antiguos al hacer scroll
  - Carga automática de 30 mensajes anteriores
  - Indicador de carga
  - Ajuste correcto de scroll
  
- **#133** - Búsqueda en conversación larga
  - Buscar en todos los mensajes (no solo cargados)
  - Mostrar todos los resultados
  - Saltar a mensaje encontrado

---

## 🔒 ÉPICA 8: Seguridad y Privacidad (9 issues)

### HU-027: Almacenamiento seguro de API Key (4 issues) - 5 puntos
**Prioridad**: Alta  
**Descripción**: Proteger la API key del usuario

- **#134** - API Key encriptada en reposo
  - Usar Android Keystore
  - Encriptación AES-256
  - No accesible fuera de la app
  
- **#135** - API Key no visible en logs
  - Nunca mostrar API key en logs
  - Ofuscar peticiones HTTP en logs
  
- **#136** - Protección contra capturas de pantalla
  - Bloquear capturas cuando se muestra API key
  - Mensaje de seguridad
  
- **#137** - Timeout de sesión (opcional)
  - Timeout configurable
  - Solicitar autenticación biométrica/PIN después de inactividad

---

### HU-028: Encriptación de conversaciones sensibles (5 issues) - 8 puntos
**Prioridad**: Baja  
**Descripción**: Encriptar conversaciones específicas con PIN

- **#138** - Marcar conversación como privada
  - Crear PIN de 6 dígitos
  - Encriptar conversación
  - Ícono de candado en lista
  
- **#139** - Acceder a conversación encriptada
  - Solicitar PIN
  - Desencriptar con PIN correcto
  - Acceso temporal (5 minutos)
  
- **#140** - PIN incorrecto
  - Mensaje de error
  - Bloqueo después de 3 intentos (30 segundos)
  
- **#141** - Olvidé mi PIN
  - Advertencia de pérdida de datos
  - Opción de restablecer eliminando conversaciones encriptadas

---

## 📅 Recomendaciones de Implementación

### Sprint 1 (Prioridad Alta) - 13 puntos
**Focus**: Seguridad fundamental
- [ ] #134 - API Key encriptada en reposo
- [ ] #135 - API Key no visible en logs
- [ ] #136 - Protección contra capturas de pantalla
- [ ] #137 - Timeout de sesión (opcional)

### Sprint 2 (Prioridad Media) - 16 puntos
**Focus**: Rendimiento y UX
- [ ] #127-130 - HU-025: Caché de modelos (3 pts)
- [ ] #131-133 - HU-026: Paginación de mensajes (5 pts)
- [ ] #112-115 - HU-021: Tokens y costos (5 pts)
- [ ] #116-119 - HU-022: Modo offline (3 pts)

### Sprint 3 (Prioridad Baja) - 14 puntos
**Focus**: Features nice-to-have
- [ ] #107-111 - HU-020: Comparación de modelos (8 pts)
- [ ] #123-126 - HU-024: Sistema de favoritos (3 pts)
- [ ] #120-122 - HU-023: Compartir conversación (3 pts)

### Sprint 4 (Prioridad Baja) - 8 puntos
**Focus**: Privacidad avanzada
- [ ] #138-141 - HU-028: Encriptación de conversaciones (8 pts)

---

## 🔄 Comandos Útiles de GitHub CLI

### Ver todas las issues abiertas
```bash
gh issue list --state open
```

### Ver issues por label
```bash
gh issue list --label "enhancement" --state open
gh issue list --label "high-priority" --state open
```

### Crear nueva issue
```bash
gh issue create --title "Título" --body "Descripción"
```

### Cerrar issue
```bash
gh issue close 107
```

### Ver detalles de una issue
```bash
gh issue view 107
```

### Asignar issue
```bash
gh issue edit 107 --add-assignee @me
```

### Agregar milestone
```bash
gh issue edit 107 --milestone "Sprint 1"
```

---

## 📝 Notas

- **Total de puntos pendientes**: 64 puntos
- **Sprints estimados**: 4 sprints de ~16 puntos cada uno
- **Duración estimada**: 8 semanas (4 sprints de 2 semanas)
- **Checkpoint actual**: v1.0-stable (commit 78fdb18)

### Dependencias Importantes

1. **HU-027** (Seguridad API Key) debe implementarse **ANTES** de cualquier feature que maneje datos sensibles
2. **HU-025** (Caché de modelos) mejorará significativamente el UX de otras features
3. **HU-026** (Paginación) es crítico antes de implementar HU-020 (Comparación) para evitar problemas de rendimiento

### Issues Bloqueadas

Ninguna issue está bloqueada actualmente. Todas pueden implementarse en paralelo, aunque se recomienda seguir el orden de prioridad.

---

**¿Necesitas ayuda con alguna issue específica?**  
Usa: `gh issue view <número>` para ver detalles completos.

