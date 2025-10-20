# 🔍 Instrucciones de Debug - Crash al Crear Conversación

## 📋 Problema
La app crashea cuando se intenta crear una nueva conversación.

## 🎯 Objetivo
Identificar EXACTAMENTE dónde ocurre el crash mediante logging detallado.

## 🚀 Pasos para Debug

### 1. Abrir el Proyecto en Android Studio
```bash
open -a "Android Studio" /Users/luiso/AndroidStudioProjects/ChatAi
```

### 2. Configurar Logcat
1. Ve a View → Tool Windows → Logcat
2. En el filtro de Logcat, ingresa:
   ```
   tag:ConversationRepo | tag:ConversationCreationVM | tag:Navigation3Manager | tag:ChatViewModel
   ```
3. Esto filtrará solo los logs relevantes

### 3. Ejecutar la App
1. Conecta un dispositivo físico o inicia un emulador
2. Click en el botón "Run" (▶️) o presiona `Shift+F10`
3. Espera a que la app se instale y se inicie

### 4. Reproducir el Crash
1. Cuando la app se abra, navega a la pantalla principal (lista de conversaciones)
2. Presiona el botón flotante de "+" para crear una nueva conversación
3. **LA APP DEBE CRASHEAR AQUÍ**

### 5. Analizar los Logs

#### Secuencia ESPERADA de logs (si todo funciona bien):
```
D/ConversationCreationVM: createConversation() called
D/ConversationCreationVM: State set to loading
D/ConversationRepo: Creating conversation: title=Nueva conversación, model=gpt-3.5-turbo
D/ConversationRepo: Created conversation with ID: xxx-xxx-xxx
D/ConversationRepo: Conversation added to list. Total conversations: 1
D/ConversationCreationVM: Use case returned: Success(...)
D/ConversationCreationVM: Success! Conversation ID: xxx-xxx-xxx
D/ConversationCreationVM: State updated with conversation ID
D/Navigation3Manager: Conversation created, navigating to chat with ID: xxx-xxx-xxx
D/Navigation3Manager: Backstack updated, clearing state
D/ChatViewModel: loadConversationHistory called with ID: xxx-xxx-xxx
D/ChatViewModel: State set to loading
D/ChatViewModel: Calling getConversationByIdUseCase...
D/ConversationRepo: Getting conversation by ID: xxx-xxx-xxx
D/ConversationRepo: Found conversation: true, Total in list: 1
D/ConversationRepo: Conversation title: Nueva conversación
D/ChatViewModel: Got conversation: Nueva conversación
D/ChatViewModel: Conversation title: Nueva conversación
D/ChatViewModel: Loading messages...
D/ChatViewModel: Got 0 messages
D/ChatViewModel: State updated successfully
```

#### ¿QUÉ BUSCAR?
1. **¿En qué punto se detienen los logs?**
   - Si se detiene después de "createConversation() called" → Error en el ViewModel
   - Si se detiene después de "Conversation added to list" → Error en el Use Case
   - Si se detiene después de "navigating to chat" → Error en la navegación
   - Si se detiene después de "loadConversationHistory called" → Error en ChatViewModel

2. **¿Hay algún log de ERROR (E/)?**
   - Los logs de error aparecerán en rojo/naranja en Logcat
   - Busca específicamente:
     ```
     E/ConversationCreationVM: Exception creating conversation
     E/ChatViewModel: Error getting conversation
     ```

3. **¿Hay alguna excepción o stack trace?**
   - Después del crash, Logcat mostrará el stack trace completo
   - Copia TODO el stack trace

### 6. Reportar Resultados

Por favor, copia y pega:

#### A. Los últimos logs ANTES del crash:
```
[PEGAR AQUÍ LOS LOGS]
```

#### B. El stack trace completo del crash:
```
[PEGAR AQUÍ EL STACK TRACE]
```

#### C. El último mensaje que apareció en los logs:
```
Último log visible: [PEGAR AQUÍ]
```

## 🔧 Posibles Causas (basado en dónde falle)

### Si falla en `createConversation()`:
- Problema con el `CreateConversationUseCase`
- Problema al obtener el default model
- Problema al acceder al string resource

### Si falla en `getConversationById()`:
- **ESTE ES EL MÁS PROBABLE**
- Problema con el Flow que no emite
- Problema con `.first()` bloqueándose

### Si falla en `loadConversationHistory()`:
- Problema con `GetMessagesSyncUseCase`
- Problema con el ViewModel scope

### Si falla en la navegación:
- Problema con el backStack
- Problema al crear el ChatScreen

## 📱 Información Adicional Útil

### Ver todos los logs (sin filtro):
Si necesitas ver TODOS los logs para contexto adicional, quita el filtro de Logcat.

### Ver logs del sistema:
Para ver si hay problemas de memoria o recursos:
```
tag:System.err | tag:AndroidRuntime | tag:dalvikvm
```

### Comandos útiles de ADB:
```bash
# Ver logs en tiempo real desde terminal
adb logcat -s ConversationRepo:D ConversationCreationVM:D Navigation3Manager:D ChatViewModel:D

# Limpiar logs
adb logcat -c

# Guardar logs a archivo
adb logcat > crash_logs.txt
```

## ✅ Una vez que tengas los logs

Compártelos conmigo y podré identificar EXACTAMENTE dónde está el problema y crear una solución definitiva.

---

**NOTA**: Si la app NO crashea con estos cambios, ¡GENIAL! Significa que la solución de usar `flowOf()` funcionó correctamente.

