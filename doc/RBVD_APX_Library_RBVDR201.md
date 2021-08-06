# ![logo-template](images/logo-template.png)
# Library RBVDR201

> El objetivo de este documento es proveer información relacionada a la librería RBVDR201 que utiliza la transacción RBVDT201 y que ha sido implementado en APX.

### 1. Funcionalidad:

> Esta librería APX tiene como objetivo consumir servicios tanto internos como externos (Rimac).

#### 1.1 Caso de Uso:

> El uso de la Librería RBVDR201 está orientado a consumir el servicio createInsurance del API insurances,
> el servicio createEmail del API notifications-gateway y el servicio de emisión de poliza de Rimac.

### 2. Capacidades:
> Esta **librería** brinda la capacidad de poder consumir los servicios mencionados de forma segura y fácil mediante los siguientes métodos:

#### 2.1 Método 1: executePrePolicyEmissionASO(DataASO requestBody)
> Método que consume el servicio ASO createInsurance, para recuperar el alta del seguro generada por Host.

##### 2.1.1 Datos de Entrada

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| DataASO | Object | Objeto que contiene el cuerpo de solicitud |
|1.1| quotationId | String | Cotización BBVA |
|1.2| productId | String | Código del producto |
|1.3| productPlan | Object | Objeto plan de producto |
|1.3.1| id | String | Código del plan |
|1.4| paymentMethod | Object | Objeto método de pago |
|1.4.1| paymentType | String | Tipo de pago |
|1.4.2| installmentFrequency | String | Frequencia de pago |
|1.4.3| relatedContracts | List | Lista de contratos relacionados |
|1.4.3.1| product | Object | Objeto producto |
|1.4.3.1.1| id | String | Código del producto |
|1.4.3.2| number | String | Cuenta donde se debitará |
|1.5| validityPeriod | Object | Objeto periodo de validez |
|1.5.1| startDate | Date | Fecha de inicio |
|1.6| totalAmount | Object | Objeto cantidad total |
|1.6.1| amount | Double | Monto |
|1.6.2| currency | String | Divisa |
|1.7| insuredAmount | Object | Objeto monto asegurado del seguro |
|1.7.1| amount | Double | Monto |
|1.7.2| currency | String | Divisa |
|1.8| installmentPlan | Object | Objeto periodo de pago |
|1.8.1| totalNumberInstallments | Long | Cantidad de pagos a realizar |
|1.8.2| period | Object | Objeto periodo de pago |
|1.8.2.1| id | String | Código del periodo de pago |
|1.8.3| startDate | Date | Fecha de inicio |
|1.8.4| paymentAmount | Object | Objeto cantidad a pagar |
|1.8.4.1| amount | Double | Monto |
|1.8.4.2| currency | String | Divisa |
|1.9| firstInstallment | Object | Objeto que contiene información del primer pago |
|1.9.1| isPaymentRequired | Boolean | Atributo que determina si el primero pago es requerido o no |
|1.10| participants | List | Lista de participantes |
|1.10.1| participantType | Object | Objeto tipo de participante |
|1.10.1.1| id | String | Id del participante |
|1.10.2| customerId | String | Id cliente del participante |
|1.10.3| identityDocument | Object | Objeto que tiene datos sobre el documento de identidad |
|1.10.3.1| documentType | Object | Objeto que tiene datos del tipo de documento |
|1.10.3.1.1| id | String | Id del tipo de documento |
|1.10.3.2| number | String | Número de documento |
|1.11| businessAgent | Object | Objeto que contiene información del gestor |
|1.11.1| id | String | Código del gestor |
|1.12| promoter | Object | Objeto que contiene información del presentador |
|1.12.1| id | String | Código del presentador |
|1.13| insuranceCompany | Object | Objeto que contiene información de la compañia aseguradora |
|1.13.1| id | String | Id de la compañia aseguradora |
|1.14| bank | Object | Objeto banco |
|1.14.1| id | String | Id banco |
|1.14.2| branch | Object | Objeto código de oficina |
|1.14.2.1| id | String | Id código de oficina |
|1.15| holder | Object | Objeto que contiene datos del titular |
|1.15.1| id | String | Id del titular |
|1.15.2| identityDocument | Object | Objeto que tiene datos sobre el documento de identidad |
|1.15.2.1| documentType | Object | Objeto que tiene datos del tipo de documento |
|1.15.2.1.1| id | String | Id del tipo de documento |
|1.15.2.2| number | String | Número de documento |

