# AI Agents - Chat AI Android App

Este documento describe los agentes de IA y modelos disponibles para la aplicación Chat AI Android.

## Modelos Principales Disponibles

### OpenAI Models

#### GPT-4 Turbo
- **ID**: `openai/gpt-4-turbo`
- **Contexto**: 128k tokens
- **Precio**: $0.01 / 1K tokens input, $0.03 / 1K tokens output
- **Descripción**: Modelo más avanzado de OpenAI, excelente para tareas complejas y creativas
- **Uso recomendado**: Análisis complejo, programación avanzada, creatividad

#### GPT-4
- **ID**: `openai/gpt-4`
- **Contexto**: 8k tokens
- **Precio**: $0.03 / 1K tokens input, $0.06 / 1K tokens output
- **Descripción**: Modelo GPT-4 estándar, balance entre calidad y costo
- **Uso recomendado**: Uso general, análisis de texto

#### GPT-3.5 Turbo
- **ID**: `openai/gpt-3.5-turbo`
- **Contexto**: 16k tokens
- **Precio**: $0.001 / 1K tokens input, $0.002 / 1K tokens output
- **Descripción**: Modelo rápido y económico
- **Uso recomendado**: Conversaciones casuales, tareas simples

### Anthropic Models

#### Claude 3 Opus
- **ID**: `anthropic/claude-3-opus-20240229`
- **Contexto**: 200k tokens
- **Precio**: $0.015 / 1K tokens input, $0.075 / 1K tokens output
- **Descripción**: Modelo más potente de Claude, excelente razonamiento
- **Uso recomendado**: Análisis profundo, escritura creativa, razonamiento complejo

#### Claude 3 Sonnet
- **ID**: `anthropic/claude-3-sonnet-20240229`
- **Contexto**: 200k tokens
- **Precio**: $0.003 / 1K tokens input, $0.015 / 1K tokens output
- **Descripción**: Balance perfecto entre capacidad y velocidad
- **Uso recomendado**: Uso general, programación, análisis

#### Claude 3 Haiku
- **ID**: `anthropic/claude-3-haiku-20240307`
- **Contexto**: 200k tokens
- **Precio**: $0.00025 / 1K tokens input, $0.00125 / 1K tokens output
- **Descripción**: Modelo rápido y eficiente
- **Uso recomendado**: Respuestas rápidas, tareas simples

### Meta Models

#### Llama 3 70B
- **ID**: `meta-llama/llama-3-70b-instruct`
- **Contexto**: 8k tokens
- **Precio**: $0.0009 / 1K tokens input, $0.0009 / 1K tokens output
- **Descripción**: Modelo de código abierto de Meta
- **Uso recomendado**: Experimentación, tareas de código

#### Llama 3 8B
- **ID**: `meta-llama/llama-3-8b-instruct`
- **Contexto**: 8k tokens
- **Precio**: $0.0002 / 1K tokens input, $0.0002 / 1K tokens output
- **Descripción**: Modelo ligero y rápido
- **Uso recomendado**: Respuestas rápidas, prototipado

### Google Models

#### Gemini Pro
- **ID**: `google/gemini-pro`
- **Contexto**: 32k tokens
- **Precio**: $0.0005 / 1K tokens input, $0.0015 / 1K tokens output
- **Descripción**: Modelo multimodal de Google
- **Uso recomendado**: Análisis multimodal, tareas generales

## Configuración de Agentes

### Configuración por Defecto
```json
{
  "default_model": "openai/gpt-4-turbo",
  "temperature": 0.7,
  "max_tokens": 2000,
  "top_p": 1.0,
  "frequency_penalty": 0.0,
  "presence_penalty": 0.0
}
```

### Configuraciones Especializadas

#### Agente de Programación
```json
{
  "model": "anthropic/claude-3-opus-20240229",
  "temperature": 0.1,
  "max_tokens": 4000,
  "system_prompt": "Eres un experto programador. Proporciona código limpio, comentado y siguiendo las mejores prácticas."
}
```

#### Agente Creativo
```json
{
  "model": "openai/gpt-4-turbo",
  "temperature": 1.2,
  "max_tokens": 3000,
  "system_prompt": "Eres un escritor creativo. Genera contenido original, imaginativo y atractivo."
}
```

