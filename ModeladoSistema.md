# Modelado del Sistema de Registro y Validación por QR

## 1. Caso de Uso

### Caso de Uso: Registrar Usuario y Validar Código QR

**Nombre:** Registrar usuario y validar código QR.

**Actor principal:** Usuario.

**Actores secundarios:**

* Operador de validación.
* Sistema de base de datos.
* Servicio generador de QR.

**Descripción:**  
El usuario ingresa sus datos en un formulario digital. El sistema valida la información, guarda el registro en la base de datos y genera un código QR único asociado al usuario. Posteriormente, el operador escanea el QR para verificar si el registro es válido y actualizar su estado.

**Precondiciones:**

* El sistema debe estar disponible.
* El formulario de registro debe estar habilitado.
* La base de datos debe estar operativa.
* El dispositivo de validación debe contar con cámara o lector de QR.

**Flujo principal:**

1. El usuario accede al formulario de registro.
2. El sistema muestra los campos requeridos.
3. El usuario ingresa sus datos personales.
4. El usuario envía el formulario.
5. El sistema valida que los campos obligatorios estén completos.
6. El sistema valida que el correo tenga un formato correcto.
7. El sistema verifica que el usuario no esté duplicado.
8. El sistema guarda el registro en la base de datos.
9. El sistema genera un identificador único para el registro.
10. El sistema genera un código QR asociado al identificador.
11. El sistema muestra el QR al usuario.
12. El usuario presenta el QR al operador.
13. El operador escanea el QR.
14. El sistema consulta el registro asociado.
15. El sistema valida el estado del QR.
16. El sistema muestra el resultado de la validación.
17. El sistema actualiza el estado del registro como validado.

**Flujos alternativos:**

* **A1 - Datos incompletos:** Si el usuario deja campos obligatorios vacíos, el sistema muestra un mensaje de error y no guarda el registro.
* **A2 - Correo inválido:** Si el correo no tiene formato válido, el sistema solicita corregir el campo.
* **A3 - Usuario duplicado:** Si el correo o identificación ya existe, el sistema rechaza el registro y muestra una alerta.
* **A4 - QR inválido:** Si el QR escaneado no existe en la base de datos, el sistema muestra el mensaje "QR inválido".
* **A5 - QR ya utilizado:** Si el QR ya fue validado previamente, el sistema muestra el mensaje "QR ya utilizado".

**Postcondiciones:**

* El usuario queda registrado en la base de datos.
* El QR queda asociado a un identificador único.
* El estado del registro se actualiza después de una validación exitosa.
* El sistema conserva la fecha y hora del escaneo.

**Resultado esperado:**  
El usuario se registra correctamente, recibe un código QR válido y el operador puede escanearlo para confirmar la autenticidad del registro.

## 2. Diagrama de Caso de Uso

```mermaid
flowchart LR
    Usuario[Usuario]
    Operador[Operador de validacion]
    BD[(Base de datos)]
    QR[Servicio generador de QR]

    UC1((Completar formulario))
    UC2((Validar datos))
    UC3((Guardar registro))
    UC4((Generar codigo QR))
    UC5((Mostrar QR))
    UC6((Escanear QR))
    UC7((Consultar registro))
    UC8((Validar estado del QR))
    UC9((Actualizar estado))

    Usuario --> UC1
    UC1 --> UC2
    UC2 --> UC3
    UC3 --> BD
    UC3 --> UC4
    UC4 --> QR
    UC4 --> UC5
    Usuario --> UC5

    Operador --> UC6
    UC6 --> UC7
    UC7 --> BD
    UC7 --> UC8
    UC8 --> UC9
    UC9 --> BD
```

## 3. Diagrama de Secuencia