##### 2.1.2 Datos de Salida

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| PolicyASO | Object | Objeto que contiene la respuesta del servicio |
|1.1| id | String | Id de la poliza |
|1.2| policyNumber | String | Número de poliza |
|1.3| productId | String | Código del producto |
|1.4| productDescription | String | Descripción del producto |
|1.5| productPlan | Object | Objeto plan de producto |
|1.5.1| id | String | Código del plan |
|1.5.2| description | String | Descripción del producto |
|1.6| paymentMethod | Object | Objeto método de pago |
|1.6.1| paymentType | String | Tipo de pago |
|1.6.2| installmentFrequency | String | Frequencia de pago |
|1.6.3| relatedContracts | List | Lista de contratos relacionados |
|1.6.3.1| product | Object | Objeto producto |
|1.6.3.1.1| id | String | Código del producto |
|1.6.3.2| number | String | Cuenta donde se debitará |
|1.7| operationDate | Date | Fecha de la operación |
|1.8| validityPeriod | Object | Objeto periodo de validez |
|1.8.1| startDate | Date | Fecha de inicio |
|1.8.2| endDate | Date | Fecha de fin |
|1.9| totalAmount | Object | Objeto cantidad total |
|1.9.1| amount | Double | Monto |
|1.9.2| currency | String | Divisa |
|1.9.3| exchangeRate | Object | Tipo de cambio para la prima asegurada |
|1.9.3.1| date | Object | Fecha de cambio |
|1.9.3.2| baseCurrency | Object | Divisa de inicio |
|1.9.3.3| targetCurrency | Object | Divisa objetivo |
|1.9.3.4| detail | Object | Objeto que contiene el detalle del tipo de cambio |
|1.9.3.4.1| factor | Object | Objeto factor del tipo de cambio |
|1.9.3.4.1.1| value | Double | Valor al tipo de cambio |
|1.9.3.4.1.2| ratio | Double | Ratio del tipo de cambio |
|1.9.3.4.2| priceType | String | Tipo de cambio |
|1.10| insuredAmount | Object | Objeto monto asegurado del seguro |
|1.10.1| amount | Double | Monto |
|1.10.2| currency | String | Divisa |
|1.11| holder | Object | Objeto que contiene datos del titular |
|1.11.1| id | String | Id del titular |
|1.11.2| identityDocument | Object | Objeto que tiene datos sobre el documento de identidad |
|1.11.2.1| documentType | Object | Objeto que tiene datos del tipo de documento |
|1.11.2.1.1| id | String | Id del tipo de documento |
|1.11.2.1.2| description | String | Nombre del tipo de documento |
|1.11.2.2| number | String | Número de documento |
|1.12| relatedContracts | List | Lista de contratos relacionados |
|1.12.1| relationType | Object | Objeto tipo de relación |
|1.12.1.1| id | String | Código del tipo de relación |
|1.12.2| contractDetails | Object | Objeto detalles del contrato |
|1.12.2.1| contractType | String | Tipo de contrato |
|1.12.2.2| contractId | String | Código del contrato |
|1.12.2.3| number | String | Número del contrato |
|1.13| installmentPlan | Object | Objeto periodo de pago |
|1.13.1| totalNumberInstallments | Long | Cantidad de pagos a realizar |
|1.13.2| period | Object | Objeto periodo de pago |
|1.13.2.1| id | String | Código del periodo de pago |
|1.13.2.2| name | String | Nombre del periodo de pago |
|1.13.3| startDate | Date | Fecha de inicio |
|1.13.4| maturityDate | Date | Fecha fin de vigencia |
|1.13.5| paymentAmount | Object | Objeto cantidad a pagar |
|1.13.5.1| amount | Double | Monto |
|1.13.5.2| currency | String | Divisa |
|1.13.6| exchangeRate | Object | Tipo de cambio para la prima asegurada |
|1.13.6.1| date | Object | Fecha de cambio |
|1.13.6.2| baseCurrency | Object | Divisa de inicio |
|1.13.6.3| targetCurrency | Object | Divisa objetivo |
|1.13.6.4| detail | Object | Objeto que contiene el detalle del tipo de cambio |
|1.13.6.4.1| factor | Object | Objeto factor del tipo de cambio |
|1.13.6.4.1.1| value | Double | Valor al tipo de cambio |
|1.13.6.4.1.2| ratio | Double | Ratio del tipo de cambio |
|1.13.6.4.2| priceType | String | Tipo de cambio |
|1.14| firstInstallment | Object | Objeto que contiene información del primer pago |
|1.14.1| firstPaymentDate | Date | Atributo que determina si el primero pago es requerido o no |
|1.14.2| isPaymentRequired | Boolean | Atributo que determina si el primero pago es requerido o no |
|1.14.3| paymentAmount | Object | Atributo que determina si el primero pago es requerido o no |
|1.14.3.1| amount | Double | Monto |
|1.14.3.2| currency | String | Divisa |
|1.14.4| exchangeRate | Object | Atributo que determina si el primero pago es requerido o no |
|1.14.4.1| date | Object | Fecha de cambio |
|1.14.4.2| baseCurrency | Object | Divisa de inicio |
|1.14.4.3| targetCurrency | Object | Divisa objetivo |
|1.14.4.4| detail | Object | Objeto que contiene el detalle del tipo de cambio |
|1.14.4.4.1| factor | Object | Objeto factor del tipo de cambio |
|1.14.4.4.1.1| value | Double | Valor al tipo de cambio |
|1.14.4.4.1.2| ratio | Double | Ratio del tipo de cambio |
|1.14.4.4.2| priceType | String | Tipo de cambio |
|1.14.5| operationNumber | String | Atributo que determina si el primero pago es requerido o no |
|1.14.6| transactionNumber | String | Atributo que determina si el primero pago es requerido o no |
|1.14.7| operationDate | Date | Atributo que determina si el primero pago es requerido o no |
|1.15| participants | List | Lista de participantes |
|1.15.1| id | String | Código del participante |
|1.15.2| identityDocument | Object | Objeto que tiene datos sobre el documento de identidad |
|1.15.2.1| documentType | Object | Objeto que tiene datos del tipo de documento |
|1.15.2.1.1| id | String | Id del tipo de documento |
|1.15.2.2| number | String | Número de documento |
|1.15.1| participantType | Object | Objeto tipo de participante |
|1.15.1.1| id | String | Id del participante |
|1.15.3| customerId | String | Id cliente del participante |
|1.15.4| relationship | Object | Objeto relación |
|1.15.4.1| id | String | Código de relación |
|1.15.4.2| description | String | Descripción de relación |
|1.16| businessAgent | Object | Objeto que contiene información del gestor |
|1.16.1| id | String | Código del gestor |
|1.16.2| fullName | String | Nombre completo del gestor |
|1.17| promoter | Object | Objeto que contiene información del presentador |
|1.17.1| id | String | Código del presentador |
|1.17.2| fullName | String | Nombre completo del presentador |
|1.18| insuranceCompany | Object | Objeto que contiene información de la compañia aseguradora |
|1.18.1| id | String | Id de la compañia aseguradora |
|1.18.2| name | String | Nombre de la compañia aseguradora |
|1.19| externalQuotationId | String | Código de la cotización de Rimac |
|1.20| externalPolicyNumber | String | Número de poliza de Rimac |
|1.21| status | Object | Objeto estado de la poliza |
|1.21.1| id | String | Código del estado |
|1.21.2| description | String | Descripción del estado |
|1.22| bank | Object | Objeto banco |
|1.22.1| id | String | Id banco |
|1.22.2| branch | Object | Objeto código de oficina |
|1.22.2.1| id | String | Id código de oficina |

