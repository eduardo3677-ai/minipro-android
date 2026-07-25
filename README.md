# FlashLabs T48 / TL866II Android App 📱⚡️

¡Bienvenido al repositorio oficial de **FlashLabs**, la adaptación profesional de código abierto de *minipro* para dispositivos Android! 

FlashLabs es una interfaz moderna construida sobre **Jetpack Compose** que envuelve nativamente el motor escrito en C de la popular herramienta [minipro](https://gitlab.com/DavidGriffith/minipro). Permite programar miles de microcontroladores, memorias EEPROM, memorias NAND y SPI directamente desde tu teléfono o tablet usando un adaptador USB OTG.

## Características Principales ✨
- **Motor C++ Nativo (NDK/JNI):** Conserva el 100% de la lógica original (NAND OOB, cálculos ECC, latencias VCC/VPP) usando el código C original procesado por el NDK de Android.
- **Sin Necesidad de ROOT:** A diferencia del puerto estándar de Linux que requiere privilegios elevados para `libusb`, FlashLabs redirige transparentemente el *File Descriptor* gestionado por el entorno Android hacia el subsistema nativo mediante `libusb_wrap_sys_device()`.
- **Soporte Oficial de Bases de Datos:** Es compatible directamente con el archivo original `infoic.xml`, ofreciendo soporte para **más de 30,000 chips**.
- **Interfaz Moderna:** Pantalla inmersiva (Edge-to-Edge) diseñada en Jetpack Compose Material Design 3 con soporte para múltiples temas (Oscuro/Claro).
- **Esquemas y Adaptadores (105 Imágenes):** Muestra el diagrama exacto de cómo conectar tu componente en el Zócalo ZIF-40 o adaptadores como TSOP48 y BGA153.
- **Navegador y Editor Hexadecimal:** Navega rápidamente entre direcciones de memoria (Go to Address), busca texto ASCII o patrones puros en formato hexadecimal, y guarda archivos en múltiples formatos (BIN, Intel HEX).

## Compilación y CI/CD ⚙️
El proyecto integra **GitHub Actions** para integración y entrega continua.
- Se compilan automáticamente las versiones **Debug** y **Release** al hacer `push` a la rama `main/master`.
- El flujo sube las versiones empaquetadas (APKs) de inmediato y crea un "GitHub Release" utilizando el control de versiones definido en `build.gradle.kts`.

## Hardware Soportado 🔌
- **XGecu T48** (Testado)
- **TL866II Plus**
- **XGecu T56** (A través de las variables experimentales de minipro)

> **Nota:** Necesitarás un adaptador USB OTG de buena calidad. Se aconseja que el dispositivo móvil tenga al menos un 50% de batería para asegurar la estabilidad de la tensión en el VCC durante la grabación y borrado.

## Créditos y Agradecimientos 🙌
Basado fuertemente en el trabajo de ingeniería inversa de la comunidad de [David Griffith / minipro](https://gitlab.com/DavidGriffith/minipro).
Diseño UI/UX y adaptaciones NDK para el puente Kotlin-C++ por la comunidad de FlashLabs.
