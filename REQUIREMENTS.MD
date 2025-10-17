# Historias de Usuario y Criterios de Aceptación
**App Chat IA Multimódelo - Android**

---

## ÉPICA 1: Onboarding y Configuración Inicial

### HU-001: Primer uso de la aplicación

**Como** usuario nuevo  
**Quiero** ver una pantalla de bienvenida con instrucciones  
**Para** entender cómo funciona la aplicación

**Prioridad:** Alta  
**Estimación:** 3 puntos

```gherkin
Feature: Onboarding inicial

  Scenario: Usuario abre la app por primera vez
    Given que la aplicación no tiene una API key configurada
    When abro la aplicación por primera vez
    Then veo la pantalla de onboarding
    And veo el logo de la aplicación
    And veo un mensaje de bienvenida
    And veo un botón "Comenzar"

  Scenario: Completar onboarding
    Given que estoy en la pantalla de onboarding
    When presiono el botón "Comenzar"
    Then soy redirigido a la pantalla de configuración de API key
```

---

### HU-002: Configurar API Key de OpenRouter

**Como** usuario  
**Quiero** ingresar mi API key de OpenRouter  
**Para** poder usar la aplicación y chatear con los modelos de IA

**Prioridad:** Alta  
**Estimación:** 5 puntos

```gherkin
Feature: Configuración de API Key

  Scenario: Ingresar API key válida
    Given que estoy en la pantalla de configuración de API key
    And no tengo una API key guardada
    When ingreso una API key válida "sk-or-v1-xxxxx"
    And presiono el botón "Guardar"
    Then la API key se guarda de forma segura en el dispositivo
    And veo un mensaje de confirmación "API key configurada correctamente"
    And soy redirigido a la lista de conversaciones

  Scenario: Ingresar API key inválida
    Given que estoy en la pantalla de configuración de API key
    When ingreso una API key con formato inválido "invalid-key"
    And presiono el botón "Guardar"
    Then veo un mensaje de error "Formato de API key inválido"
    And permanezco en la pantalla de configuración

  Scenario: Validar API key con OpenRouter
    Given que estoy en la pantalla de configuración de API key
    When ingreso una API key válida
    And presiono el botón "Validar"
    Then la app hace una petición a OpenRouter para validar la key
    And veo un indicador de carga
    And si la key es válida, veo "API key válida ✓"
    And si la key es inválida, veo "API key rechazada por OpenRouter"

  Scenario: Campo de API key vacío
    Given que estoy en la pantalla de configuración de API key
    When el campo de API key está vacío
    And presiono el botón "Guardar"
    Then veo un mensaje de error "Por favor ingresa tu API key"
    And el botón "Guardar" permanece deshabilitado

  Scenario: Obtener información sobre dónde conseguir API key
    Given que estoy en la pantalla de configuración de API key
    When presiono el enlace "¿Dónde obtengo mi API key?"
    Then se abre el navegador en "https://openrouter.ai/keys"
```

---

## ÉPICA 2: Gestión de Conversaciones

### HU-003: Ver lista de conversaciones

**Como** usuario  
**Quiero** ver todas mis conversaciones previas  
**Para** acceder rápidamente a mis chats anteriores

**Prioridad:** Alta  
**Estimación:** 5 puntos

```gherkin
Feature: Lista de conversaciones

  Background:
    Given que tengo una API key configurada
    And estoy en la pantalla de lista de conversaciones

  Scenario: Ver lista vacía de conversaciones
    Given que no tengo conversaciones guardadas
    When accedo a la pantalla de lista de conversaciones
    Then veo un mensaje "No tienes conversaciones"
    And veo un botón flotante "+" para crear nueva conversación
    And veo una ilustración de estado vacío

  Scenario: Ver lista con conversaciones existentes
    Given que tengo 5 conversaciones guardadas
    When accedo a la pantalla de lista de conversaciones
    Then veo una lista con 5 conversaciones
    And cada conversación muestra su título
    And cada conversación muestra el modelo usado
    And cada conversación muestra el preview del último mensaje
    And cada conversación muestra la fecha/hora de la última actividad
    And las conversaciones están ordenadas por fecha descendente

  Scenario: Visualizar diferentes tipos de modelos en la lista
    Given que tengo conversaciones con diferentes modelos:
      | título           | modelo           |
      | Chat con GPT-4   | gpt-4            |
      | Pregunta Claude  | claude-3-opus    |
      | Test Llama       | llama-3-70b      |
    When accedo a la lista de conversaciones
    Then cada conversación muestra un ícono distintivo según el modelo
    And cada conversación muestra el nombre del modelo usado

  Scenario: Ver conversaciones archivadas
    Given que tengo 3 conversaciones archivadas
    When accedo al menú de opciones
    And selecciono "Ver archivadas"
    Then veo solo las conversaciones archivadas
    And puedo restaurar una conversación archivada
```

---

### HU-004: Crear nueva conversación

**Como** usuario  
**Quiero** crear una nueva conversación  
**Para** comenzar a chatear con un modelo de IA

**Prioridad:** Alta  
**Estimación:** 3 puntos

```gherkin
Feature: Crear nueva conversación

  Background:
    Given que estoy en la pantalla de lista de conversaciones
    And tengo una API key configurada

  Scenario: Crear conversación con modelo por defecto
    When presiono el botón flotante "+"
    Then se crea una nueva conversación
    And la conversación usa el modelo por defecto configurado
    And soy redirigido a la pantalla de chat
    And veo un campo de entrada de texto vacío
    And veo el nombre del modelo seleccionado en la parte superior

  Scenario: Crear conversación seleccionando modelo
    When presiono el botón flotante "+"
    And se muestra un diálogo para seleccionar modelo
    And selecciono "claude-3-opus-20240229"
    And presiono "Crear"
    Then se crea una nueva conversación con el modelo seleccionado
    And soy redirigido a la pantalla de chat
    And el modelo "Claude 3 Opus" está seleccionado

  Scenario: Cancelar creación de conversación
    When presiono el botón flotante "+"
    And se muestra el diálogo de selección de modelo
    And presiono "Cancelar"
    Then el diálogo se cierra
    And permanezco en la lista de conversaciones
    And no se crea ninguna conversación nueva
```

