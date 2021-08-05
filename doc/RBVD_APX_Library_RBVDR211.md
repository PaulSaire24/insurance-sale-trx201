# ![logo-template](images/logo-template.png)
# Library RBVDR211

> El objetivo de este documento es proveer información relacionada a la librería RBVDR211 que utiliza la transacción RBVDT201 y que ha sido implementado en APX.

### 1. Funcionalidad:
> Esta Librería APX tiene como objetivo realizar la lógica de negocio de la transacción RBVDT201.

#### 1.1 Caso de Uso:

> El uso de la Librería RBVDR211 está orientado a realizar los mapeos de los campos de salida de la transacción, realizar validaciones, todo lo necesario para cumplir con la lógica de negocio.

### 2. Capacidades:

> Esta **librería** brinda la capacidad de poder ejecutar la lógica de negocio de la transacción de alta de poliza (RBVDT201) de forma fácil y segura con el siguiente método:

#### 2.1 Método 1: executeBusinessLogicEmissionPrePolicy(PolicyDTO input)
> Método que ejecuta toda la lógica de negocio

##### 2.1.1 Datos de Entrada

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| PolicyDTO | Object | Objeto que envuelve los datos del cuerpo de solicitud |
|1.1| quotationId | String | Cotización BBVA |
|1.2| productId | String | Código del producto |
|1.3| productPlan | Object | Objeto plan de producto |
|1.3.1| id | String | Código del plan |
|1.4| paymentMethod | Object | Objeto método de pago |
|1.4.1| paymentType | String | Tipo de pago |
|1.4.2| installmentFrequency | String | Frequencia de pago |
|1.4.3| relatedContracts | List | Lista de contratos relacionados |
|1.4.3.1| contractId | String | Identificador del contrato |
|1.4.3.2| number | String | Cuenta donde se debitará |
|1.4.3.3| product | Object | Objeto producto |
|1.4.3.3.1| id | String | Código del producto |
|1.5| validityPeriod | Object | Objeto periodo de validez |
|1.5.1| startDate | Date | Fecha de inicio |
|1.6| totalAmount | Object | Objeto cantidad total |
|1.6.1| amount | Double | Monto |
|1.6.2| currency | String | Divisa |
|1.7| insuredAmount | Object | Objeto monto asegurado del seguro |
|1.7.1| amount | Double | Monto |
|1.7.2| currency | String | Divisa |
|1.8| isDataTreatment | Boolean | Indica si el cliente aceptó el tratamiento de datos |
|1.9| holder | Object | Objeto que contiene datos del titular |
|1.9.1| id | String | Id del titular |
|1.9.2| identityDocument | Object | Objeto que tiene datos sobre el documento de identidad |
|1.9.2.1| documentType | Object | Objeto que tiene datos del tipo de documento |
|1.9.2.1.1| id | String | Id del tipo de documento |
|1.9.2.2| number | String | Número de documento |
|1.9.3| contactDetails | List | Lista que contiene los detalles de contacto del titular |
|1.9.3.1| contact | Object | Objeto contacto |
|1.9.3.1.1| contactDetailType | String | Tipo detalle de contacto |
|1.9.3.1.2| address | String | Dirección |
|1.9.3.1.3| phoneNumber | String | Número de celular |
|1.10| relatedContracts | List | Lista de contratos relacionados |
|1.10.1| relationType | Object | Objeto tipo de relación |
|1.10.1.1| id | String | Código del tipo de relación |
|1.10.2| contractDetails | Object | Objeto detalles del contrato |
|1.10.2.1| contractType | String | Tipo de contrato |
|1.10.2.2| contractId | String | Código del contrato |
|1.10.2.3| number | String | Número del contrato |
|1.10.2.4| numberType | Object | Objeto tipo de número |
|1.10.2.4.1| id | String | Código tipo de número |
|1.11| installmentPlan | Object | Objeto periodo de pago |
|1.11.1| startDate | Date | Fecha de inicio |
|1.11.2| maturityDate | Date | Fecha de fin de vigencia |
|1.11.3| totalNumberInstallments | Long | Cantidad de pagos a realizar |
|1.11.4| period | Object | Objeto periodo de pago |
|1.11.4.1| id | String | Código del periodo de pago |
|1.11.5| paymentAmount | Object | Objeto cantidad a pagar |
|1.11.5.1| amount | Double | Monto |
|1.11.5.2| currency | String | Divisa |
|1.12| hasAcceptedContract | Boolean | Indicador acepta documentación contractual |
|1.13| inspection | Object | Objeto inspección |
|1.13.1| isRequired | Boolean | Indicador de inspección de bienes |
|1.13.2| fullName | Object | Nombre completo del inspeccionado |
|1.13.3| contactDetails | List | Lista que contiene los detalles de contacto del inspeccionado |
|1.13.3.1| contact | Object | Objeto contacto |
|1.13.3.1.1| contactDetailType | String | Tipo detalle de contacto |
|1.13.3.1.2| address | String | Dirección |
|1.13.3.1.3| phoneNumber | String | Número de celular |
|1.14| firstInstallment | Object | Objeto que contiene información del primer pago |
|1.14.1| isPaymentRequired | Boolean | Atributo que determina si el primero pago es requerido o no |
|1.14.2| paymentAmount | Object | Objeto cantidad a pagar |
|1.14.2.1| amount | Double | Monto |
|1.14.2.2| currency | String | Divisa |
|1.15| participants | List | Lista de participantes |
|1.15.1| participantType | Object | Objeto tipo de participante |
|1.15.1.1| fullName | String | Nombre completo del participante |
|1.15.2| identityDocument | Object | Objeto que tiene datos sobre el documento de identidad |
|1.15.2.1| documentType | Object | Objeto que tiene datos del tipo de documento |
|1.15.2.1.1| id | String | Id del tipo de documento |
|1.15.2.2| number | String | Número de documento |
|1.15.3| participantType | Object | Objeto tipo de participante |
|1.15.3.1| id | String | Id del participante |
|1.15.4| customerId | String | Id cliente del participante |
|1.15.5| startDate | Date | Fecha de inicio de participación |
|1.15.6| endDate | String | Fecha fin de participacion |
|1.15.7| relationship | Object | Objeto relación |
|1.15.7.1| id | String | Código de relación |
|1.15.8| benefitPercentage | Double | - |
|1.15.9| benefitAmount | Object | Objeto monto beneficiado |
|1.15.9.1| amount | Double | Monto del endoso |
|1.15.9.2| currency | String | Divisa del endoso |
|1.16| businessAgent | Object | Objeto que contiene información del gestor |
|1.16.1| id | String | Código del gestor |
|1.17| promoter | Object | Objeto que contiene información del presentador |
|1.17.1| id | String | Código del presentador |
|1.18| bank | Object | Objeto banco |
|1.18.1| id | String | Id banco |
|1.18.2| branch | Object | Objeto código de oficina |
|1.18.2.1| id | String | Id código de oficina |
|1.19| identityVerificationCode | String | Código verificación de identidad |
|1.20| insuranceCompany | Object | Objeto que contiene información de la compañia aseguradora |
|1.20.1| id | String | Id de la compañia aseguradora |

