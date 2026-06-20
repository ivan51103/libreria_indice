# Estructura del Proyecto de Ingeniería de Software

## 1. Planteamiento del Problema

En muchas organizaciones que requieren registrar personas, validar asistencia o controlar accesos, el proceso se realiza todavía mediante formularios físicos, hojas de cálculo o verificaciones manuales. Esto provoca retrasos, duplicidad de información, errores de captura y poca trazabilidad al momento de confirmar si una persona está registrada o autorizada.

**Contexto:**  
El problema ocurre en escenarios donde se necesita capturar datos de usuarios, visitantes, asistentes o clientes mediante un formulario y posteriormente validar su registro usando un código QR. Esto puede aplicarse en eventos, instituciones educativas, empresas, clínicas, sistemas de citas o controles de acceso.

**Diagnóstico:**  
Los procesos actuales presentan las siguientes deficiencias:

* Captura manual de datos con riesgo de errores.
* Validación lenta al momento de confirmar la identidad o registro de una persona.
* Dificultad para consultar rápidamente si un usuario ya fue registrado.
* Posibilidad de duplicar registros.
* Falta de evidencia digital sobre cuándo fue escaneado o validado un QR.
* Dependencia de documentos físicos o listas impresas.

**Pronóstico:**  
Si no se desarrolla esta solución, el proceso continuará siendo lento, poco confiable y propenso a errores. Además, será difícil controlar el estado real de cada registro, detectar accesos duplicados, consultar información en tiempo real y generar reportes confiables sobre los usuarios registrados o validados.

**Control:**  
El software propuesto resolverá estos inconvenientes mediante un sistema que permita capturar datos desde un formulario digital, validar la información ingresada, almacenar los registros en una base de datos, generar un código QR único para cada usuario y permitir su posterior escaneo para verificar el estado del registro. Esto mejorará la rapidez, seguridad, trazabilidad y confiabilidad del proceso.

## 2. Objetivos

### Objetivo General

Desarrollar un sistema de registro y validación mediante formulario digital y código QR, utilizando una aplicación web conectada a una base de datos, para optimizar el control, consulta y verificación de usuarios registrados en tiempo real.

### Objetivos Específicos

* Realizar el levantamiento de requerimientos funcionales y no funcionales del sistema.
* Diseñar la estructura de la base de datos para almacenar usuarios, registros, códigos QR y estados de validación.
* Implementar un formulario digital para capturar y validar los datos del usuario.
* Generar un código QR único asociado a cada registro creado en el sistema.
* Implementar un módulo de escaneo de QR para consultar y validar la información registrada.
* Registrar el estado del escaneo, fecha, hora y resultado de la validación.
* Validar la calidad del software mediante pruebas de caja negra, caja blanca y caja gris.

## 3. Determinación de la Metodología de Ingeniería de Software

### Metodología Seleccionada

Para este proyecto se utilizará una **metodología ágil basada en Scrum**, debido a que el sistema puede desarrollarse de forma incremental, permitiendo entregar módulos funcionales por etapas y realizar ajustes según las necesidades de los usuarios.

### Justificación

Scrum es adecuado para este proyecto porque permite dividir el desarrollo en entregas cortas y verificables. El sistema puede construirse por módulos, comenzando con el formulario de registro, luego la generación del QR, posteriormente el escaneo y finalmente la validación con reportes o historial.

Esta metodología facilita recibir retroalimentación temprana de los usuarios, corregir errores de diseño o funcionalidad y adaptar el sistema si cambian los campos del formulario, las reglas de validación o los criterios de acceso.

### Roles del Proyecto

* **Product Owner:** Define las necesidades del sistema, prioriza funcionalidades y valida que el producto cumpla los objetivos.
* **Scrum Master:** Supervisa el cumplimiento de la metodología y elimina obstáculos durante el desarrollo.
* **Equipo de Desarrollo:** Diseña, programa, prueba e implementa los módulos del sistema.
* **QA Engineer:** Diseña y ejecuta pruebas funcionales, técnicas y de integración.