---

### HU-005: Eliminar conversación

**Como** usuario  
**Quiero** eliminar conversaciones que ya no necesito  
**Para** mantener mi lista organizada

**Prioridad:** Media  
**Estimación:** 3 puntos

```gherkin
Feature: Eliminar conversación

  Background:
    Given que estoy en la lista de conversaciones
    And tengo al menos una conversación guardada

  Scenario: Eliminar conversación con swipe
    When deslizo una conversación hacia la izquierda
    Then aparece un botón "Eliminar" de color rojo
    When presiono el botón "Eliminar"
    Then se muestra un diálogo de confirmación "¿Eliminar esta conversación?"
    When confirmo la eliminación
    Then la conversación se elimina de la lista
    And veo un mensaje "Conversación eliminada"
    And se muestra un botón "Deshacer" durante 5 segundos

  Scenario: Deshacer eliminación de conversación
    Given que acabo de eliminar una conversación
    And veo el mensaje "Conversación eliminada" con botón "Deshacer"
    When presiono "Deshacer" antes de 5 segundos
    Then la conversación se restaura en la lista
    And veo un mensaje "Conversación restaurada"

  Scenario: Cancelar eliminación
    When deslizo una conversación hacia la izquierda
    And presiono el botón "Eliminar"
    And aparece el diálogo de confirmación
    When presiono "Cancelar"
    Then el diálogo se cierra
    And la conversación permanece en la lista

  Scenario: Eliminar conversación desde menú contextual
    When mantengo presionada una conversación
    Then aparece un menú contextual con opciones
    When selecciono "Eliminar"
    Then se muestra el diálogo de confirmación
    When confirmo la eliminación
    Then la conversación se elimina permanentemente
```

---

### HU-006: Archivar conversación

**Como** usuario  
**Quiero** archivar conversaciones  
**Para** ocultarlas de mi lista principal sin eliminarlas

**Prioridad:** Baja  
**Estimación:** 3 puntos

```gherkin
Feature: Archivar conversación

  Background:
    Given que estoy en la lista de conversaciones
    And tengo conversaciones activas

  Scenario: Archivar conversación con swipe
    When deslizo una conversación hacia la derecha
    Then aparece un botón "Archivar"
    When presiono el botón "Archivar"
    Then la conversación desaparece de la lista principal
    And veo un mensaje "Conversación archivada"
    And la conversación se mueve a la sección de archivadas

  Scenario: Restaurar conversación archivada
    Given que tengo conversaciones archivadas
    When accedo a la sección "Archivadas"
    And deslizo una conversación hacia la derecha
    And presiono "Restaurar"
    Then la conversación vuelve a la lista principal
    And veo un mensaje "Conversación restaurada"
```

---

### HU-007: Buscar conversaciones

**Como** usuario  
**Quiero** buscar conversaciones por título o contenido  
**Para** encontrar rápidamente conversaciones específicas

**Prioridad:** Media  
**Estimación:** 5 puntos

```gherkin
Feature: Búsqueda de conversaciones

  Background:
    Given que estoy en la lista de conversaciones
    And tengo múltiples conversaciones guardadas

  Scenario: Buscar conversación por título
    When presiono el ícono de búsqueda
    Then aparece un campo de búsqueda en la parte superior
    When escribo "proyecto kotlin" en el campo de búsqueda
    Then la lista se filtra mostrando solo conversaciones que contienen "proyecto kotlin" en el título
    And veo el número de resultados "3 conversaciones encontradas"

  Scenario: Buscar conversación por contenido de mensajes
    When presiono el ícono de búsqueda
    And escribo "clean architecture"
    Then la lista muestra conversaciones que contienen "clean architecture" en sus mensajes
    And cada resultado resalta el texto encontrado

  Scenario: Búsqueda sin resultados
    When presiono el ícono de búsqueda
    And escribo "xyz123notfound"
    Then veo un mensaje "No se encontraron conversaciones"
    And veo una sugerencia "Intenta con otros términos de búsqueda"

  Scenario: Limpiar búsqueda
    Given que he realizado una búsqueda con resultados
    When presiono el botón "X" en el campo de búsqueda
    Then el texto de búsqueda se borra
    And se muestra nuevamente la lista completa de conversaciones
```

---

## ÉPICA 3: Chat y Mensajería

### HU-008: Enviar mensaje a IA

**Como** usuario  
**Quiero** escribir y enviar mensajes  
**Para** interactuar con el modelo de IA seleccionado

**Prioridad:** Alta  
**Estimación:** 8 puntos

