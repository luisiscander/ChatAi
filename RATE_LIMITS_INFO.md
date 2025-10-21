# ⏱️ Límites de Rate Limiting - OpenRouter

## 🔴 Error 429: "Rate Limit Exceeded"

Este error significa que has enviado **demasiadas solicitudes en un período corto de tiempo**.

---

## 📊 Límites por Modelo

### 🎁 Modelos GRATUITOS (más restrictivos)

#### google/gemini-2.0-flash-exp:free
- **Requests**: ~10-20 por minuto
- **Tokens**: Limitado por día
- **Recomendación**: Espera 10-15 segundos entre mensajes

#### google/gemini-exp-1206:free
- **Requests**: ~10-20 por minuto
- **Tokens**: Limitado por día
- **Recomendación**: Espera 10-15 segundos entre mensajes

### 💰 Modelos DE PAGO (más permisivos)

#### google/gemini-flash-1.5-8b
- **Requests**: ~120 por minuto
- **Tokens**: ~120,000 por minuto
- **Costo**: $0.000075 (input) / $0.0003 (output) por 1K tokens

#### google/gemini-flash-1.5
- **Requests**: ~100 por minuto
- **Tokens**: ~100,000 por minuto
- **Costo**: $0.00015 / $0.0006 por 1K tokens

#### google/gemini-pro-1.5
- **Requests**: ~80 por minuto
- **Tokens**: ~160,000 por minuto
- **Costo**: $0.00125 / $0.005 por 1K tokens

---

## ✅ Soluciones al Error 429

### 1️⃣ Espera entre solicitudes
```
⏰ TIEMPO RECOMENDADO DE ESPERA:
• Modelos gratuitos: 10-15 segundos
• Modelos de pago: 1-2 segundos
```

### 2️⃣ Revisa tu uso actual

Ve a: **https://openrouter.ai/activity**

Aquí puedes ver:
- Cuántas requests has hecho
- Cuánto crédito has usado
- Cuándo se resetean tus límites

### 3️⃣ Cambia a un modelo de pago

Los modelos de pago tienen límites **mucho más altos**:
- ✅ ~100-120 requests por minuto
- ✅ Sin límites diarios estrictos
- ✅ Respuestas más rápidas
- ✅ Mejor prioridad en la cola

**Costo estimado:**
- Conversación típica (20 mensajes): ~$0.01 - $0.05 USD
- Uso diario moderado: $0.50 - $2.00 USD/mes

### 4️⃣ Agrega créditos a tu cuenta

1. Ve a: https://openrouter.ai/credits
2. Agrega crédito (mínimo $5 USD)
3. Ahora puedes usar modelos de pago sin límites estrictos

---

## 🛠️ Implementado en la App

### Mensajes de Error Mejorados

Cuando ocurre un error 429, la app ahora muestra:
- ⏱️ Tiempo de espera recomendado (del header `retry-after`)
- 💡 Consejos para evitar el error
- 📚 Link a documentación de OpenRouter

### Headers de Rate Limit

OpenRouter devuelve estos headers útiles:
```
X-RateLimit-Limit: 20          # Límite total
X-RateLimit-Remaining: 5       # Requests restantes
X-RateLimit-Reset: 1234567890  # Cuándo se resetea
Retry-After: 60                # Segundos para reintentar
```

La app lee el header `retry-after` y te dice exactamente cuánto esperar.

---

## 📈 Recomendaciones por Caso de Uso

### 💬 Uso Casual (< 100 mensajes/día)
```
✅ MODELO: google/gemini-2.0-flash-exp:free
✅ COSTO: Gratis
⚠️ LÍMITE: ~20 requests/minuto
💡 TIP: Espera 10-15 segundos entre mensajes
```

### 🔬 Uso Moderado (100-500 mensajes/día)
```
✅ MODELO: google/gemini-flash-1.5-8b
✅ COSTO: ~$0.50-2.00 USD/mes
✅ LÍMITE: ~120 requests/minuto
💡 TIP: Agrega $10 de crédito para el mes
```

### 💼 Uso Intensivo (> 500 mensajes/día)
```
✅ MODELO: google/gemini-flash-1.5 o gemini-pro-1.5
✅ COSTO: ~$2-10 USD/mes
✅ LÍMITE: ~100+ requests/minuto
💡 TIP: Considera el plan Enterprise de OpenRouter
```

---

## 🔍 Debugging del Error 429

Si recibes el error constantemente:

### Paso 1: Verifica tu actividad
```bash
curl https://openrouter.ai/api/v1/auth/key \
  -H "Authorization: Bearer TU_API_KEY"
```

Respuesta muestra:
- `rate_limit.requests_remaining`
- `rate_limit.requests_reset_at`

### Paso 2: Revisa los logs
En Logcat busca:
```
tag:StreamAiResponse level:error
```

Verás el error body completo con detalles.

### Paso 3: Prueba con otro modelo
Cambia temporalmente a un modelo de pago para confirmar que es un rate limit.

---

## 📚 Enlaces Útiles

- **OpenRouter Rate Limits**: https://openrouter.ai/docs#limits
- **OpenRouter Pricing**: https://openrouter.ai/docs#pricing
- **Tu Activity**: https://openrouter.ai/activity
- **Agregar Créditos**: https://openrouter.ai/credits
- **Status de la API**: https://status.openrouter.ai

---

## 💡 Pro Tips

1. **Usa drafts**: La app guarda tu mensaje como borrador si falla. No lo pierdas.

2. **Modo avión**: Si tienes varios mensajes pendientes, envíalos con pausas.

3. **Batch messages**: Si tienes muchas preguntas, combínalas en un solo mensaje.

4. **Horarios pico**: Los rate limits son más estrictos en horarios pico (9am-5pm PST).

5. **Caché local**: La app cachea conversaciones y modelos para reducir requests.

---

**Última actualización**: Enero 2025  
**Versión de la app**: 1.0.0