##### 2.1.3 Ejemplo
```java
PolicyASO output = rbvdR201.executePrePolicyEmissionASO(DataASO input);
```

#### 2.2 Método 2: executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId)
> Método para generar la poliza por el lado de Rimac

##### 2.2.1 Datos de Entrada

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| EmisionBO | Object | Objeto que contiene el cuerpo de solicitud |
|1.1| payload | Object | Objeto que contiene información del asegurado, flags y datos adicionales |
|1.1.1| contactoInspeccion | Object | Lista de objetos para la confiuración de la solicitud |
|1.1.1.1| nombre | String | Nombre del asegurado |
|1.1.1.2| correo | String | Correo del asegurado |
|1.1.1.3| telefono | String | Telefono del asegurado |
|1.1.2| datosParticulares | List | Lista que contiene todos datos adicionales del asegurado |
|1.1.2.1| etiqueta | String | Nombre del dato particular |
|1.1.2.2| codigo | String | En algunos objetos es necesario enviar el valor en este campo, si no es necesario se envía "" |
|1.1.2.3| valor | String | Valor del dato particular, si se envía el código, este campo no se envía |
|1.1.3| envioElectronico | String | Indicador envío de correo |
|1.1.4| indCobro | String | Indicador de cobro |
|1.1.5| indInspeccion | Long | Indicador inspección |
|1.1.6| indValidaciones | String | Indicador de validaciones |
|2| quotationId | String | Código de cotización de Rimac |
|3| traceId | String | Código de la trama |