```mermaid
sequenceDiagram
    actor Usuario
    participant Formulario as Interfaz de Registro
    participant Backend as API / Backend
    participant BD as Base de Datos
    participant QR as Servicio QR
    actor Operador
    participant Scanner as Interfaz de Escaneo

    Usuario->>Formulario: Ingresa datos personales
    Formulario->>Backend: Envia datos del formulario
    Backend->>Backend: Valida campos obligatorios y formato
    Backend->>BD: Verifica duplicidad por correo o identificacion
    BD-->>Backend: Retorna resultado de busqueda

    alt Datos validos y usuario no duplicado
        Backend->>BD: Guarda nuevo registro
        BD-->>Backend: Retorna ID del registro
        Backend->>QR: Solicita generacion de QR con token unico
        QR-->>Backend: Retorna codigo QR generado
        Backend-->>Formulario: Retorna registro creado y QR
        Formulario-->>Usuario: Muestra QR generado
    else Datos invalidos o usuario duplicado
        Backend-->>Formulario: Retorna mensaje de error
        Formulario-->>Usuario: Muestra error de validacion
    end

    Usuario->>Operador: Presenta codigo QR
    Operador->>Scanner: Escanea QR
    Scanner->>Backend: Envia token del QR
    Backend->>BD: Consulta registro asociado al token
    BD-->>Backend: Retorna datos y estado del registro

    alt QR valido y pendiente
        Backend->>BD: Actualiza estado a validado
        Backend-->>Scanner: Retorna validacion exitosa
        Scanner-->>Operador: Muestra acceso valido
    else QR invalido, expirado o ya utilizado
        Backend-->>Scanner: Retorna rechazo de validacion
        Scanner-->>Operador: Muestra motivo del rechazo
    end
```

## 4. Diagrama de Clases

```mermaid
classDiagram
    class Usuario {
        +int id
        +string nombre
        +string correo
        +string identificacion
        +string telefono
        +Date fechaRegistro
        +registrar()
        +actualizarDatos()
    }

    class Registro {
        +int id
        +string estado
        +Date fechaCreacion
        +Date fechaValidacion
        +crearRegistro()
        +marcarComoValidado()
        +rechazarRegistro()
    }

    class CodigoQR {
        +int id
        +string token
        +Date fechaGeneracion
        +Date fechaExpiracion
        +string estado
        +generar()
        +validar()
        +expirar()
    }

    class Escaneo {
        +int id
        +Date fechaEscaneo
        +string resultado
        +string observacion
        +registrarEscaneo()
    }

    class Operador {
        +int id
        +string nombre
        +string usuario
        +escanearQR()
    }

    class FormularioRegistro {
        +validarCampos()
        +enviarDatos()
        +mostrarErrores()
    }

    class ServicioQR {
        +generarToken()
        +generarImagenQR()
        +leerToken()
    }

    class ServicioValidacion {
        +validarDuplicidad()
        +validarFormatoCorreo()
        +validarEstadoQR()
        +actualizarEstadoRegistro()
    }

    Usuario "1" --> "1" Registro : posee
    Registro "1" --> "1" CodigoQR : genera
    CodigoQR "1" --> "0..*" Escaneo : registra
    Operador "1" --> "0..*" Escaneo : realiza
    FormularioRegistro --> ServicioValidacion : usa
    ServicioValidacion --> Registro : valida
    ServicioQR --> CodigoQR : crea
```

## 5. Diagrama de Contexto

```mermaid
flowchart TB
    Usuario[Usuario]
    Operador[Operador de validacion]
    Admin[Administrador]

    Sistema[[Sistema de Registro y Validacion por QR]]

    BD[(Base de Datos)]
    Camara[Camara o lector QR]
    ServicioQR[Servicio generador de QR]

    Usuario -->|Ingresa datos en formulario| Sistema
    Sistema -->|Muestra codigo QR| Usuario

    Operador -->|Solicita validacion por escaneo| Sistema
    Camara -->|Captura token QR| Sistema
    Sistema -->|Muestra resultado de validacion| Operador

    Admin -->|Consulta registros e historial| Sistema
    Sistema -->|Entrega reportes y estados| Admin

    Sistema -->|Guarda y consulta registros| BD
    BD -->|Retorna datos de usuarios y estados| Sistema

    Sistema -->|Solicita generacion de QR| ServicioQR
    ServicioQR -->|Retorna QR generado| Sistema
```

## 6. Resumen del Flujo General

El sistema inicia cuando el usuario llena el formulario de registro. Después, el backend valida los datos, evita duplicados y guarda la información. Una vez creado el registro, se genera un código QR único que se entrega al usuario. En la etapa de validación, el operador escanea el QR, el sistema consulta la base de datos, verifica el estado del registro y muestra si el QR es válido, inválido, expirado o ya utilizado.