```gherkin
Feature: Enviar mensaje

  Background:
    Given que estoy en una conversación activa
    And tengo conexión a internet
    And mi API key es válida

  Scenario: Enviar mensaje de texto simple
    Given que el campo de entrada está habilitado
    When escribo "Hola, ¿cómo estás?"
    And presiono el botón "Enviar"
    Then el mensaje aparece en el chat como mensaje del usuario
    And el mensaje se guarda en la base de datos local
    And se muestra un indicador de "escribiendo..."
    And se envía la petición a OpenRouter API
    And espero la respuesta del modelo

  Scenario: Recibir respuesta del modelo
    Given que he enviado un mensaje
    And el modelo está procesando la respuesta
    When recibo la respuesta del modelo
    Then el indicador de "escribiendo..." desaparece
    And la respuesta aparece en el chat como mensaje del asistente
    And el mensaje se guarda en la base de datos local
    And el scroll se desplaza automáticamente al último mensaje
    And se muestra el número de tokens usados (opcional)

  Scenario: Error al enviar mensaje sin conexión
    Given que no tengo conexión a internet
    When intento enviar un mensaje
    Then veo un mensaje de error "Sin conexión a internet"
    And el mensaje se marca como "pendiente de envío"
    And el mensaje permanece en el campo de entrada

  Scenario: Error de API key inválida
    Given que mi API key ha expirado o es inválida
    When envío un mensaje
    Then recibo un error de la API
    And veo un mensaje "Error de autenticación. Verifica tu API key"
    And se me ofrece la opción de "Configurar API key"

  Scenario: Mensaje vacío
    Given que el campo de entrada está vacío
    Then el botón "Enviar" está deshabilitado
    When intento presionar "Enviar"
    Then no se envía ningún mensaje

  Scenario: Mensaje demasiado largo
    Given que he escrito un mensaje de más de 10,000 caracteres
    When intento enviar el mensaje
    Then veo una advertencia "El mensaje es muy largo (máximo 10,000 caracteres)"
    And el botón "Enviar" está deshabilitado
```

---

### HU-009: Recibir respuesta con streaming

**Como** usuario  
**Quiero** ver la respuesta del modelo en tiempo real mientras se genera  
**Para** tener una experiencia más interactiva y fluida

**Prioridad:** Alta  
**Estimación:** 8 puntos

```gherkin
Feature: Streaming de respuestas

  Background:
    Given que estoy en una conversación activa
    And el streaming está habilitado
    And he enviado un mensaje

  Scenario: Recibir respuesta con streaming exitoso
    Given que el modelo está generando una respuesta
    When empiezan a llegar los chunks de texto
    Then veo el texto aparecer palabra por palabra en tiempo real
    And el cursor o indicador muestra que se está escribiendo
    And el scroll se mantiene en el último mensaje automáticamente
    When la respuesta termina
    Then el indicador de escritura desaparece
    And el mensaje completo se guarda en la base de datos

  Scenario: Cancelar streaming en progreso
    Given que el modelo está generando una respuesta en streaming
    And veo el texto aparecer en tiempo real
    When presiono el botón "Detener"
    Then el streaming se cancela inmediatamente
    And se guarda solo el texto generado hasta ese momento
    And veo un indicador "Respuesta interrumpida"

  Scenario: Error durante streaming
    Given que el modelo está generando una respuesta
    When ocurre un error de conexión durante el streaming
    Then el streaming se detiene
    And veo un mensaje de error "Error de conexión"
    And se guarda el texto generado hasta el momento del error
    And se muestra un botón "Reintentar"

  Scenario: Reconexión automática durante streaming
    Given que estoy recibiendo una respuesta en streaming
    When pierdo la conexión temporalmente
    And la conexión se restablece en menos de 3 segundos
    Then el streaming continúa automáticamente
    And no veo interrupciones en el texto

  Scenario: Streaming con formato markdown
    Given que el modelo está generando una respuesta con formato
    When recibo chunks que contienen markdown (**, `, #, etc.)
    Then el texto se renderiza con el formato correcto en tiempo real
    And el formato se actualiza a medida que llega más texto
```

---

### HU-010: Ver historial de mensajes

**Como** usuario  
**Quiero** ver todos los mensajes anteriores de una conversación  
**Para** revisar el contexto y las respuestas previas

**Prioridad:** Alta  
**Estimación:** 5 puntos

```gherkin
Feature: Historial de mensajes

  Background:
    Given que estoy en una conversación con mensajes previos

  Scenario: Cargar historial completo
    When abro una conversación existente
    Then veo todos los mensajes en orden cronológico
    And los mensajes del usuario aparecen alineados a la derecha
    And los mensajes del asistente aparecen alineados a la izquierda
    And cada mensaje muestra la hora de envío
    And el scroll está posicionado en el último mensaje

  Scenario: Scroll automático a nuevo mensaje
    Given que estoy viendo mensajes antiguos en el medio del chat
    When recibo una nueva respuesta del asistente
    Then el scroll se desplaza automáticamente al último mensaje
    And el nuevo mensaje es visible inmediatamente

  Scenario: Scroll manual en historial
    Given que estoy en el último mensaje
    When hago scroll hacia arriba
    Then puedo ver mensajes antiguos
    And el auto-scroll se desactiva temporalmente
    When llega un nuevo mensaje
    Then veo una notificación "Nuevo mensaje ↓"
    When presiono la notificación
    Then el scroll vuelve al último mensaje

  Scenario: Cargar mensajes antiguos (paginación)
    Given que tengo una conversación con más de 50 mensajes
    When hago scroll hasta el principio del chat
    Then se cargan automáticamente los 20 mensajes anteriores
    And veo un indicador de carga
    And mi posición de scroll se mantiene

  Scenario: Copiar mensaje
    Given que veo un mensaje del asistente
    When mantengo presionado el mensaje
    Then aparece un menú contextual
    When selecciono "Copiar"
    Then el texto del mensaje se copia al portapapeles
    And veo una confirmación "Texto copiado"
```

---

### HU-011: Eliminar mensaje individual

**Como** usuario  
**Quiero** eliminar mensajes específicos  
**Para** corregir errores o limpiar mensajes no deseados

**Prioridad:** Baja  
**Estimación:** 3 puntos

```gherkin
Feature: Eliminar mensaje

  Background:
    Given que estoy en una conversación con mensajes

  Scenario: Eliminar mensaje propio
    Given que veo un mensaje que yo envié
    When mantengo presionado el mensaje
    Then aparece un menú contextual con la opción "Eliminar"
    When selecciono "Eliminar"
    Then aparece un diálogo "¿Eliminar este mensaje?"
    When confirmo
    Then el mensaje se elimina del chat
    And se elimina de la base de datos local
    And veo el mensaje "Mensaje eliminado"

  Scenario: Eliminar mensaje del asistente
    Given que veo un mensaje del asistente
    When mantengo presionado el mensaje
    And selecciono "Eliminar"
    And confirmo la eliminación
    Then el mensaje se elimina del chat
    And el contexto de la conversación se actualiza

  Scenario: Cancelar eliminación de mensaje
    Given que he seleccionado eliminar un mensaje
    When aparece el diálogo de confirmación
    And presiono "Cancelar"
    Then el diálogo se cierra
    And el mensaje permanece en el chat
