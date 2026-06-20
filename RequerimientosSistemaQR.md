# Requerimientos del Sistema de Registro y Validacion por QR

## 1. Descripcion General

El sistema de registro y validacion por QR tiene como finalidad permitir que un usuario complete un formulario digital, que el sistema almacene sus datos, genere un codigo QR unico asociado al registro y que posteriormente un operador pueda escanear dicho QR para validar la autenticidad y estado del registro.

Este sistema busca reducir procesos manuales, evitar duplicidad de informacion, agilizar la validacion de usuarios y mantener trazabilidad sobre cada registro y escaneo realizado.

## 2. Alcance del Sistema

El sistema contempla las siguientes funcionalidades principales:

* Registro de usuarios mediante formulario digital.
* Validacion de campos obligatorios y formatos.
* Prevencion de registros duplicados.
* Almacenamiento de datos en base de datos.
* Generacion de un codigo QR unico por registro.
* Visualizacion o descarga del QR generado.
* Escaneo del QR mediante camara o lector compatible.
* Consulta del registro asociado al QR.
* Validacion del estado del QR.
* Actualizacion del estado despues de un escaneo exitoso.
* Registro de historial de escaneos.

El sistema no contempla inicialmente pagos, firma electronica, reconocimiento facial ni integracion con sistemas externos, salvo que se definan como ampliaciones futuras.

## 3. Actores del Sistema

### Usuario

Persona que ingresa sus datos en el formulario digital y recibe un codigo QR para su posterior validacion.

### Operador de Validacion

Persona encargada de escanear el codigo QR presentado por el usuario y verificar si el registro es valido.

### Administrador

Persona responsable de consultar registros, revisar historial de escaneos, administrar usuarios operadores y supervisar el funcionamiento general del sistema.

### Sistema de Base de Datos

Componente encargado de almacenar usuarios, registros, codigos QR, estados y eventos de escaneo.

### Servicio Generador de QR

Componente responsable de generar la representacion visual del codigo QR a partir de un token o identificador unico.

## 4. Requerimientos Funcionales

### RF1. Registro de usuario

El sistema debe permitir que un usuario registre sus datos mediante un formulario digital.

**Campos minimos requeridos:**

* Nombre completo.
* Correo electronico.
* Numero de identificacion.
* Telefono.

### RF2. Validacion de campos obligatorios

El sistema debe validar que los campos obligatorios no esten vacios antes de permitir el envio del formulario.

### RF3. Validacion de formato de correo

El sistema debe validar que el correo electronico ingresado tenga un formato correcto.

### RF4. Validacion de duplicidad

El sistema debe verificar que no exista un registro previo con el mismo correo electronico o numero de identificacion.

### RF5. Almacenamiento del registro

El sistema debe guardar los datos del usuario en la base de datos cuando la informacion sea valida.

### RF6. Generacion de identificador unico

El sistema debe generar un identificador unico para cada registro creado.

### RF7. Generacion de codigo QR

El sistema debe generar un codigo QR asociado al identificador unico del registro.

### RF8. Seguridad del contenido del QR

El codigo QR no debe contener directamente datos personales sensibles. Debe contener un token o identificador que permita consultar el registro desde el backend.

### RF9. Visualizacion del QR

El sistema debe mostrar el codigo QR generado al usuario despues de un registro exitoso.

### RF10. Descarga o captura del QR

El sistema debe permitir que el usuario guarde o descargue el codigo QR generado.

### RF11. Escaneo del QR

El sistema debe permitir al operador escanear el codigo QR usando una camara o lector compatible.

### RF12. Consulta del registro por QR

El sistema debe consultar en la base de datos el registro asociado al token obtenido del QR.

### RF13. Validacion del estado del QR

El sistema debe validar si el QR se encuentra en uno de los siguientes estados:

* Pendiente.
* Validado.
* Expirado.
* Cancelado.
* Invalido.

### RF14. Resultado de validacion

El sistema debe mostrar al operador un resultado claro despues del escaneo:

* QR valido.
* QR invalido.
* QR ya utilizado.
* QR expirado.
* Registro no encontrado.

### RF15. Actualizacion de estado

Cuando el QR sea valido y se escanee correctamente, el sistema debe actualizar el estado del registro a `Validado`.

### RF16. Registro de fecha y hora de escaneo

El sistema debe guardar la fecha y hora exacta del escaneo realizado.

### RF17. Registro del operador

El sistema debe guardar que operador realizo el escaneo, cuando el modulo de operadores este habilitado.

### RF18. Historial de escaneos

El sistema debe permitir consultar un historial de escaneos realizados.

### RF19. Consulta administrativa

El administrador debe poder consultar registros por nombre, correo, identificacion, estado o fecha.

### RF20. Manejo de errores

El sistema debe mostrar mensajes claros cuando ocurra un error de validacion, conexion o procesamiento.

## 5. Requerimientos No Funcionales

### RNF1. Usabilidad

La interfaz debe ser clara, simple y permitir que el usuario complete el registro sin instrucciones externas.

### RNF2. Rendimiento

La validacion de un QR debe completarse en un tiempo maximo de 2 segundos bajo condiciones normales de operacion.

### RNF3. Seguridad

El sistema no debe almacenar informacion sensible directamente en el codigo QR.

### RNF4. Integridad

El sistema debe mantener consistencia entre el usuario registrado, el codigo QR generado y el estado de validacion.

### RNF5. Disponibilidad

El sistema debe estar disponible durante los horarios definidos para registro y validacion.

### RNF6. Compatibilidad

El sistema debe funcionar en navegadores modernos y en dispositivos con camara compatible para escaneo QR.

### RNF7. Mantenibilidad

El codigo debe organizarse por modulos, separando registro, validacion, QR, escaneo, persistencia y administracion.

