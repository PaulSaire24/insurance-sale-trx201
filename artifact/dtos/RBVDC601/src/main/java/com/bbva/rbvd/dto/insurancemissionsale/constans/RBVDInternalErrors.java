//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.bbva.rbvd.dto.insurancemissionsale.constans;

public enum RBVDInternalErrors {
    ERROR_NOT_CONFIG_FREQUENCY_TYPE("RBVD00088042", true, "El periodo de pago %s enviado no está configurado para su conversión en la consola de operaciones para el alta de póliza. Por favor, realiza las configuraciones necesarias para permitir una emisión adecuada de la póliza."),
    ERROR_NOT_VALUE_QUOTATION_FREQUENCY_TYPE("RBVD00088043", true, "Se identificó un problema con el periodo de pago, el cual no se registró al efectuar la cotización. Por favor, revise la cotización relacionada en nuestros sistemas: %s."),
    ERROR_NOT_VALUE_PREMIUM_AMOUNT("RBVD00088044", true, "Se identificó un problema con el importe de la prima, el cual no se registró al efectuar la cotización. Por favor, revise la cotización relacionada en nuestros sistemas: %s."),
    ERROR_NOT_VALUE_QUOTATION_CURRENCY_ID("RBVD00088045", true, "Se identificó un problema con la moneda , el cual no se registró al efectuar la cotización. Por favor, revise la cotización relacionada en nuestros sistemas: %s."),
    ERROR_NOT_VALUE_VALIDITY_PERIOD("RBVD00088046", true, "El inicio de la cobertura no se ha incluido en el request para crear el seguro. Por favor, corrija esta información: campo - valor: %s - %s."),
    ERROR_RESPONSE_SERVICE_ICR2("RBVD00088047", true, "Se produjo un error al llamar al backend ICR2. Por favor, inténtelo de nuevo más tarde."),
    INSERTION_ERROR_IN_TABLE("RBVD00088048", true, "Se produjo un error al intentar insertar en la tabla [%s]. Por favor, vuelva a intentarlo."),
    ERROR_NOT_VALUE_REQUEST_CURRENCY_ID("RBVD00088049", true, "Se ha detectado una discrepancia durante la validación de los montos entre la moneda de la cotización ('%s') y la moneda de la petición ('%s'). Le sugerimos revisar y ajustar la información pertinente para garantizar la igualdad."),
    ERROR_EMPTY_RESULT_FREQUENCY_TYPE("RBVD00088050", true, "No se encontró un periodo de pago registrado en la base de datos para el tipo de frecuencia %s enviado. Por favor, verifica el tipo de periodo de pago enviado o registra un nuevo periodo de pago si es necesario."),
    ERROR_EMPTY_RESULT_QUOTATION_DATA("RBVD00088051", true, "No se encontraron cotizaciones con el valor enviado. Por favor, verifica la creación de la siguiente cotización: %s."),
    ERROR_EMPTY_RESULT_PRODUCT_DATA("RBVD00088052", true, "No se encontró el producto en la base de datos. Por favor, verifica la creación del siguiente producto: %s."),
    ERROR_VALID_ADDRESS("RBVD00088053", true, "No se ha podido completar el alta del seguro debido a un error en la construcción de la dirección, causado por inconsistencias en la dirección proporcionada . Por favor, revise la misma: %s."),
    ERROR_VALID_RANGE_AMOUNT("RBVD00088054", true, "Se ha identificado un error en la validación del monto de cotización, ya que el valor proporcionado (%s) está fuera del rango permitido para la contratación de un seguro. Rango mínimo: %s, Rango máximo: %s,Variacion: %s. Por favor, revise y ajuste el monto de acuerdo con los rangos especificados."),
    ERROR_POLICY_ALREADY_EXISTS("RBVD00088055", true, "Ya hay un contrato activo con la cotización que enviaste, %s. Por favor, revisa el listado de contratos del cliente."),
    ERROR_NOT_VALUE_QUOTATION("RBVD00088056", true, "Se identificó un problema con el %s, el cual no se registró al efectuar la cotización. Por favor, revise la cotización relacionada en nuestros sistemas: %s."),
    ERROR_GENERIC_HOST("RBVD00088057", true, "[MESSAGE_GENERIC]"),
    ERROR_GENERIC_APX_IN_CALLED_RIMAC("RBVD00088058", false, "[MESSAGE_GENERIC]"),
    ERROR_NOT_RESULT_CONTRACT("RBVD00088059", true, "El contrato '%s' no se encuentra en nuestra base de datos. Por favor, verifica la información proporcionada y vuelve a intentarlo."),
    ERROR_NOT_TIMEOUT_GENERAL("RBVD00088060", true, "Se produjo un error debido a un tiempo de espera excesivo con la base de datos. Por favor, intenta de nuevo."),
    ERROR_STATUS_CONTRACT_FLOW_PRE_FORMALIZATION("RBVD00088061", true, "El contrato '%s' esta en estado '%s' y no se puede realizar la preformalización. Por favor, verifica el contrato proporcionado ."),
    ERROR_STATUS_CONTRACT_BAJ("RBVD00088062", true, "Debido a que el contrato está dado de baja, la preformalización no puede ser realizada."),
    ERROR_STATUS_CONTRACT_ANU("RBVD00088063", true, "Debido a que el contrato está anulado, la preformalización no puede ser realizada."),
    ERROR_STATUS_CONTRACT_FOR("RBVD00088064", true, "La formalización del contrato ya ha sido realizada."),
    UPDATE_ERROR_IN_CONTRACT_TABLE("RBVD00088065", true, "No se actualizo el registro en la tabla [T_PISD_INSURANCE_CONTRACT]");

    private final String adviceCode;
    private final boolean rollback;
    private final String message;

    RBVDInternalErrors(String adviceCode, boolean rollback, String message) {
        this.adviceCode = adviceCode;
        this.rollback = rollback;
        this.message = message;
    }

    public String getAdviceCode() {
        return this.adviceCode;
    }

    public boolean isRollback() {
        return this.rollback;
    }

    public String getMessage() {
        return this.message;
    }
}