##### 2.2.2 Datos de Salida

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| EmisionBO | Object | Objeto que contiene la respuesta del servicio |
|1.1| payload | Object | Datos de la respuesta del servicio |
|1.1.1| cotizacion | String | Código de la cotización |
|1.1.2| indicadorRequierePago | Long | Indicador requiere pago |
|1.1.3| codProducto | String | Código del producto |
|1.1.4| numeroPoliza | String | Número de poliza |
|1.1.5| primaNeta | Double | Valor de prima neta |
|1.1.6| primaBruta | Double | Valor de prima bruta |
|1.1.7| indicadorInspeccion | Long | Indicador requiere inspección |
|1.1.8| indicadorGps | Long | Indicador gps |
|1.1.9| envioElectronico | String | Indicador envío de correo |
|1.1.10| financiamiento | Long | Código de financiamiento |
|1.1.11| numeroCuotas | Long | Cantidad de cuotas a pagar |
|1.1.12| fechaInicio | Date | Fecha de inicio del seguro |
|1.1.13| fechaFinal | Date | Fecha final del seguro |
|1.1.14| cuotasFinanciamiento | List | Lista que contiene los datos de las cuotas a pagar |
|1.1.14.1| cuota | Integer | Número de cuota |
|1.1.14.2| monto | BigDecimal | Monto a pagar |
|1.1.14.3| fechaVencimiento | Date | Fecha de vencimiento de la cuota |
|1.1.14.4| moneda | String | Tipo de moneda |
|1.1.15| contratante | Object | Objeto que contiene información del contratante |
|1.1.15.1| tipoDocumento | String | Tipo de documento |
|1.1.15.2| numeroDocumento | String | Numero de documento |
|1.1.15.3| apellidoPaterno | String | Apellido paterno |
|1.1.15.4| apellidoMaterno | String | Apellido materno |
|1.1.15.5| nombres | String | Nombres |
|1.1.15.6| sexo | String | Sexo |
|1.1.15.7| fechaNacimiento | Date | Fecha de nacimiento |
|1.1.15.8| ubigeo | String | Ubicación |
|1.1.15.9| nombreDistrito | String | Nombre del distrito |
|1.1.15.10| nombreProvincia | String | Nombre de la provincia |
|1.1.15.11| nombreDepartamento | String | Nombre del departamento |
|1.1.15.12| nombreVia | String | Nombre de la vía |
|1.1.15.13| numeroVia | String | Número de la vía |
|1.1.15.14| correo | String | Correo |
|1.1.15.15| telefono | String | Telefono |
|1.1.16| responsablePago | Object | Objeto que contiene información del responsable de pago |
|1.1.16.1| tipoDocumento | String | Tipo de documento |
|1.1.16.2| numeroDocumento | String | Numero de documento |
|1.1.16.3| apellidoPaterno | String | Apellido paterno |
|1.1.16.4| apellidoMaterno | String | Apellido materno |
|1.1.16.5| nombres | String | Nombres |
|1.1.16.6| sexo | String | Sexo |
|1.1.16.7| fechaNacimiento | Date | Fecha de nacimiento |
|1.1.16.8| ubigeo | String | Ubicación |
|1.1.16.9| nombreDistrito | String | Nombre del distrito |
|1.1.16.10| nombreProvincia | String | Nombre de la provincia |
|1.1.16.11| nombreDepartamento | String | Nombre del departamento |
|1.1.16.12| nombreVia | String | Nombre de la vía |
|1.1.16.13| numeroVia | String | Número de la vía |
|1.1.16.14| correo | String | Correo |
|1.1.16.15| telefono | String | Telefono |
|1.1.17| asegurado | Object | Objeto que contiene información del asegurado |
|1.1.17.1| tipoDocumento | String | Tipo de documento |
|1.1.17.2| numeroDocumento | String | Numero de documento |
|1.1.17.3| apellidoPaterno | String | Apellido paterno |
|1.1.17.4| apellidoMaterno | String | Apellido materno |
|1.1.17.5| nombres | String | Nombres |
|1.1.17.6| sexo | String | Sexo |
|1.1.17.7| fechaNacimiento | Date | Fecha de nacimiento |
|1.1.17.8| ubigeo | String | Ubicación |
|1.1.17.9| nombreDistrito | String | Nombre del distrito |
|1.1.17.10| nombreProvincia | String | Nombre de la provincia |
|1.1.17.11| nombreDepartamento | String | Nombre del departamento |
|1.1.17.12| nombreVia | String | Nombre de la vía |
|1.1.17.13| numeroVia | String | Número de la vía |
|1.1.17.14| correo | String | Correo |
|1.1.17.15| telefono | String | Telefono |