### Propuesta de Sprints

* **Sprint 1:** Levantamiento de requerimientos, diseño de pantallas y modelo de base de datos.
* **Sprint 2:** Implementación del formulario de registro y validación de datos.
* **Sprint 3:** Generación del código QR y asociación con el registro del usuario.
* **Sprint 4:** Implementación del módulo de escaneo y consulta de datos.
* **Sprint 5:** Pruebas, corrección de errores, documentación y entrega final.

## 4. Determinación de Requerimientos

### A. Requerimientos Funcionales (RF)

Los requerimientos funcionales describen las operaciones que el sistema debe ejecutar, considerando entradas, procesos y salidas.

**RF1:** El sistema permitirá registrar usuarios mediante un formulario digital con campos obligatorios como nombre, correo electrónico, identificación y teléfono.

**RF2:** El sistema deberá validar que los campos obligatorios no estén vacíos antes de guardar el registro.

**RF3:** El sistema deberá validar que el correo electrónico tenga un formato correcto.

**RF4:** El sistema deberá evitar registros duplicados utilizando un identificador único, como número de documento o correo electrónico.

**RF5:** El sistema deberá almacenar los datos del usuario en una base de datos.

**RF6:** El sistema deberá generar un código QR único después de registrar correctamente al usuario.

**RF7:** El código QR deberá estar asociado a un identificador único del registro, evitando almacenar datos personales directamente dentro del QR.

**RF8:** El sistema deberá mostrar el código QR generado en pantalla para que el usuario pueda descargarlo o presentarlo.

**RF9:** El sistema deberá permitir escanear el código QR desde un dispositivo con cámara o lector compatible.

**RF10:** El sistema deberá consultar en la base de datos el registro asociado al QR escaneado.

**RF11:** El sistema deberá mostrar el resultado del escaneo, indicando si el QR es válido, inválido, expirado o ya utilizado.

**RF12:** El sistema deberá actualizar el estado del registro después de un escaneo exitoso.

**RF13:** El sistema deberá guardar la fecha y hora del escaneo realizado.

**RF14:** El sistema deberá permitir consultar el historial de registros y validaciones realizadas.

### B. Requerimientos No Funcionales (RNF)

Los requerimientos no funcionales describen atributos de calidad, restricciones técnicas y condiciones bajo las cuales debe operar el sistema.

**Usabilidad:**  
La interfaz debe ser clara e intuitiva, permitiendo completar el registro y obtener el QR en un flujo simple y directo.

**Rendimiento:**  
El sistema debe responder las consultas de validación de QR en un tiempo máximo de 2 segundos bajo condiciones normales de operación.

**Seguridad:**  
El QR no debe almacenar información personal sensible. Debe contener únicamente un identificador único o token que permita consultar los datos desde el backend.

**Integridad de Datos:**  
El sistema debe evitar registros duplicados y mantener consistencia entre el usuario registrado, el QR generado y el estado del escaneo.

**Disponibilidad:**  
El sistema debe estar disponible durante los horarios operativos definidos por la organización, especialmente en los periodos de validación o acceso.

**Compatibilidad:**  
La aplicación debe funcionar correctamente en navegadores web modernos y dispositivos con cámara para el escaneo del QR.

**Mantenibilidad:**  
El código debe organizarse por módulos, separando el formulario, la generación del QR, la validación, el escaneo y la conexión con la base de datos.

**Trazabilidad:**  
El sistema debe registrar eventos relevantes, como creación de usuario, generación del QR, escaneo exitoso, intento inválido y cambios de estado.

**Privacidad:**  
Los datos personales del usuario deben almacenarse de forma segura y utilizarse únicamente para los fines definidos por el sistema.

**Escalabilidad:**  
El sistema debe permitir aumentar la cantidad de usuarios registrados y escaneos sin afectar significativamente el rendimiento.
