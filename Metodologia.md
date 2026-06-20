# Metodología de Ingeniería de Software: Scrum

## 1. Metodología Seleccionada

Para el desarrollo del sistema de registro mediante formulario digital, generación de código QR y escaneo para validación, se selecciona la metodología **Scrum**, perteneciente al grupo de metodologías ágiles.

Scrum permite organizar el trabajo en ciclos cortos llamados **Sprints**, donde se desarrollan, prueban y entregan incrementos funcionales del sistema. Esta metodología es adecuada para proyectos donde pueden existir cambios durante el desarrollo, especialmente en los campos del formulario, reglas de validación, diseño de interfaz, estructura de datos o comportamiento del escaneo del QR.

## 2. Justificación de la Selección

Scrum se adapta mejor a este proyecto porque el sistema puede dividirse en módulos independientes y entregables:

* Registro de usuarios mediante formulario.
* Validación de datos ingresados.
* Almacenamiento de la información en base de datos.
* Generación de código QR único.
* Visualización o descarga del QR.
* Escaneo del código QR.
* Consulta de información asociada al QR.
* Actualización del estado del registro.
* Historial de validaciones.

Al usar Scrum, cada módulo puede desarrollarse de forma incremental, permitiendo probar avances parciales antes de construir todo el sistema. Esto reduce riesgos, facilita la detección temprana de errores y permite ajustar el proyecto según las necesidades reales del usuario.

## 3. Comparación con Otras Metodologías

### Scrum frente al Modelo en Cascada

El modelo en cascada requiere definir todos los requerimientos desde el inicio y avanzar por fases secuenciales: análisis, diseño, desarrollo, pruebas e implementación. Aunque puede ser útil cuando el proyecto está completamente definido, no es la mejor opción para este sistema, ya que podrían surgir cambios durante el desarrollo.

Por ejemplo, podrían modificarse los campos del formulario, las reglas para validar el QR o los estados del registro. Scrum permite manejar estos cambios de forma más flexible.

### Scrum frente a RUP

El Proceso Unificado Racional, conocido como RUP, es una metodología robusta orientada a casos de uso y arquitectura. Es útil para sistemas grandes, complejos y con altos niveles de documentación formal.

Sin embargo, para este proyecto, RUP puede resultar más pesado de lo necesario. El sistema requiere entregas rápidas, validación funcional temprana y ajustes continuos. Por eso, Scrum representa una alternativa más práctica y eficiente.

## 4. Aplicación de Scrum en el Proyecto

El proyecto se organizará en Sprints, cada uno con una duración estimada de una a dos semanas. Al finalizar cada Sprint, se deberá entregar una parte funcional del sistema que pueda ser revisada y probada.

El objetivo será construir el sistema por incrementos, comenzando desde las funcionalidades principales hasta completar la validación mediante QR.

## 5. Roles Scrum

### Product Owner

Es la persona responsable de definir las prioridades del sistema y validar que el producto cumpla con las necesidades del usuario final.

En este proyecto, el Product Owner define:

* Qué datos debe solicitar el formulario.
* Qué reglas de validación debe cumplir cada campo.
* Qué información se debe mostrar al escanear el QR.
* Qué estados puede tener un registro.
* Qué reportes o historial necesita el sistema.

### Scrum Master

Es la persona encargada de facilitar el trabajo del equipo y asegurar que se aplique correctamente la metodología Scrum.

Sus responsabilidades incluyen:

* Coordinar reuniones Scrum.
* Identificar obstáculos durante el desarrollo.
* Verificar que el equipo cumpla los objetivos de cada Sprint.
* Promover la comunicación entre los integrantes del proyecto.

### Equipo de Desarrollo

Es el grupo encargado de construir el sistema. Incluye perfiles como programadores, diseñadores, responsables de base de datos y testers.

Sus responsabilidades incluyen:

* Diseñar la interfaz del formulario.
* Implementar la lógica de validación.
* Crear la base de datos.
* Generar el código QR.
* Implementar el escaneo del QR.
* Ejecutar pruebas funcionales y técnicas.

### QA Engineer

Es responsable de validar la calidad del software mediante pruebas.

En este proyecto, el QA Engineer deberá:

* Diseñar casos de prueba para el formulario.
* Validar la generación correcta del QR.
* Comprobar el funcionamiento del escaneo.
* Ejecutar pruebas de caja negra, caja blanca y caja gris.
* Reportar defectos encontrados durante los Sprints.

## 6. Artefactos Scrum

### Product Backlog

Es la lista priorizada de funcionalidades, mejoras y tareas necesarias para construir el sistema.

Ejemplo de elementos del Product Backlog:

