# 🔧 Cómo Solucionar el Error 401 (Unauthorized)

## 🔍 Diagnóstico Completo

Hemos probado tu API key directamente con OpenRouter y **confirmamos que es inválida:**

```json
{
  "error": {
    "message": "User not found.",
    "code": 401
  }
}
```

Esto significa que tu API key:
- ❌ Ha expirado
- ❌ Fue revocada
- ❌ Nunca fue válida
- ❌ La cuenta de OpenRouter no existe

---

## ✅ Solución Paso a Paso

### 1️⃣ Obtén una Nueva API Key

**Ve a:** https://openrouter.ai/keys

Si no tienes cuenta:
1. Click en **"Sign Up"**
2. Crea tu cuenta (puedes usar GitHub o Google)
3. Ve a la sección **"API Keys"**

Si ya tienes cuenta:
1. Inicia sesión
2. Ve a **"API Keys"** en el menú
3. Click en **"Create Key"**
4. Dale un nombre descriptivo: `ChatAi Android App`
5. **COPIA LA KEY COMPLETA** (empieza con `sk-or-v1-`)

⚠️ **IMPORTANTE:** Esta es tu única oportunidad de copiar la key. Si la pierdes, tendrás que crear una nueva.

---

### 2️⃣ Configura la Nueva API Key en la App

**Opción A - Desde la App (Recomendado):**

1. Abre la app ChatAi
2. Ve a **⚙️ Configuración**
3. Selecciona **"Gestionar API Key"**
4. Pega tu nueva API key
5. Presiona **"Guardar API Key"**
6. Verifica que aparezca: **"✓ API Key guardada correctamente"**

**Opción B - Actualizar el Código:**

Abre `app/src/main/java/com/example/chatai/config/ApiConfig.kt` y reemplaza:

```kotlin
const val DEFAULT_API_KEY = "TU_NUEVA_API_KEY_AQUI"
```

Por:

```kotlin
const val DEFAULT_API_KEY = "sk-or-v1-TU_KEY_COMPLETA_AQUI"
```

Luego recompila y ejecuta la app.

---

### 3️⃣ Verifica que Funcione

Ahora intenta enviar un mensaje en el chat:

1. Crea una nueva conversación
2. Escribe: `Hola, ¿funciona?`
3. Deberías recibir una respuesta del modelo Gemini

Si ves el mensaje, **¡ÉXITO! 🎉**

Si sigue el error 401:
- Verifica que copiaste la key completa (incluye `sk-or-v1-`)
- Asegúrate de que no haya espacios al inicio o final
- Confirma que tu cuenta de OpenRouter está activa
- Verifica que tengas créditos (los modelos gratuitos no requieren crédito)

---

## 📊 Modelos Disponibles

La app está configurada para usar **solo modelos de Google Gemini:**

### 🎁 Modelos Gratuitos (NO requieren crédito)
- **Gemini 2.0 Flash (Experimental)** - Rápido y capaz
- **Gemini Exp 1206** - Contexto extendido (2M tokens)

### 💰 Modelos de Pago (requieren crédito)
- **Gemini Flash 1.5 8B** - Muy económico ($0.000075/$0.0003 por 1K)
- **Gemini Flash 1.5** - Balance ideal
- **Gemini Pro 1.5** - Máxima capacidad

**Por defecto, la app usa:** `google/gemini-2.0-flash-exp:free` (GRATIS)

---

## 🆘 ¿Sigue sin funcionar?

Si después de seguir todos los pasos el error persiste:

1. **Verifica los logs de Android Studio:**
   - Filtro: `tag:UserPreferencesRepository OR tag:StreamAiResponse`
   - Busca líneas como: `API Key retrieved from storage - Length: XX`

2. **Revisa que la API key tenga el formato correcto:**
   - Debe empezar con: `sk-or-v1-`
   - Debe tener ~70 caracteres
   - No debe contener espacios

3. **Verifica tu cuenta de OpenRouter:**
   - Está activa y verificada
   - La API key no ha sido revocada
   - Tienes límites de rate disponibles

4. **Prueba con otro modelo:**
   - En el chat, toca el menú de 3 puntos (⋮)
   - Selecciona otro modelo de la lista
   - Intenta de nuevo

---

## 🔐 Nota sobre Seguridad

**IMPORTANTE:** La encriptación está temporalmente desactivada para debugging.

Esto significa que tu API key se guarda en texto plano en:
- `SharedPreferences` (modo privado de Android)
- No es accesible por otras apps
- Se borrará si desinstalas la app

Una vez confirmado que todo funciona, la encriptación se restaurará en una actualización futura.

---

## 📝 Resumen

1. ✅ La API key actual es **INVÁLIDA**
2. ✅ Necesitas **crear una nueva** en OpenRouter
3. ✅ Guardar la nueva key en **Ajustes → Gestionar API Key**
4. ✅ Probar enviando un mensaje

¿Listo? ¡Ve a https://openrouter.ai/keys ahora! 🚀