##### 2.1.2 Datos de Salida

|#|Nombre del Atributo|Tipo de Dato| Descripción|
| :----|:---------- |:--------------| :-----|
|1| PolicyDTO | Object | Objeto que contiene la respuesta de la transacción |
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
|1.6.3.1| contractId | String | Identificador del contrato |
|1.6.3.2| number | String | Cuenta donde se debitará |
|1.6.3.3| product | Object | Objeto producto |
|1.6.3.3.1| id | String | Código del producto |
|1.6.3.3.2| name | String | Nombre del producto |
|1.7| operationDate | Date | Fecha de la operación |
|1.8| validityPeriod | Object | Objeto periodo de validez |
|1.8.1| startDate | Date | Fecha de inicio |
|1.8.2| endDate | Date | Fecha de fin |
|1.9| links | List | Lista de links de documentos |
|1.9.1| href | String | Url del documento de poliza |
|1.9.2| rel | String | Relación de documento con la poliza |
|1.9.3| title | String | Título del documento |
|1.10| totalAmount | Object | Objeto cantidad total |
|1.10.1| amount | Double | Monto |
|1.10.2| currency | String | Divisa |
|1.10.3| exchangeRate | Object | Tipo de cambio para la prima asegurada |
|1.10.3.1| date | Object | Fecha de cambio |
|1.10.3.2| baseCurrency | Object | Divisa de inicio |
|1.10.3.3| targetCurrency | Object | Divisa objetivo |
|1.10.3.4| detail | Object | Objeto que contiene el detalle del tipo de cambio |
|1.10.3.4.1| factor | Object | Objeto factor del tipo de cambio |
|1.10.3.4.1.1| value | Double | Valor al tipo de cambio |
|1.10.3.4.1.2| ratio | Double | Ratio del tipo de cambio |
|1.10.3.4.2| priceType | String | Tipo de cambio |
|1.11| insuredAmount | Object | Objeto monto asegurado del seguro |
|1.11.1| amount | Double | Monto |
|1.11.2| currency | String | Divisa |
|1.12| isDataTreatment | Boolean | Indica si el cliente aceptó el tratamiento de datos |
|1.13| holder | Object | Objeto que contiene datos del titular |
|1.13.1| id | String | Id del titular |
|1.13.2| identityDocument | Object | Objeto que tiene datos sobre el documento de identidad |
|1.13.2.1| documentType | Object | Objeto que tiene datos del tipo de documento |
|1.13.2.1.1| id | String | Id del tipo de documento |
|1.13.2.1.2| description | String | Nombre del tipo de documento |
|1.13.2.2| documentNumber | String | Número de documento |
|1.13.3| contactDetails | List | Lista que contiene los detalles de contacto del titular |
|1.13.3.1| id | String | Código del detalle de contacto |
|1.13.3.2| contact | Object | Objeto contacto |
|1.13.3.2.1| contactDetailType | String | Tipo detalle de contacto |
|1.13.3.2.2| address | String | Dirección |
|1.13.3.2.3| phoneNumber | String | Número de celular |
|1.14| relatedContracts | List | Lista de contratos relacionados |
|1.14.1| id | String | Código del contrato relacionado |
|1.14.2| relationType | Object | Objeto tipo de relación |
|1.14.2.1| id | String | Código del tipo de relación |
|1.14.2.2| description | String | Nombre del tipo de relación |
|1.14.3| contractDetails | Object | Objeto detalles del contrato |
|1.14.3.1| contractType | String | Tipo de contrato |
|1.14.3.2| description | String | Descripción del tipo de contrato |
|1.14.3.3| contractId | String | Código del contrato |
|1.14.3.4| product | Object | Objeto producto |
|1.14.3.4.1| id | String | Código producto |
|1.14.3.4.2| name | String | Nombre producto |
|1.14.3.5| number | String | Número de contrato |
|1.14.3.6| numberType | Object | Objeto tipo de número |
|1.14.3.6.1| id | String | Código tipo de número |
|1.14.3.6.2| description | String | Descripción tipo de número |
|1.15| installmentPlan | Object | Objeto periodo de pago |
|1.15.1| startDate | Date | Fecha de inicio |
|1.15.2| maturityDate | Date | Fecha fin de vigencia |
|1.15.3| totalNumberInstallments | Long | Cantidad de pagos a realizar |
|1.15.4| period | Object | Objeto periodo de pago |
|1.15.4.1| id | String | Código del periodo de pago |
|1.15.4.2| name | String | Nombre del periodo de pago |
|1.15.5| paymentAmount | Object | Objeto cantidad a pagar |
|1.15.5.1| amount | Double | Monto |
|1.15.5.2| currency | String | Divisa |
|1.15.6| exchangeRate | Object | Tipo de cambio para la prima asegurada |
|1.15.6.1| date | Object | Fecha de cambio |
|1.15.6.2| baseCurrency | Object | Divisa de inicio |
|1.15.6.3| targetCurrency | Object | Divisa objetivo |
|1.15.6.4| detail | Object | Objeto que contiene el detalle del tipo de cambio |
|1.15.6.4.1| factor | Object | Objeto factor del tipo de cambio |
|1.15.6.4.1.1| value | Double | Valor al tipo de cambio |
|1.15.6.4.1.2| ratio | Double | Ratio del tipo de cambio |
|1.15.6.4.2| priceType | String | Tipo de cambio |
|1.16| hasAcceptedContract | Boolean | Indicador acepta documentación contractual |
|1.17| inspection | Object | Objeto inspección |
|1.17.1| isRequired | Boolean | Indicador de inspección de bienes |
|1.17.2| fullName | Object | Nombre completo del inspeccionado |
|1.17.3| contactDetails | List | Lista que contiene los detalles de contacto del inspeccionado |
|1.17.3.1| id | String | Código del detalle de contacto |
|1.17.3.2| contact | Object | Objeto contacto |
|1.17.3.2.1| contactDetailType | String | Tipo detalle de contacto |
|1.17.3.2.2| address | String | Dirección |
|1.17.3.2.3| phoneNumber | String | Número de celular |
|1.18| firstInstallment | Object | Objeto que contiene información del primer pago |
|1.18.1| firstPaymentDate | Date | Atributo que determina si el primero pago es requerido o no |
|1.18.2| isPaymentRequired | Boolean | Atributo que determina si el primero pago es requerido o no |
|1.18.3| paymentAmount | Object | Atributo que determina si el primero pago es requerido o no |
|1.18.3.1| amount | Double | Monto |
|1.18.3.2| currency | String | Divisa |
|1.18.4| operationNumber | String | Atributo que determina si el primero pago es requerido o no |
|1.18.5| transactionNumber | String | Atributo que determina si el primero pago es requerido o no |
|1.18.6| operationDate | Date | Atributo que determina si el primero pago es requerido o no |
|1.18.7| exchangeRate | Object | Atributo que determina si el primero pago es requerido o no |
|1.18.7.1| date | Object | Fecha de cambio |
|1.18.7.2| baseCurrency | Object | Divisa de inicio |
|1.18.7.3| targetCurrency | Object | Divisa objetivo |
|1.18.7.4| detail | Object | Objeto que contiene el detalle del tipo de cambio |
|1.18.7.4.1| factor | Object | Objeto factor del tipo de cambio |
|1.18.7.4.1.1| value | Double | Valor al tipo de cambio |
|1.18.7.4.1.2| ratio | Double | Ratio del tipo de cambio |
|1.18.7.4.2| priceType | String | Tipo de cambio |
|1.19| participants | List | Lista de participantes |
|1.19.1| id | String | Código del participante |
|1.19.2| fullName | String | Nombre completo del participante |
|1.19.3| identityDocument | Object | Objeto que tiene datos sobre el documento de identidad |
|1.19.3.1| documentType | Object | Objeto que tiene datos del tipo de documento |
|1.19.3.1.1| id | String | Id del tipo de documento |
|1.19.3.1.2| description | String | Descripción del tipo de documento |
|1.19.3.2| number | String | Número de documento |
|1.19.4| participantType | Object | Objeto tipo de participante |
|1.19.4.1| id | String | Id del tipo de participante |
|1.19.4.2| name | String | Nombre del tipo de participante |
|1.19.5| customerId | String | Id cliente del participante |
|1.19.6| startDate | Date | Fecha de inicio |
|1.19.7| endDate | Date | Fecha de fin |
|1.19.8| relationship | Object | Objeto relación |
|1.19.8.1| id | String | Código de relación |
|1.19.8.2| description | String | Descripción de relación |
|1.19.9| benefitPercentage | Double | Porcentaje beneficiario |
|1.19.10| benefitAmount | Object | Objeto monto beneficiado |
|1.19.10.1| amount | Double | Monto del endoso |
|1.19.10.2| currency | String | Divisa del endoso |
|1.20| businessAgent | Object | Objeto que contiene información del gestor |
|1.20.1| id | String | Código del gestor |
|1.20.2| fullName | String | Nombre completo del gestor |
|1.21| promoter | Object | Objeto que contiene información del presentador |
|1.21.1| id | String | Código del presentador |
|1.21.2| fullName | String | Nombre completo del presentador |
|1.22| insuranceCompany | Object | Objeto que contiene información de la compañia aseguradora |
|1.22.1| id | String | Id de la compañia aseguradora |
|1.22.2| name | String | Nombre de la compañia aseguradora |
|1.22.3| productId | String | Código de producto de la compañia aseguradora |
|1.23| externalQuotationId | String | Código de la cotización de Rimac |
|1.24| externalPolicyNumber | String | Número de poliza de Rimac |
|1.25| status | Object | Objeto estado de la poliza |
|1.25.1| id | String | Código del estado |
|1.25.2| description | String | Descripción del estado |
|1.26| bank | Object | Objeto banco |
|1.26.1| id | String | Id banco |
|1.26.2| branch | Object | Objeto código de oficina |
|1.26.2.1| id | String | Id código de oficina |
|1.27| identityVerificationCode | String | Código de verificación de identidad del cliente |