#### Agente Analítico
```json
{
  "model": "anthropic/claude-3-sonnet-20240229",
  "temperature": 0.3,
  "max_tokens": 4000,
  "system_prompt": "Eres un analista experto. Proporciona análisis detallados, estructurados y basados en evidencia."
}
```

#### Agente Rápido
```json
{
  "model": "anthropic/claude-3-haiku-20240307",
  "temperature": 0.5,
  "max_tokens": 1000,
  "system_prompt": "Proporciona respuestas concisas y directas."
}
```

## Comparación de Modelos

| Modelo | Velocidad | Calidad | Costo | Contexto | Mejor para |
|--------|-----------|---------|-------|----------|------------|
| GPT-4 Turbo | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ | Tareas complejas |
| Claude 3 Opus | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐⭐⭐ | Razonamiento profundo |
| Claude 3 Sonnet | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Balance general |
| GPT-3.5 Turbo | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | Uso frecuente |
| Claude 3 Haiku | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Respuestas rápidas |
| Llama 3 70B | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐ | Experimentación |
| Gemini Pro | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Multimodal |

## Casos de Uso Recomendados

### Desarrollo de Software
- **Principal**: Claude 3 Opus o GPT-4 Turbo
- **Alternativo**: Claude 3 Sonnet
- **Rápido**: Claude 3 Haiku

### Escritura Creativa
- **Principal**: GPT-4 Turbo
- **Alternativo**: Claude 3 Opus
- **Económico**: GPT-3.5 Turbo

### Análisis de Datos
- **Principal**: Claude 3 Opus
- **Alternativo**: GPT-4 Turbo
- **Rápido**: Claude 3 Sonnet

### Conversación Casual
- **Principal**: GPT-3.5 Turbo
- **Alternativo**: Claude 3 Haiku
- **Premium**: GPT-4 Turbo

### Investigación Académica
- **Principal**: Claude 3 Opus
- **Alternativo**: GPT-4 Turbo
- **Económico**: Claude 3 Sonnet

## Límites y Consideraciones

### Límites de Rate
- **OpenAI**: 10,000 tokens/minuto (tier 1)
- **Anthropic**: 5,000 tokens/minuto (tier 1)
- **Meta**: 2,000 tokens/minuto
- **Google**: 1,500 tokens/minuto

### Límites de Contexto
- **Claude 3**: 200k tokens (más largo)
- **GPT-4 Turbo**: 128k tokens
- **Gemini Pro**: 32k tokens
- **GPT-3.5 Turbo**: 16k tokens
- **Llama 3**: 8k tokens

### Consideraciones de Costo
- **Más económico**: Llama 3 8B, Claude 3 Haiku
- **Balance**: GPT-3.5 Turbo, Claude 3 Sonnet
- **Premium**: GPT-4 Turbo, Claude 3 Opus

## Implementación en la App

### Configuración de Modelos
```kotlin
data class AIModel(
    val id: String,
    val name: String,
    val provider: String,
    val contextLength: Int,
    val inputPrice: Double,
    val outputPrice: Double,
    val capabilities: List<String>
)

data class AgentConfig(
    val model: AIModel,
    val temperature: Float,
    val maxTokens: Int,
    val systemPrompt: String?,
    val parameters: Map<String, Any>
)
```

### Gestión de Agentes
```kotlin
class AgentManager {
    fun getAvailableModels(): List<AIModel>
    fun getDefaultAgent(): AgentConfig
    fun createCustomAgent(modelId: String, config: AgentConfig): AgentConfig
    fun switchAgent(conversationId: String, agentId: String)
}
```

## Actualizaciones y Mantenimiento

### Frecuencia de Actualización
- **Lista de modelos**: Diaria
- **Precios**: Semanal
- **Capacidades**: Mensual
- **Nuevos modelos**: Según disponibilidad

### Monitoreo
- Tiempo de respuesta por modelo
- Costo por conversación
- Satisfacción del usuario
- Errores y fallos

## Notas de Implementación

1. **Caché de modelos**: Mantener lista actualizada en local
2. **Fallback**: Tener modelo alternativo en caso de fallo
3. **Rate limiting**: Implementar colas de peticiones
4. **Costos**: Mostrar estimación antes de envío
5. **Contexto**: Gestionar truncamiento automático
6. **Streaming**: Implementar para todos los modelos soportados

---

**Última actualización**: $(date)
**Versión**: 1.0.0
