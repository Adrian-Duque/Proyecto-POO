# MiniJuegos POO

**Proyecto Final — Programación Orientada a Objetos · 1º Ingeniería Informática**

Aplicación de escritorio en Java con interfaz gráfica Swing, arquitectura en cuatro capas y persistencia en ficheros. Integra dos juegos completos — **Pasapalabra** y **Tres en Raya** — con un sistema de usuarios, roles, estadísticas y partidas pausables.

> Documentación web: https://jp-aceves.github.io/MiniJuegos-Proyecto-POO/
> Presentación: https://jp-aceves.github.io/MiniJuegos-Proyecto-POO/Presentacion.html

---

## Índice

1. [Descripción general](#descripción-general)
2. [Juegos implementados](#juegos-implementados)
3. [Arquitectura del sistema](#arquitectura-del-sistema)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Decisiones de diseño](#decisiones-de-diseño)
6. [Cómo compilar y ejecutar](#cómo-compilar-y-ejecutar)
7. [Tecnologías](#tecnologías)
8. [Reparto de trabajo](#reparto-de-trabajo)
9. [Autores](#autores)

---

## Descripción general

MiniJuegos POO es una plataforma de juegos multijugador local que permite a varios usuarios registrarse, iniciar sesión y competir entre sí. El sistema mantiene un ranking de puntuaciones, permite pausar partidas en mitad del juego y reanudarlas más tarde, y ofrece un panel de administración para gestionar usuarios y datos.

La interfaz sigue un diseño visual coherente negro/amarillo implementado a través de una clase `Tema` centralizada, lo que garantiza consistencia en todas las ventanas sin colores hardcodeados.

---

## Juegos implementados

### Pasapalabra

Juego individual de preguntas por letras basado en el programa televisivo español.

- Rosco de 27 letras (A–Z + CH) con una pregunta por letra
- El jugador puede **responder**, **pasar** o **fallar** cada letra
- Temporizador con cuenta atrás configurable por dificultad
- 4 niveles de dificultad con ficheros de preguntas independientes: `facil`, `medio`, `avanzado`, `infantil`
- La partida se puede pausar en cualquier momento y reanudar posteriormente
- Puntuación basada en letras acertadas

### Tres en Raya

Juego clásico para dos jugadores en local.

- Tablero 3×3 con fichas X (amarillo) y O (blanco)
- Turnos alternados con validación de celda ocupada
- Detección automática de victoria (filas, columnas, diagonales) y empate
- La partida se puede pausar y reanudar
- El ganador suma 10 puntos al ranking

---

## Arquitectura del sistema

El proyecto sigue una **arquitectura en cuatro capas** con dependencias unidireccionales: Vista → Controlador → Modelo ← Persistencia.

```
┌─────────────────────────────────────────────────────────┐
│  VISTA  (Java Swing)                                    │
│  VentanaLogin · VentanaMenuPrincipal · VentanaJuego*    │
│  VentanaJuegoTresEnRaya · VentanaJuegoPasapalabra       │
│  VentanaSeleccionJuego · VentanaEstadisticas            │
│  VentanaAdmin · Tema (estilos)                          │
└────────────────────┬────────────────────────────────────┘
                     │ usa
┌────────────────────▼────────────────────────────────────┐
│  CONTROLADOR                                            │
│  GestorUsuarios · GestorPartidas · GestorEstadisticas   │
│  GestorJuegos                                           │
└────────────────────┬────────────────────────────────────┘
                     │ opera sobre
┌────────────────────▼────────────────────────────────────┐
│  MODELO                                                 │
│  Juego* · PasaPalabra · TresEnRaya                      │
│  Usuario* · Jugador · Administrador                     │
│  Partida · Estadistica · PuntuacionJugador              │
│  EstadoPartida                                          │
└────────────────────┬────────────────────────────────────┘
                     │ persiste con
┌────────────────────▼────────────────────────────────────┐
│  PERSISTENCIA                                           │
│  GestorPersistencia (interfaz) · PersistenciaArchivos   │
└─────────────────────────────────────────────────────────┘
  * = clase abstracta
```

### Flujo de la aplicación

```
Arranque (Aplicacion.main)
  └─► VentanaLogin
        ├─► [login]    → GestorUsuarios.iniciarSesion()
        └─► [registro] → GestorUsuarios.registrarUsuario()
              └─► VentanaMenuPrincipal
                    ├─► [Jugar]        → VentanaSeleccionJuego → VentanaJuegoXxx
                    ├─► [Cargar]       → GestorPartidas.listarPartidasPausadas() → VentanaJuegoXxx
                    ├─► [Estadísticas] → VentanaEstadisticas
                    ├─► [Admin]        → VentanaAdmin  (solo administradores)
                    └─► [Cerrar sesión]→ VentanaLogin
```

---

## Estructura del proyecto

```
MiniJuegos-Proyecto-POO/
│
├── Programa/
│   ├── compilar.sh                      # Compilar y ejecutar en Mac/Linux
│   ├── compilar.bat                     # Compilar y ejecutar en Windows
│   │
│   ├── src/
│   │   │
│   │   ├── Vista/                       # Capa de presentación (Swing)
│   │   │   ├── Aplicacion.java          # Punto de entrada; inyecta dependencias y lanza la GUI
│   │   │   ├── Tema.java                # Única fuente de verdad de colores y tipografía
│   │   │   ├── VentanaLogin.java        # Login y registro de usuario
│   │   │   ├── VentanaMenuPrincipal.java# Menú principal post-login
│   │   │   ├── VentanaSeleccionJuego.java# Diálogo para elegir juego
│   │   │   ├── VentanaJuego.java        # Clase abstracta: lógica común de pausar/finalizar
│   │   │   ├── VentanaJuegoTresEnRaya.java
│   │   │   ├── VentanaJuegoPasapalabra.java
│   │   │   ├── VentanaEstadisticas.java # Tabla de ranking y resultados
│   │   │   └── VentanaAdmin.java        # Panel de gestión (usuarios, partidas, ranking)
│   │   │
│   │   ├── Controlador/                 # Capa de lógica de negocio
│   │   │   ├── GestorUsuarios.java      # Registro, login, roles, borrar usuarios
│   │   │   ├── GestorPartidas.java      # Crear, pausar, reanudar y finalizar partidas
│   │   │   ├── GestorEstadisticas.java  # Registrar resultados y consultar ranking
│   │   │   ├── GestorJuegos.java        # Registro de juegos disponibles por nombre
│   │   │   └── Sistema.java             # Punto de entrada alternativo (delega en Aplicacion)
│   │   │
│   │   ├── Modelo/                      # Capa de dominio
│   │   │   ├── Juego.java               # Clase abstracta: estado, puntuaciones, serialización
│   │   │   ├── PasaPalabra.java         # Implementación completa del juego Pasapalabra
│   │   │   ├── TresEnRaya.java          # Implementación completa del juego Tres en Raya
│   │   │   ├── Usuario.java             # Clase abstracta base de cuenta de usuario
│   │   │   ├── Jugador.java             # Usuario estándar (esAdmin = false)
│   │   │   ├── Administrador.java       # Usuario con privilegios de administración
│   │   │   ├── Partida.java             # Representa una sesión de juego con sus jugadores
│   │   │   ├── Estadistica.java         # Resultado individual de un jugador en una partida
│   │   │   ├── PuntuacionJugador.java   # Par (username, puntos) usado dentro de los juegos
│   │   │   ├── EstadoPartida.java       # Enum: EN_CURSO, PAUSADA, FINALIZADA
│   │   │   └── roscos/                  # Ficheros de preguntas para Pasapalabra
│   │   │       ├── rosco_facil.txt
│   │   │       ├── rosco_medio.txt
│   │   │       ├── rosco_avanzado.txt
│   │   │       └── rosco_infantil.txt
│   │   │
│   │   └── Persistencia/                # Capa de almacenamiento
│   │       ├── GestorPersistencia.java  # Interfaz: contrato de lectura/escritura
│   │       └── PersistenciaArchivos.java# Implementación en ficheros de texto plano
│   │
│   └── data/                            # Datos en tiempo de ejecución (generados automáticamente)
│       ├── usuarios.txt                 # Cuentas registradas (username + hash SHA-256)
│       ├── estadisticas.txt             # Historial de resultados
│       └── partidas/                    # Partidas pausadas serializadas
│
├── docs/
│   └── README.md                        # Este fichero
│
└── Diagrama_TrabajoFinal.drawio         # Diagrama UML de clases
```

---

## Decisiones de diseño

### `Juego` como clase abstracta

Todos los juegos comparten estado común: nombre, flag de finalizado y mapa de puntuaciones por jugador. Los métodos abstractos que cada juego implementa son:

| Método | Responsabilidad |
|--------|----------------|
| `inicializar()` | Reiniciar el estado interno del juego |
| `getEstadoTexto()` | Representación en texto del estado actual |
| `serializarEstado()` | Convertir el estado a String para persistirlo |
| `deserializarEstado(String)` | Restaurar el estado desde un String guardado |
| `terminar()` | Marcar el juego como finalizado |

### `GestorPersistencia` como interfaz

Desacopla completamente el resto del sistema del mecanismo de almacenamiento. Cambiar de ficheros de texto a una base de datos solo requiere implementar la interfaz — ninguna otra clase cambia.

### Jerarquía de `VentanaJuego`

`VentanaJuego` es una clase abstracta que encapsula la lógica común a todos los juegos: referencia a la ventana padre, acceso a `GestorPartidas` y `GestorEstadisticas`, y los métodos `accionPausar()` y `accionFinalizar()` con el flujo correcto de persistencia. Ambas ventanas de juego (`VentanaJuegoTresEnRaya` y `VentanaJuegoPasapalabra`) extienden esta clase, garantizando comportamiento uniforme y eliminando duplicación.

### Jerarquía de `Usuario`

```
Usuario (abstracta)
├── Jugador      → cuenta estándar, acceso solo a juegos y estadísticas propias
└── Administrador → acceso al panel de administración (borrar usuarios, ver todos los datos)
```

### `Tema.java` como fuente única de estilos

Clase final con constantes estáticas para todos los colores y fuentes. Ninguna ventana tiene colores o fuentes hardcodeados — todas referencian `Tema.XXX`. Esto garantiza que cualquier cambio visual se aplica de forma global modificando un único fichero.

### Serialización de partidas pausadas

Cuando se pausa una partida, `GestorPartidas` guarda en fichero el estado completo con el formato:

```
nombreJuego|jugador1,jugador2|estadoSerializadoDelJuego
```

Cada juego define su propio formato interno en `serializarEstado()`. Al reanudar, el gestor reconstruye el objeto `Juego` con `deserializarEstado()` y restaura los jugadores.

---

## Cómo compilar y ejecutar

**Requisito:** Java 17 o superior instalado (`java -version` debe funcionar en la terminal).

### Mac / Linux

```bash
cd Programa
chmod +x compilar.sh
./compilar.sh
```

### Windows

```cmd
cd Programa
compilar.bat
```

### Manual (cualquier sistema)

```bash
cd Programa
find src -name "*.java" > sources.txt        # En Windows: dir /s /b src\*.java > sources.txt
javac -d out -sourcepath src @sources.txt
java -cp out Vista.Aplicacion
```

> **Importante:** ejecuta siempre desde la carpeta `Programa/`. Los ficheros de roscos y de datos se buscan con rutas relativas a esa carpeta. Ejecutar desde otro directorio causará errores al cargar preguntas.

La carpeta `data/` se crea automáticamente en el primer arranque. El usuario administrador por defecto se crea al registrarse con el nombre `admin`.

---

## Tecnologías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17+ | Lenguaje principal |
| Java Swing | (incluido en JDK) | Interfaz gráfica de escritorio |
| SHA-256 (MessageDigest) | (incluido en JDK) | Hash de contraseñas |
| Ficheros de texto plano | — | Persistencia de datos (requisito de asignatura) |
| Draw.io | — | Diagramas UML |

---

## Reparto de trabajo

| Miembro | Componentes desarrollados |
|---------|--------------------------|
| **JP Aceves** | `Juego` (abstracta), `Partida`, `EstadoPartida`, `PuntuacionJugador`, `GestorPersistencia` (interfaz), `PersistenciaArchivos`, `GestorPartidas`, `VentanaJuego` (abstracta), `Aplicacion`, `Sistema` |
| **Adrián Duque** | `Usuario` (abstracta), `Jugador`, `Administrador`, `PasaPalabra`, `VentanaLogin`, `VentanaMenuPrincipal`, `VentanaJuegoPasapalabra` |
| **Juan Carlos Alcazarde** | `Estadistica`, `GestorEstadisticas`, `VentanaEstadisticas`, `VentanaAdmin` |
| **Ignacio del Peso** | `TresEnRaya`, `GestorJuegos`, `VentanaJuegoTresEnRaya`, `VentanaSeleccionJuego` |

---

## Autores

- **JP Aceves** — [@jp-aceves](https://github.com/jp-aceves)
- **Adrián Duque** — [@Adrian-Duque](https://github.com/Adrian-Duque)
- **Juan Carlos Pérez**
- **Ignacio del Peso**