```

---

## ÉPICA 4: Gestión de Modelos

### HU-012: Ver modelos disponibles

**Como** usuario  
**Quiero** ver una lista de todos los modelos de IA disponibles  
**Para** conocer mis opciones y seleccionar el más adecuado

**Prioridad:** Alta  
**Estimación:** 5 puntos

```gherkin
Feature: Lista de modelos disponibles

  Background:
    Given que tengo una API key válida configurada
    And tengo conexión a internet

  Scenario: Cargar lista de modelos desde OpenRouter
    When accedo a la pantalla de selección de modelos
    Then veo un indicador de carga
    And la app consulta la API de OpenRouter
    When los modelos se cargan exitosamente
    Then veo una lista de todos los modelos disponibles
    And cada modelo muestra:
      | Campo          | Ejemplo                    |
      | Nombre         | GPT-4 Turbo                |
      | ID             | openai/gpt-4-turbo         |
      | Compañía       | OpenAI                     |
      | Contexto       | 128k tokens                |
      | Precio         | $0.01 / 1K tokens          |

  Scenario: Filtrar modelos por compañía
    Given que estoy viendo la lista de modelos
    When selecciono el filtro "Compañía"
    And selecciono "Anthropic"
    Then solo veo modelos de Anthropic (Claude)
    And veo el contador "5 modelos encontrados"

  Scenario: Buscar modelo por nombre
    Given que estoy viendo la lista de modelos
    When escribo "llama" en el campo de búsqueda
    Then solo veo modelos que contienen "llama" en su nombre
    And los resultados resaltan el término buscado

  Scenario: Ordenar modelos por precio
    Given que estoy viendo la lista de modelos
    When selecciono "Ordenar por precio"
    Then los modelos se ordenan de menor a mayor precio
    And veo primero los modelos más económicos

  Scenario: Ver detalles de un modelo
    Given que estoy viendo la lista de modelos
    When presiono sobre un modelo específico
    Then veo una pantalla de detalles con:
      | Información                  |
      | Nombre completo              |
      | Descripción                  |
      | Capacidades (contexto)       |
      | Precio de input/output       |
      | Límites de rate              |
      | Link a documentación         |

  Scenario: Error al cargar modelos
    Given que no tengo conexión a internet
    When intento acceder a la lista de modelos
    Then veo un mensaje "No se pudieron cargar los modelos"
    And veo un botón "Reintentar"
    And puedo ver modelos en caché si existen
```

---

### HU-013: Cambiar modelo durante conversación

**Como** usuario  
**Quiero** cambiar de modelo de IA en medio de una conversación  
**Para** comparar respuestas o usar capacidades específicas

**Prioridad:** Media  
**Estimación:** 5 puntos

```gherkin
Feature: Cambiar modelo en conversación

  Background:
    Given que estoy en una conversación activa
    And el modelo actual es "gpt-3.5-turbo"

  Scenario: Cambiar modelo exitosamente
    When presiono el selector de modelo en la parte superior
    Then veo una lista de modelos disponibles
    When selecciono "claude-3-opus-20240229"
    And confirmo el cambio
    Then el modelo activo cambia a "Claude 3 Opus"
    And veo una notificación "Modelo cambiado a Claude 3 Opus"
    And los mensajes previos mantienen el modelo con que fueron generados
    And los nuevos mensajes usarán el nuevo modelo

  Scenario: Ver indicador de modelo por mensaje
    Given que he cambiado de modelo durante la conversación
    When reviso los mensajes anteriores
    Then cada mensaje del asistente muestra el modelo usado
    And puedo distinguir visualmente respuestas de diferentes modelos

  Scenario: Advertencia de cambio de contexto
    Given que tengo una conversación larga con contexto importante
    When intento cambiar de modelo
    Then veo una advertencia "El nuevo modelo no tendrá el contexto completo"
    And puedo elegir "Continuar" o "Cancelar"

  Scenario: Cambio de modelo con diferentes precios
    Given que estoy usando un modelo económico
    When selecciono un modelo más costoso
    Then veo una advertencia con la diferencia de precio
    And puedo confirmar o cancelar el cambio
```

---

### HU-014: Configurar parámetros del modelo

**Como** usuario avanzado  
**Quiero** ajustar parámetros como temperatura, max_tokens, etc.  
**Para** tener mayor control sobre las respuestas generadas

**Prioridad:** Media  
**Estimación:** 5 puntos

```gherkin
Feature: Configuración de parámetros de modelo

  Background:
    Given que estoy en la pantalla de configuración de modelos
    And he seleccionado un modelo específico

  Scenario: Ajustar temperatura del modelo
    When accedo a los parámetros avanzados
    Then veo un slider para "Temperature" con rango 0.0 a 2.0
    And el valor por defecto es 0.7
    When ajusto el slider a 1.2
    And presiono "Guardar"
    Then el valor se guarda para ese modelo
    And los futuros mensajes usarán temperatura 1.2

  Scenario: Configurar tokens máximos
    When veo el campo "Max Tokens"
    And el valor actual es 2000
    When cambio el valor a 4000
    And presiono "Guardar"
    Then las respuestas del modelo tendrán límite de 4000 tokens

  Scenario: Restablecer valores por defecto
    Given que he modificado varios parámetros
    When presiono "Restablecer valores por defecto"
    Then todos los parámetros vuelven a sus valores originales:
      | Parámetro          | Valor por defecto |
      | temperature        | 0.7               |
      | max_tokens         | 2000              |
      | top_p              | 1.0               |
      | frequency_penalty  | 0.0               |
      | presence_penalty   | 0.0               |

  Scenario: Configuración independiente por modelo
    Given que configuro temperatura en 1.0 para "gpt-4"
    When cambio a modelo "claude-3-opus"
    Then veo que Claude tiene su propia configuración independiente
    And la temperatura de GPT-4 no afecta a Claude

  Scenario: Ver descripción de parámetros
    When presiono el ícono de información junto a "Temperature"
    Then veo una explicación "Controla la aleatoriedad. Valores más altos = más creativo"
    And veo ejemplos de uso recomendado
