# Moli's Nail App

Esta es una aplicación móvil desarrollada para **Moli's Nail**, un salón de uñas. La aplicación permite a los usuarios gestionar citas, consultar un catálogo de uñas y explorar publicaciones de redes sociales directamente desde la app.

## Características

- **Gestión de citas**: Ver y reservar citas según la disponibilidad.
- **Catálogo de uñas**: Explora diferentes estilos y diseños de uñas.
- **Integración de redes sociales**: Muestra publicaciones de Instagram dentro de la app.

## Requisitos previos

Antes de empezar, deberás de contar con lo siguiente:

- **Android Studio** instalado en tu ordenador.
- **JDK** (Java Development Kit) versión 8 o superior.
- Una **cuenta de Firebase** para autenticación y almacenamiento, si deseas integrar Firebase.

## Configuración del proyecto

### 1. Clonar el repositorio

Clona el proyecto desde GitHub y navega hasta el directorio del proyecto:

git clone https://github.com/tu-usuario/molis-nail-app.git
cd molis-nail-app

## 2. Importar el proyecto en Android Studio

1. Abre **Android Studio**.
2. Selecciona **Open an Existing Project** y elige la carpeta del proyecto `molis-nail-app` que has clonado.
3. Android Studio sincronizará las dependencias y preparará el proyecto para ser ejecutado.

## 3. Configuración de Firebase

Para integrar autenticación y Firestore en la app:

1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/).
2. Añade una aplicación Android en Firebase y descarga el archivo `google-services.json`.
3. Coloca el archivo `google-services.json` en la carpeta `app` de tu proyecto.

## 4. Widget de Instagram

La aplicación puede mostrar publicaciones de Instagram usando el widget de **LightWidget**. Para configurarlo:

1. Crea una cuenta en [LightWidget](https://lightwidget.com/).
2. Genera un widget con tu cuenta de Instagram y copia la URL del widget.
3. Sustituye la URL de ejemplo en el archivo `HomeFragment.java` por la URL de tu propio widget.

## 5. Probar la aplicación en un dispositivo o emulador

Una vez configurado e importado el proyecto en Android Studio, sigue estos pasos para probar la aplicación:

1. Conecta un dispositivo Android o abre un emulador.
2. Haz clic en **Run > Run 'app'** o selecciona el botón de ejecución en la barra de herramientas de Android Studio.
3. La aplicación se instalará en tu dispositivo o emulador, permitiéndote explorar las funcionalidades y probar la app.


## Contacto

Para más información o preguntas, puedes ponerte en contacto a través de:

- **Email**: `contact@molisnail.com`