##### 2.1.3 Ejemplo
```java
PolicyDTO output = rbvdR211.executeBusinessLogicEmissionPrePolicy(PolicyDTO input);
```

### 3.  Mensajes:

#### 3.1  Código RBVD00000129:
> Este código de error es devuelto cuando no se recupera ninguna cotización de la BD.

#### 3.2  Código RBVD00000136:
> Este código de error es devuelto cuando se envía data incorrecta al servicio createInsurance que posteriormente va hacia HOST.

#### 3.3  Código RBVD00000137:
> Este código de error es devuelto cuando se envía un request body incorrecto al servicio de emisión de Rimac.

#### 3.4  Código RBVD00000138:
> Este código de error es devuelto cuando se intenta dar de alta una poliza más de 1 vez en Rimac.

#### 3.5  Código RBVD00000121:
> Este código de error es devuelto cuando hubo un problema al registrar el contrato en la BD.
 
#### 3.6  Código RBVD00000122:
> Este código de error es devuelto cuando hubo un problema al registrar el o los recibos en la BD.

#### 3.7  Código RBVD00000123:
> Este código de error es devuelto cuando hubo un problema al hacer un registro en la tabla T_PISD_INSRNC_CONTRACT_MOV en la BD.

#### 3.8  Código RBVD00000124:
> Este código de error es devuelto cuando hubo un problema al registrar a los participantes en la BD.

### 4.  Versiones:
#### 4.1  Versión 0.1.0-SNAPSHOT

+ Versión 0.1.0-SNAPSHOT: Esta versión permite realizar la lógica de negocio para cumplir con el proceso deseado de la transaccion RBVDT201.