```

---

## ÉPICA 5: Configuración y Preferencias

### HU-015: Gestionar API Key

**Como** usuario  
**Quiero** ver, actualizar o eliminar mi API key  
**Para** mantener mi configuración segura y actualizada

**Prioridad:** Alta  
**Estimación:** 3 puntos

```gherkin
Feature: Gestión de API Key

  Background:
    Given que tengo una API key configurada
    And estoy en la pantalla de configuración

  Scenario: Ver API key ofuscada
    When accedo a la sección "API Key"
    Then veo la key parcialmente oculta "sk-or-v1-••••••••••xxxx"
    And veo un botón "Mostrar"
    And veo un botón "Editar"
    And veo un botón "Eliminar"

  Scenario: Mostrar API key completa
    Given que veo la API key ofuscada
    When presiono "Mostrar"
    Then veo la API key completa durante 5 segundos
    And después vuelve a ofuscarse automáticamente

  Scenario: Actualizar API key
    When presiono "Editar"
    Then veo un campo de texto con la key actual
    When ingreso una nueva API key válida
    And presiono "Guardar"
    Then la nueva key se guarda de forma segura
    And veo confirmación "API key actualizada correctamente"

  Scenario: Eliminar API key
    When presiono "Eliminar"
    Then veo un diálogo de confirmación "¿Eliminar API key?"
    And veo advertencia "No podrás usar la app sin API key"
    When confirmo la eliminación
    Then la API key se elimina del dispositivo
    And soy redirigido a la pantalla de configuración inicial

  Scenario: Validar nueva API key antes de guardar
    Given que estoy editando mi API key
    When ingreso una nueva key
    And presiono "Validar"
    Then la app verifica la key con OpenRouter
    And veo el resultado de la validación
```

---

### HU-016: Cambiar tema de la aplicación

**Como** usuario  
**Quiero** elegir entre tema claro y oscuro  
**Para** adaptar la app a mi preferencia visual y ambiente

**Prioridad:** Media  
**Estimación:** 3 puntos

```gherkin
Feature: Tema de la aplicación

  Background:
    Given que estoy en la pantalla de configuración

  Scenario: Ver opciones de tema
    When accedo a "Apariencia"
    Then veo las opciones:
      | Opción                  |
      | Claro                   |
      | Oscuro                  |
      | Sistema (automático)    |

  Scenario: Cambiar a tema oscuro
    Given que el tema actual es "Claro"
    When selecciono "Oscuro"
    Then la interfaz cambia inmediatamente a colores oscuros
    And el cambio se guarda en las preferencias
    And el tema oscuro persiste al reiniciar la app

  Scenario: Usar tema del sistema
    When selecciono "Sistema (automático)"
    Then el tema sigue la configuración del sistema operativo
    And si cambio el tema del sistema, la app se actualiza automáticamente

  Scenario: Previsualización del tema
    Given que estoy seleccionando un tema
    When selecciono "Oscuro"
    Then veo una previsualización del cambio antes de aplicarlo
    And puedo confirmar o cancelar
```

---

### HU-017: Configurar modelo por defecto

**Como** usuario  
**Quiero** establecer un modelo por defecto  
**Para** que se use automáticamente al crear nuevas conversaciones

**Prioridad:** Media  
**Estimación:** 2 puntos

```gherkin
Feature: Modelo por defecto

  Background:
    Given que estoy en la pantalla de configuración

  Scenario: Establecer modelo por defecto
    When accedo a "Modelo por defecto"
    Then veo la lista de modelos disponibles
    And el modelo actual está marcado
    When selecciono "gpt-4-turbo"
    And presiono "Guardar"
    Then "gpt-4-turbo" se establece como modelo por defecto
    And las nuevas conversaciones usarán este modelo automáticamente

  Scenario: Cambiar modelo por defecto
    Given que tengo "gpt-3.5-turbo" como modelo por defecto
    When cambio el modelo por defecto a "claude-3-opus"
    Then las conversaciones existentes no se ven afectadas
    And solo las nuevas conversaciones usarán Claude 3 Opus

  Scenario: Ver modelo por defecto en creación de conversación
    Given que tengo un modelo por defecto configurado
    When creo una nueva conversación
    Then el modelo por defecto está pre-seleccionado
    And puedo cambiarlo antes de enviar el primer mensaje
