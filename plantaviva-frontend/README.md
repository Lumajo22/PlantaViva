# 🌿 PlantaViva — Frontend

Frontend React + Vite del sistema **PlantaViva — Control Inteligente de Cultivos**.

## 📋 Tecnologías

- **React 18** — Librería de UI
- **Vite** — Build tool ultrarrápido
- **React Router** — Navegación
- **Axios** — Cliente HTTP
- **Tailwind CSS** — Estilos utility-first
- **Recharts** — Gráficas interactivas
- **Lucide React** — Iconos modernos

## 📦 Instalación

### Requisitos previos

- **Node.js 18+** instalado ([descargar aquí](https://nodejs.org/))
- **Backend** corriendo en `http://localhost:8080`

### Paso 1 — Verificar Node.js

Abre una terminal y ejecuta:

```bash
node --version
```

Debe mostrar `v18.x.x` o superior.

### Paso 2 — Instalar dependencias

Navega a la carpeta del proyecto y ejecuta:

```bash
npm install
```

Esto instalará todas las dependencias listadas en `package.json`.

**⏱️ Tiempo estimado:** 1-2 minutos

### Paso 3 — Arrancar el servidor de desarrollo

```bash
npm run dev
```

Deberías ver algo como:

```
  VITE v5.1.0  ready in 500 ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

### Paso 4 — Abrir en el navegador

Abre tu navegador en:

```
http://localhost:5173
```

¡Listo! Deberías ver el Dashboard de PlantaViva. 🎉

---

## 🏗️ Estructura del proyecto

```
plantaviva-frontend/
├── index.html              ← HTML principal
├── package.json            ← Dependencias
├── vite.config.js          ← Configuración de Vite
├── tailwind.config.js      ← Configuración de Tailwind
├── postcss.config.js       ← PostCSS para Tailwind
└── src/
    ├── main.jsx            ← Punto de entrada
    ├── App.jsx             ← Rutas principales
    ├── index.css           ← Estilos globales + Tailwind
    ├── components/
    │   └── Layout.jsx      ← Layout con navegación
    ├── pages/
    │   ├── Dashboard.jsx   ← Página principal
    │   ├── Plantas.jsx     ← CRUD de plantas
    │   ├── Sensores.jsx    ← CRUD de sensores
    │   ├── Lecturas.jsx    ← Monitoreo con gráficas
    │   └── Alertas.jsx     ← Panel de alertas
    └── services/
        └── api.js          ← Cliente Axios + endpoints
```

---

## 🎨 Páginas incluidas

### 1. **Dashboard** (`/`)
- Resumen general del sistema
- Estadísticas: plantas activas, sensores, alertas pendientes
- Cards informativos

### 2. **Plantas** (`/plantas`)
- Tabla completa con todas las plantas
- CRUD completo (crear, editar, eliminar)
- Modal para formularios
- Indicador de estado activo/inactivo

### 3. **Sensores** (`/sensores`)
- Grid de tarjetas con sensores
- Creación con selección de planta y tipo
- Auto-asignación de unidades según tipo
- Gestión de umbrales mín/máx

### 4. **Lecturas** (`/lecturas`)
- **Gráfica interactiva** con Recharts
- Selector de sensor
- Registro manual de lecturas
- Detección visual de anomalías (puntos rojos)
- Historial completo

### 5. **Alertas** (`/alertas`)
- Lista de alertas pendientes y revisadas
- Filtro pendientes/todas
- Marcar como revisada
- Eliminar alertas

---

## 🔌 Conexión con el Backend

El frontend está configurado para conectarse automáticamente a:

```
http://localhost:8080/api
```

Esto está definido en `src/services/api.js`.

**Si tu backend corre en otro puerto**, edita ese archivo y cambia la línea:

```javascript
baseURL: 'http://localhost:OTRO_PUERTO/api',
```

---

## 🎨 Personalización de estilos

Los colores principales están en `tailwind.config.js`:

```javascript
colors: {
  primary: {
    500: '#22c55e',  // Verde principal
    600: '#16a34a',
    700: '#15803d',
    // ...
  }
}
```

Puedes cambiar estos valores para personalizar el tema.

---

## 🛠️ Scripts disponibles

```bash
npm run dev      # Servidor de desarrollo (http://localhost:5173)
npm run build    # Build para producción (carpeta dist/)
npm run preview  # Preview del build de producción
```

---

## 🚀 Build para producción

Cuando quieras generar los archivos optimizados:

```bash
npm run build
```

Esto crea una carpeta `dist/` con todo el frontend listo para desplegar en cualquier servidor web.

---

## 🧪 Prueba rápida

1. **Backend corriendo** → `http://localhost:8080`
2. **Frontend corriendo** → `http://localhost:5173`
3. **Crear una planta** → Ir a "Plantas" → clic en "Nueva Planta"
4. **Crear un sensor** → Ir a "Sensores" → clic en "Nuevo Sensor"
5. **Registrar lectura** → Ir a "Lecturas" → clic en "Registrar Lectura"
6. **Ver alertas** → Si la lectura está fuera de rango, ir a "Alertas"

---

## ❓ Solución de problemas

### Error: `Cannot GET /api/plantas`
- Verifica que el backend esté corriendo en el puerto 8080
- Revisa la consola del navegador (F12) para ver el error exacto

### Error: `npm: command not found`
- Node.js no está instalado o no está en el PATH
- Descarga Node.js de https://nodejs.org/

### Pantalla en blanco
- Abre la consola del navegador (F12) y busca errores
- Verifica que ejecutaste `npm install` antes de `npm run dev`

---

**Equipo PlantaViva** • Proyecto académico • Versión 1.0.0
