🍰 Pastelería Mil Sabores - App Android

Este es el proyecto oficial de la aplicación móvil para "Pastelería Mil Sabores", desarrollado en Kotlin utilizando Jetpack Compose para la asignatura Aplicaciones Móviles.

📝 Descripción del Proyecto

Esta aplicación permite a los usuarios explorar el catálogo de productos de la pastelería, ver detalles de los productos, añadirlos a un carrito de compras y realizar un proceso de checkout. Incluye un sistema de autenticación de clientes completo para gestionar perfiles y aplicar descuentos especiales basados en la fecha de nacimiento o códigos promocionales.

👨‍💻 Estudiantes

Solange Labbé

Carlos Rojas

✨ Funcionalidades Implementadas

Catálogo de Productos: 

    Visualización de productos cargados desde ProductRepository y agrupados por categorías.

Detalle de Producto:

    Pantalla individual para cada producto con su descripción, precio y la opción de añadir un mensaje especial (opcional).

Sistema de Autenticación:

    Registro e inicio de sesión de usuarios con un sistema completo.

    Selector de fecha amigable para registrar el cumpleaños.

Gestión de Perfil de Usuario:

    Los usuarios pueden actualizar su nombre, correo y contraseña.

    Funcionalidad para cerrar sesión.

Carrito de Compras:

    Añadir productos al carrito (implementado con CartViewModel).

    Visualizar un resumen de los productos en el carrito.

    Cálculo del subtotal.

Proceso de Pago y Descuentos:

    Simulación de un proceso de pago.

    Cálculo automático y aplicación de descuentos en el carrito para usuarios registrados (authUiState.applicableDiscountPercent).

Navegación condicional: Si el usuario no está logueado al ir a pagar, se le redirige al Login.

    Visualización de una boleta electrónica con el resumen de la compra antes de finalizar el proceso.

UI Moderna:

    Interfaz de usuario limpia y reactiva construida 100% con Jetpack Compose.

    Uso de Scaffold, NavHost, LazyColumn, Card, BadgedBox, etc.

    Formateo de precios a moneda local (CLP) usando NumberFormat.

🚀 Pasos para Ejecutar el Proyecto

Para la compilación y ejecución de esta aplicación, se requiere contar con Android Studio (versión recomendada: Hedgehog o más reciente).

Clonar el Repositorio (Opcional):
En caso de que el proyecto se encuentre en un repositorio Git, se debe ejecutar el siguiente comando:

git clone [URL_DEL_REPOSITORIA_GIT]



De lo contrario, la carpeta del proyecto puede abrirse directamente.

Abrir en Android Studio:

  Iniciar Android Studio.

  Seleccionar la opción "Open" (Abrir).

  Dirigirse a la carpeta raíz del proyecto (aquella que contiene build.gradle y la carpeta app) y seleccionarla.

Sincronizar Gradle:

  Android Studio detectará automáticamente el proyecto y solicitará la sincronización de los archivos Gradle. Se debe hacer clic en "Sync Now" (Sincronizar ahora) en la barra que aparecerá en la parte superior.

  Espere hasta que Android Studio complete la descarga de todas las dependencias necesarias.

Ejecutar la Aplicación:

Dispositivo Virtual: Es necesario verificar la configuración de un emulador de Android (se puede crear desde Tools > Device Manager).

Dispositivo Físico: Alternativamente, se puede conectar un dispositivo Android físico al equipo (asegurándose de tener las opciones de desarrollador y la depuración USB habilitadas).

  Seleccione el dispositivo deseado en la barra de herramientas de Android Studio (junto al botón de "Run").

  Presione el botón "Run 'app'.

  La aplicación se compilará y se instalará en el dispositivo o emulador seleccionado.