```

---

### HU-018: Exportar conversaciones

**Como** usuario  
**Quiero** exportar mis conversaciones  
**Para** guardar un respaldo o compartir el contenido

**Prioridad:** Baja  
**Estimación:** 5 puntos

```gherkin
Feature: Exportar conversaciones

  Background:
    Given que estoy en la pantalla de configuración
    Or estoy viendo una conversación específica

  Scenario: Exportar conversación individual como texto
    Given que estoy en una conversación
    When presiono el menú de opciones
    And selecciono "Exportar conversación"
    Then veo opciones de formato:
      | Formato         |
      | Texto plano     |
      | Markdown        |
      | JSON            |
    When selecciono "Texto plano"
    Then se genera un archivo .txt con todos los mensajes
    And se abre el diálogo de compartir del sistema
    And puedo guardar o compartir el archivo

  Scenario: Exportar conversación como Markdown
    Given que estoy en una conversación
    When exporto como "Markdown"
    Then el archivo mantiene el formato de los mensajes
    And incluye encabezados con fecha y modelo usado
    And los bloques de código se formatean correctamente

  Scenario: Exportar conversación como JSON
    When exporto como "JSON"
    Then se genera un archivo con estructura:
      """
      {
        "conversation_id": "uuid",
        "title": "Mi conversación",
        "model": "gpt-4",
        "created_at": "timestamp",
        "messages": [
          {
            "role": "user",
            "content": "mensaje",
            "timestamp": "..."
          }
        ]
      }
      """

  Scenario: Exportar todas las conversaciones
    Given que estoy en configuración
    When selecciono "Exportar todas las conversaciones"
    Then veo un diálogo de confirmación
    When confirmo
    Then se genera un archivo ZIP con todas las conversaciones
    And cada conversación es un archivo individual
    And veo el progreso de la exportación

  Scenario: Error al exportar sin permisos de almacenamiento
    Given que la app no tiene permisos de escritura
    When intento exportar una conversación
    Then veo un mensaje "Se requieren permisos de almacenamiento"
    And se me solicita otorgar los permisos
```

---

### HU-019: Eliminar todos los datos

**Como** usuario  
**Quiero** eliminar todos mis datos de la aplicación  
**Para** empezar de cero o por privacidad

**Prioridad:** Baja  
**Estimación:** 3 puntos

```gherkin
Feature: Eliminar todos los datos

  Background:
    Given que estoy en la pantalla de configuración

  Scenario: Eliminar todos los datos
    When accedo a "Privacidad y datos"
    And selecciono "Eliminar todos los datos"
    Then veo una advertencia seria:
      """
      ⚠️ Esta acción eliminará permanentemente:
      - Todas las conversaciones
      - Todos los mensajes
      - Configuraciones de modelos
      - API Key
      
      Esta acción NO se puede deshacer.
      """
    When escribo "ELIMINAR" para confirmar
    And presiono "Confirmar eliminación"
    Then se eliminan todas las conversaciones
    And se elimina la API key
    And se restablecen todas las configuraciones
    And soy redirigido a la pantalla de onboarding

  Scenario: Cancelar eliminación de datos
    Given que he iniciado el proceso de eliminación
    When veo el diálogo de confirmación
    And presiono "Cancelar"
    Then el diálogo se cierra
    And no se elimina ningún dato

  Scenario: Palabra de confirmación incorrecta
    Given que estoy en el diálogo de eliminar datos
    When escribo "eliminar" en lugar de "ELIMINAR"
    Then el botón de confirmación permanece deshabilitado
    And veo un mensaje "Escribe ELIMINAR (en mayúsculas) para confirmar"
```

---

## ÉPICA 6: Funcionalidades Avanzadas

### HU-020: Comparar respuestas de múltiples modelos

**Como** usuario  
**Quiero** enviar el mismo mensaje a varios modelos simultáneamente  
**Para** comparar sus respuestas y elegir la mejor

**Prioridad:** Baja  
**Estimación:** 8 puntos

```gherkin
Feature: Comparación de modelos

  Background:
    Given que estoy en una conversación
    And tengo una API key válida

  Scenario: Activar modo comparación
    When presiono el menú de opciones
    And selecciono "Modo comparación"
    Then veo una interfaz para seleccionar múltiples modelos
    And puedo seleccionar hasta 4 modelos
    And veo el costo estimado total

  Scenario: Enviar mensaje a múltiples modelos
    Given que estoy en modo comparación
    And he seleccionado 3 modelos:
      | Modelo             |
      | gpt-4-turbo        |
      | claude-3-opus      |
      | llama-3-70b        |
    When escribo "Explica la relatividad general" y envío
    Then el mensaje se envía a los 3 modelos simultáneamente
    And veo 3 secciones de respuesta, una por modelo
    And cada sección muestra el nombre del modelo
    And cada respuesta se actualiza en streaming

  Scenario: Ver respuestas lado a lado
    Given que he recibido respuestas de múltiples modelos
    Then puedo ver las respuestas en columnas paralelas
    And puedo hacer scroll independiente en cada columna
    And cada respuesta muestra:
      | Información          |
      | Modelo usado         |
      | Tiempo de respuesta  |
      | Tokens utilizados    |
      | Costo aproximado     |

  Scenario: Seleccionar mejor respuesta
    Given que veo respuestas de múltiples modelos
    When presiono "⭐ Marcar como mejor" en una respuesta
    Then esa respuesta se guarda como la principal en el historial
    And las otras respuestas se guardan como alternativas
    And puedo ver las alternativas cuando quiera

  Scenario: Desactivar modo comparación
    Given que estoy en modo comparación
    When presiono "Salir de modo comparación"
    Then vuelvo al modo normal de chat
    And se usa solo el modelo por defecto