* Crear formulario de registro.
* Validar campos obligatorios.
* Evitar registros duplicados.
* Guardar datos en la base de datos.
* Generar código QR único.
* Mostrar QR al usuario.
* Escanear QR desde cámara.
* Consultar registro asociado al QR.
* Actualizar estado después del escaneo.
* Registrar fecha y hora de validación.

### Sprint Backlog

Es el conjunto de tareas seleccionadas del Product Backlog para desarrollarse durante un Sprint específico.

Ejemplo para un Sprint:

* Diseñar pantalla de formulario.
* Crear campos de nombre, correo, identificación y teléfono.
* Agregar validación de campos obligatorios.
* Guardar registros en base de datos.

### Incremento

Es el resultado funcional entregado al finalizar cada Sprint. Debe ser una parte útil y verificable del sistema.

Ejemplo de incremento:

* Al finalizar el Sprint 2, el sistema permite registrar usuarios correctamente desde un formulario y almacenarlos en la base de datos.

## 7. Propuesta de Sprints

### Sprint 1: Análisis y Diseño Inicial

**Objetivo:** Definir los requerimientos principales y diseñar la estructura inicial del sistema.

**Actividades:**

* Levantar requerimientos funcionales y no funcionales.
* Definir los campos del formulario.
* Diseñar el flujo general del sistema.
* Diseñar el modelo de base de datos.
* Crear prototipo inicial de la interfaz.

**Entregable:** Documento de requerimientos, diseño de flujo y modelo inicial de base de datos.

### Sprint 2: Formulario de Registro

**Objetivo:** Implementar el formulario de captura de datos.

**Actividades:**

* Crear pantalla de registro.
* Implementar campos obligatorios.
* Validar formato de correo electrónico.
* Validar duplicidad de usuarios.
* Guardar información en la base de datos.

**Entregable:** Formulario funcional que registra usuarios correctamente.

### Sprint 3: Generación de Código QR

**Objetivo:** Generar un código QR único asociado a cada registro.

**Actividades:**

* Crear identificador único para cada registro.
* Generar QR a partir del identificador.
* Asociar el QR con el usuario registrado.
* Mostrar el QR en pantalla.
* Permitir descargar o consultar el QR generado.

**Entregable:** Módulo funcional de generación y visualización de QR.

### Sprint 4: Escaneo y Validación del QR

**Objetivo:** Implementar el escaneo del QR y la validación del registro asociado.

**Actividades:**

* Integrar lectura de QR mediante cámara o lector.
* Consultar el registro asociado al código escaneado.
* Validar si el QR existe, está activo o ya fue utilizado.
* Mostrar resultado del escaneo.
* Actualizar el estado del registro.

**Entregable:** Módulo funcional de escaneo y validación de QR.

### Sprint 5: Pruebas, Correcciones y Entrega

**Objetivo:** Validar la calidad del sistema y corregir errores encontrados.

**Actividades:**

* Ejecutar pruebas de caja negra sobre el formulario y escaneo.
* Ejecutar pruebas de caja blanca sobre funciones críticas.
* Ejecutar pruebas de caja gris sobre integración entre interfaz, API y base de datos.
* Corregir defectos encontrados.
* Preparar documentación final.

**Entregable:** Sistema probado, corregido y documentado.

## 8. Reuniones Scrum

### Sprint Planning

Reunión al inicio de cada Sprint para seleccionar las tareas que se desarrollarán y definir el objetivo del Sprint.

### Daily Scrum

Reunión breve diaria donde cada integrante informa:

* Qué hizo desde la última reunión.
* Qué hará después.
* Qué obstáculos tiene.

### Sprint Review

Reunión al final del Sprint para presentar el incremento funcional desarrollado y recibir retroalimentación.

### Sprint Retrospective

Reunión interna del equipo para identificar qué funcionó bien, qué debe mejorar y qué acciones se tomarán en el siguiente Sprint.

## 9. Beneficios de Scrum para el Proyecto

El uso de Scrum ofrece los siguientes beneficios:

* Permite entregar funcionalidades por etapas.
* Facilita la adaptación ante cambios.
* Reduce riesgos al probar módulos desde etapas tempranas.
* Mejora la comunicación entre los integrantes del proyecto.
* Permite validar continuamente el formulario, el QR y el escaneo.
* Ayuda a detectar errores antes de la entrega final.
* Favorece una documentación alineada con el avance real del sistema.

## 10. Conclusión

La metodología Scrum es la más adecuada para este proyecto porque permite desarrollar el sistema de registro, generación de QR y escaneo de manera incremental, flexible y controlada. A través de Sprints, el equipo puede construir primero las funciones principales, probarlas, corregir errores y mejorar el producto con base en la retroalimentación recibida.

Esta metodología permite asegurar que el sistema final sea funcional, confiable, fácil de usar y alineado con las necesidades reales del usuario.