##### 2.2.3 Ejemplo
```java
EmisionBO output = rbvdR201.executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId);
```

#### 2.3. Método 3: executeCreateEmail(CreateEmailASO createEmailASO)
> Método para realizar una petición al servicio createEmail del API notifications-gateway

##### 2.3.1 Datos de Entrada

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| CreateEmailASO | Object | Objeto que envuelve todos los datos necesarios para el cuerpo de solicitud |
|1.1| applicationId | String | Código de la aplicación |
|1.2| recipient | String | Correo o correos de las personas a envíar el mensaje |
|1.3| subject | String | Asunto del mensaje |
|1.4| body | String | Datos para completar el cuerpo del mensaje |
|1.5| sender | String | Valor "procesos@bbva.com.pe" por defecto |

##### 2.3.2 Datos de Salida

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| httpStatus | Integer | Código de estado http retornado |

##### 2.3.3 Ejemplo
```java
Integer httpStatusEmail = rbvdR201.executeCreateEmail(CreateEmailASO requestBody);
```

### 3.  Mensajes: No hay mensajes, solo se capturan excepciones para procesarlas en la librería de lógica de negocio.

### 4.  Versiones:
#### 4.1  Versión 0.1.0-SNAPSHOT

+ Versión 0.1.0-SNAPSHOT: Esta versión permite consumir los servicios mencionados, 1 interno y 1 externo de Rimac.