```

---

### HU-021: Ver uso de tokens y costos

**Como** usuario  
**Quiero** ver cuántos tokens he usado y el costo aproximado  
**Para** controlar mi gasto en la API

**Prioridad:** Media  
**Estimación:** 5 puntos

```gherkin
Feature: Seguimiento de tokens y costos

  Background:
    Given que tengo conversaciones activas

  Scenario: Ver tokens por mensaje
    Given que estoy viendo un mensaje del asistente
    When el mensaje se ha generado completamente
    Then veo un indicador pequeño con los tokens usados
    And muestra: "~250 tokens"
    When presiono el indicador
    Then veo el desglose:
      | Campo           | Valor    |
      | Input tokens    | 50       |
      | Output tokens   | 200      |
      | Total tokens    | 250      |
      | Costo aprox.    | $0.005   |

  Scenario: Ver estadísticas de conversación
    Given que estoy en una conversación
    When accedo al menú de información
    Then veo estadísticas de la conversación:
      | Métrica                    | Valor         |
      | Total mensajes             | 24            |
      | Total tokens usados        | 15,420        |
      | Costo total aproximado     | $0.45         |
      | Modelo más usado           | gpt-4-turbo   |
      | Tiempo total de chat       | 45 minutos    |

  Scenario: Ver uso total en configuración
    Given que estoy en configuración
    When accedo a "Estadísticas de uso"
    Then veo el uso total de la app:
      | Métrica                    | Valor         |
      | Total conversaciones       | 18            |
      | Total mensajes enviados    | 342           |
      | Total tokens procesados    | 127,550       |
      | Costo total estimado       | $3.24         |
      | Modelo favorito            | gpt-4-turbo   |
    And puedo ver un desglose por modelo
    And puedo ver un gráfico de uso por día

  Scenario: Alerta de alto uso
    Given que he configurado un límite mensual de $10
    When mi uso alcanza el 80% del límite ($8)
    Then recibo una notificación "Has usado $8 de tu límite de $10"
    And se me sugiere revisar mi uso
```

---

### HU-022: Modo offline - Ver conversaciones guardadas

**Como** usuario  
**Quiero** poder leer mis conversaciones sin conexión  
**Para** acceder al historial en cualquier momento

**Prioridad:** Media  
**Estimación:** 3 puntos

```gherkin
Feature: Modo offline

  Scenario: Abrir app sin conexión
    Given que no tengo conexión a internet
    When abro la aplicación
    Then puedo ver todas mis conversaciones guardadas
    And puedo leer todos los mensajes previos
    And veo un indicador "Sin conexión" en la parte superior

  Scenario: Intentar enviar mensaje sin conexión
    Given que estoy sin conexión
    When intento enviar un mensaje
    Then veo un mensaje "Sin conexión a internet"
    And el mensaje no se envía
    And el texto permanece en el campo de entrada
    And puedo guardar el mensaje como borrador

  Scenario: Reconexión automática
    Given que estaba sin conexión
    When la conexión se restablece
    Then veo una notificación "Conexión restablecida"
    And puedo enviar mensajes normalmente
    And si había mensajes en borrador, se muestra un botón para enviarlos

  Scenario: Sincronización al reconectar
    Given que he realizado cambios offline (eliminar conversaciones)
    When recupero la conexión
    Then los cambios se sincronizan automáticamente
    And veo un indicador de sincronización
```

---

### HU-023: Compartir conversación

**Como** usuario  
**Quiero** compartir una conversación o mensaje específico  
**Para** mostrar respuestas interesantes a otros

**Prioridad:** Baja  
**Estimación:** 3 puntos

```gherkin
Feature: Compartir contenido

  Scenario: Compartir mensaje individual
    Given que estoy viendo un mensaje del asistente
    When mantengo presionado el mensaje
    And selecciono "Compartir"
    Then se abre el diálogo de compartir del sistema
    And el texto está formateado correctamente
    And incluye atribución al modelo usado

  Scenario: Compartir conversación completa
    Given que estoy en una conversación
    When presiono el menú de opciones
    And selecciono "Compartir conversación"
    Then puedo elegir el formato de compartir:
      | Formato                           |
      | Texto simple                      |
      | Captura de pantalla               |
      | Link (si hay servidor backend)    |
    When selecciono un formato
    Then se abre el diálogo de compartir

  Scenario: Compartir con captura de pantalla
    Given que he seleccionado "Captura de pantalla"
    Then la app genera una imagen de la conversación
    And la imagen tiene un diseño atractivo
    And incluye el logo de la app (opcional)
    And puedo compartir la imagen
```

---

### HU-024: Sistema de favoritos

**Como** usuario  
**Quiero** marcar conversaciones como favoritas  
**Para** acceder rápidamente a las más importantes

**Prioridad:** Baja  
**Estimación:** 3 puntos

```gherkin
Feature: Favoritos

  Scenario: Marcar conversación como favorita
    Given que estoy viendo la lista de conversaciones
    When presiono el ícono de estrella en una conversación
    Then la conversación se marca como favorita
    And aparece un indicador visual de estrella dorada
    And la conversación permanece marcada

  Scenario: Ver solo conversaciones favoritas
    Given que tengo conversaciones marcadas como favoritas
    When accedo al filtro en la lista de conversaciones
    And selecciono "Favoritas"
    Then solo veo las conversaciones marcadas como favoritas
    And veo el contador "3 favoritas"

  Scenario: Quitar conversación de favoritos
    Given que una conversación está marcada como favorita
    When presiono nuevamente el ícono de estrella
    Then la conversación se quita de favoritos
    And el indicador visual desaparece

  Scenario: Ordenar favoritas primero
    Given que tengo conversaciones favoritas y normales
    When estoy en la lista principal
    Then las conversaciones favoritas aparecen primero
    And luego aparecen las conversaciones normales
    And ambos grupos están ordenados por fecha
```

---

## ÉPICA 7: Rendimiento y Optimización

### HU-025: Caché de modelos disponibles

**Como** usuario  
**Quiero** que los modelos se carguen rápidamente  
**Para** tener una experiencia fluida sin esperas

**Prioridad:** Media  
**Estimación:** 3 puntos

```gherkin
Feature: Caché de modelos

  Scenario: Primera carga de modelos
    Given que es la primera vez que abro la app
    When accedo a la lista de modelos
    Then los modelos se descargan desde OpenRouter
    And se guardan en caché local
    And veo un indicador de carga

  Scenario: Cargar modelos desde caché
    Given que ya he descargado los modelos previamente
    When accedo a la lista de modelos
    Then los modelos se cargan instantáneamente desde caché
    And no veo indicador de carga
    And en segundo plano se verifica si hay actualizaciones

  Scenario: Actualización de modelos en background
    Given que estoy viendo modelos en caché
    When hay nuevos modelos disponibles en OpenRouter
    Then la app actualiza la caché en segundo plano
    And veo una notificación "Nuevos modelos disponibles"
    And puedo actualizar la lista

  Scenario: Forzar actualización de modelos
    Given que estoy viendo la lista de modelos
    When hago pull-to-refresh
    Then se fuerza la descarga de modelos actualizados
    And veo un indicador de actualización
    And la caché se reemplaza con los nuevos datos