### RNF8. Trazabilidad

El sistema debe conservar evidencia de los eventos importantes, como creacion de registro, generacion de QR, escaneo exitoso e intento invalido.

### RNF9. Privacidad

Los datos personales deben utilizarse solo para los fines definidos del sistema.

### RNF10. Escalabilidad

El sistema debe permitir aumentar la cantidad de registros y escaneos sin afectar gravemente el rendimiento.

## 6. Reglas de Negocio

**RN1:** Un usuario no puede registrarse dos veces con el mismo correo electronico.

**RN2:** Un usuario no puede registrarse dos veces con el mismo numero de identificacion.

**RN3:** Cada registro debe tener un unico codigo QR asociado.

**RN4:** El QR debe estar asociado a un token unico generado por el sistema.

**RN5:** El QR no debe mostrar datos personales al ser leido directamente.

**RN6:** Un QR en estado `Validado` no debe poder validarse nuevamente como si fuera nuevo.

**RN7:** Un QR expirado debe ser rechazado por el sistema.

**RN8:** Un QR cancelado debe ser rechazado por el sistema.

**RN9:** Todo escaneo debe generar un evento de auditoria.

**RN10:** Solo usuarios autorizados deben poder acceder al modulo administrativo.

## 7. Estados del Registro y del QR

### Pendiente

Estado inicial despues de generar correctamente el QR. Indica que el usuario esta registrado, pero el QR aun no ha sido validado.

### Validado

Estado asignado despues de un escaneo exitoso. Indica que el QR fue presentado y aceptado por el sistema.

### Expirado

Estado asignado cuando el QR supera su fecha limite de uso.

### Cancelado

Estado asignado manualmente por un administrador cuando el registro ya no debe considerarse valido.

### Invalido

Estado utilizado cuando el token escaneado no corresponde a un registro existente o no cumple el formato esperado.

## 8. Validaciones del Formulario

El formulario debe validar como minimo:

* Nombre completo obligatorio.
* Correo electronico obligatorio.
* Formato valido del correo electronico.
* Identificacion obligatoria.
* Telefono obligatorio.
* Longitud minima y maxima de campos.
* Caracteres permitidos por campo.
* Duplicidad por correo electronico.
* Duplicidad por identificacion.

## 9. Mensajes Esperados del Sistema

### Registro exitoso

`Registro creado correctamente. Se ha generado su codigo QR.`

### Campos obligatorios faltantes

`Complete todos los campos obligatorios.`

### Correo invalido

`Ingrese un correo electronico valido.`

### Registro duplicado

`Ya existe un registro asociado a este correo o identificacion.`

### QR valido

`QR valido. Registro verificado correctamente.`

### QR ya utilizado

`QR ya utilizado anteriormente.`

### QR expirado

`QR expirado. No es posible validar el registro.`

### QR invalido

`QR invalido o registro no encontrado.`

## 10. Criterios de Aceptacion

### CA1. Registro correcto

Dado que el usuario completa todos los campos obligatorios con datos validos, cuando envia el formulario, entonces el sistema debe guardar el registro y generar un codigo QR unico.

### CA2. Rechazo por campos vacios

Dado que el usuario deja campos obligatorios vacios, cuando intenta enviar el formulario, entonces el sistema debe impedir el registro y mostrar un mensaje de validacion.

### CA3. Rechazo por duplicidad

Dado que ya existe un registro con el mismo correo o identificacion, cuando el usuario intenta registrarse, entonces el sistema debe rechazar el registro.

### CA4. Escaneo exitoso

Dado que un operador escanea un QR valido en estado pendiente, cuando el sistema consulta el token, entonces debe mostrar validacion exitosa y actualizar el estado a validado.

### CA5. Rechazo de QR ya utilizado

Dado que un QR ya fue validado, cuando se escanea nuevamente, entonces el sistema debe rechazarlo indicando que ya fue utilizado.

### CA6. Rechazo de QR inexistente

Dado que el operador escanea un QR con token desconocido, cuando el sistema lo consulta, entonces debe mostrar que el QR es invalido o que el registro no fue encontrado.

## 11. Prioridad de Implementacion

### Prioridad Alta

* Formulario de registro.
* Validacion de campos.
* Almacenamiento de datos.
* Generacion de QR.
* Escaneo y validacion de QR.
* Actualizacion de estado.

### Prioridad Media

* Historial de escaneos.
* Consulta administrativa.
* Registro del operador.
* Filtros por estado y fecha.

### Prioridad Baja

* Reportes exportables.
* Notificaciones por correo.
* Personalizacion visual del QR.
* Integraciones externas.

## 12. Supuestos y Restricciones

### Supuestos

* El usuario tendra acceso a internet o a la red donde este desplegado el sistema.
* El operador contara con un dispositivo con camara o lector QR.
* La base de datos estara disponible durante el proceso de registro y validacion.

### Restricciones

* El sistema debe proteger los datos personales.
* El QR debe usar un identificador seguro y no datos sensibles.
* La validacion depende de la disponibilidad del backend y la base de datos.
* El alcance inicial no incluye integraciones externas complejas.

## 13. Modulos Propuestos

### Modulo de Registro

Gestiona la captura, validacion y envio de datos del formulario.

### Modulo de Usuarios

Administra la informacion registrada de cada usuario.

### Modulo de QR

Genera, almacena y valida los tokens asociados a los codigos QR.

### Modulo de Escaneo

Permite leer el QR y enviar el token para validacion.

### Modulo de Validacion

Consulta el estado del registro y determina si el QR debe ser aceptado o rechazado.

### Modulo Administrativo

Permite consultar registros, revisar historial y administrar estados.

### Modulo de Auditoria

Registra eventos importantes del sistema para trazabilidad.
