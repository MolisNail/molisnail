Moli's Nail App
Esta es una aplicación móvil desarrollada para Moli's Nail, un salón de uñas. La aplicación permite a los usuarios gestionar citas, consultar un catálogo de uñas, y explorar publicaciones de redes sociales directamente desde la app.

Características
Gestión de citas: Ver y reservar citas según la disponibilidad.
Catálogo de uñas: Explora diferentes estilos y diseños de uñas.
Integración de redes sociales: Muestra publicaciones de Instagram dentro de la app.
Requisitos previos
Antes de empezar, asegúrate de contar con lo siguiente:

Android Studio instalado en tu ordenador.
JDK (Java Development Kit) versión 8 o superior.
Una cuenta de Firebase para autenticación y almacenamiento, si deseas integrar Firebase.
Configuración del proyecto
1. Clonar el repositorio
Clona el proyecto desde GitHub y navega hasta el directorio del proyecto:

bash
Copiar código
git clone https://github.com/tu-usuario/molis-nail-app.git
cd molis-nail-app
2. Importar el proyecto en Android Studio
Abre Android Studio.
Selecciona Open an Existing Project y elige la carpeta del proyecto molis-nail-app que has clonado.
Android Studio sincronizará las dependencias y preparará el proyecto para ser ejecutado.
3. Configuración de Firebase (Opcional)
Para integrar autenticación y Firestore en la app:

Crea un proyecto en Firebase Console.
Añade una aplicación Android en Firebase y descarga el archivo google-services.json.
Coloca el archivo google-services.json en la carpeta app de tu proyecto.
4. Widget de Instagram (Opcional)
La aplicación puede mostrar publicaciones de Instagram usando el widget de LightWidget. Para configurarlo:

Crea una cuenta en LightWidget.
Genera un widget con tu cuenta de Instagram y copia la URL del widget.
Sustituye la URL de ejemplo en el archivo HomeFragment.java por la URL de tu propio widget.
5. Probar la aplicación en un dispositivo o emulador
Una vez configurado e importado el proyecto en Android Studio, sigue estos pasos para probar la aplicación:

Conecta un dispositivo Android o abre un emulador.
Haz clic en Run > Run 'app' o selecciona el botón de ejecución en la barra de herramientas de Android Studio.
La aplicación se instalará en tu dispositivo o emulador, permitiéndote explorar las funcionalidades y probar la app.
Contribuir
Si deseas contribuir al proyecto:

Realiza un fork de este repositorio.
Crea una nueva rama para tus cambios: git checkout -b feature/nueva-funcionalidad.
Realiza tus cambios y haz commit: git commit -m 'Añadida nueva funcionalidad'.
Haz push a la rama: git push origin feature/nueva-funcionalidad.
Abre un Pull Request en este repositorio.
Contacto
Para más información o preguntas, puedes ponerte en contacto a través de:

Email: contacto@molisnail.com
Instagram: @molisnail