```

---

### HU-026: Paginación de mensajes

**Como** usuario con conversaciones largas  
**Quiero** que los mensajes se carguen de forma progresiva  
**Para** que la app sea rápida incluso con muchos mensajes

**Prioridad:** Media  
**Estimación:** 5 puntos

```gherkin
Feature: Paginación de mensajes

  Scenario: Cargar conversación con muchos mensajes
    Given que tengo una conversación con 200 mensajes
    When abro la conversación
    Then se cargan solo los últimos 50 mensajes
    And el scroll está en el último mensaje
    And la carga es instantánea

  Scenario: Cargar mensajes antiguos al hacer scroll
    Given que estoy viendo los mensajes más recientes
    When hago scroll hasta arriba
    And llego al primer mensaje visible
    Then se cargan automáticamente los 30 mensajes anteriores
    And veo un pequeño indicador de carga
    And mi posición de scroll se ajusta correctamente

  Scenario: Búsqueda en conversación larga
    Given que tengo una conversación con 300 mensajes
    When busco un término específico
    Then se busca en todos los mensajes (no solo los cargados)
    And veo todos los resultados
    And puedo saltar a cualquier mensaje encontrado
```

---

## ÉPICA 8: Seguridad y Privacidad

### HU-027: Almacenamiento seguro de API Key

**Como** usuario  
**Quiero** que mi API key esté protegida  
**Para** evitar accesos no autorizados

**Prioridad:** Alta  
**Estimación:** 5 puntos

```gherkin
Feature: Seguridad de API Key

  Scenario: API Key encriptada en reposo
    Given que he guardado mi API key
    Then la key se almacena usando Android Keystore
    And la key está encriptada con AES-256
    And no está accesible desde fuera de la app

  Scenario: API Key no visible en logs
    Given que la app está ejecutándose
    When reviso los logs del sistema
    Then la API key nunca aparece en texto plano
    And las peticiones HTTP están ofuscadas en logs

  Scenario: Protección contra capturas de pantalla
    Given que estoy viendo mi API key completa
    When intento tomar una captura de pantalla
    Then la captura se bloquea o la key aparece oculta
    And veo un mensaje "Captura de pantalla bloqueada por seguridad"

  Scenario: Timeout de sesión (opcional)
    Given que he configurado un timeout de seguridad
    When la app está en background por 10 minutos
    And vuelvo a abrir la app
    Then me solicita confirmar mi identidad (biometría o PIN)
    Before allowing access to conversations
```

---

### HU-028: Encriptación de conversaciones sensibles (opcional)

**Como** usuario preocupado por la privacidad  
**Quiero** encriptar conversaciones específicas  
**Para** proteger información sensible

**Prioridad:** Baja  
**Estimación:** 8 puntos

```gherkin
Feature: Encriptación de conversaciones

  Scenario: Marcar conversación como privada
    Given que estoy viendo una conversación
    When accedo al menú de opciones
    And selecciono "Marcar como privada"
    Then se me solicita crear un PIN de 6 dígitos
    When ingreso y confirmo el PIN
    Then la conversación se encripta
    And aparece un ícono de candado en la lista

  Scenario: Acceder a conversación encriptada
    Given que tengo conversaciones encriptadas
    When intento abrir una conversación con candado
    Then se me solicita el PIN
    When ingreso el PIN correcto
    Then la conversación se desencripta y abre
    And tengo acceso por 5 minutos

  Scenario: PIN incorrecto
    Given que intento abrir una conversación encriptada
    When ingreso un PIN incorrecto
    Then veo un mensaje "PIN incorrecto"
    And después de 3 intentos fallidos
    Then se bloquea por 30 segundos

  Scenario: Olvidé mi PIN
    Given que no recuerdo el PIN de mis conversaciones
    When selecciono "Olvidé mi PIN"
    Then veo una advertencia que las conversaciones se perderán
    And puedo restablecer eliminando las conversaciones encriptadas
```

---

## Resumen de Épicas y Priorización

### Prioridad ALTA (MVP)
1. ✅ **Onboarding y Configuración Inicial** (HU-001, HU-002)
2. ✅ **Gestión Básica de Conversaciones** (HU-003, HU-004, HU-005)
3. ✅ **Chat y Mensajería Core** (HU-008, HU-009, HU-010)
4. ✅ **Modelos Disponibles** (HU-012)
5. ✅ **Gestión de API Key** (HU-015)
6. ✅ **Seguridad API Key** (HU-027)

### Prioridad MEDIA (Post-MVP)
7. ✅ **Búsqueda y Archivo** (HU-006, HU-007)
8. ✅ **Gestión Avanzada de Modelos** (HU-013, HU-014)
9. ✅ **Preferencias UI** (HU-016, HU-017)
10. ✅ **Estadísticas y Uso** (HU-021)
11. ✅ **Modo Offline** (HU-022)
12. ✅ **Optimizaciones** (HU-025, HU-026)

### Prioridad BAJA (Features Nice-to-Have)
13. ✅ **Comparación de Modelos** (HU-020)
14. ✅ **Exportar/Compartir** (HU-018, HU-023)
15. ✅ **Favoritos** (HU-024)
16. ✅ **Eliminar Mensajes** (HU-011)
17. ✅ **Privacidad Avanzada** (HU-019, HU-028)

---

**Total Historias de Usuario:** 28  
**Puntos de Estimación Total:** ~135 puntos  
**Sprint Size sugerido:** 20-25 puntos (sprints de 2 semanas)