package com.bbva.rbvd.lib.r211.impl.properties;

import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class BasicProductInsuranceProperties extends Properties{

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicProductInsuranceProperties.class);

    public String obtainFrequencyTypeByPeriodId(String periodId){
        String frequencyType = this.getProperty(periodId, StringUtils.EMPTY);
        LOGGER.info(" :: BasicProductInsuranceProperties[ obtainFrequencyTypeByPeriodId :: {} ]",frequencyType);
        return frequencyType;
    }

    public Boolean enableValidationQuotationAmountByProductIdAndChannelId(String productId, String channelId){
        String key = "property.validation.range.".concat(StringUtils.defaultString(productId)).concat(".").concat(StringUtils.defaultString(channelId));
        String enabledRulesValidationQuotationAmount = this.getProperty(key, String.valueOf(PISDConstants.Number.CERO));
        LOGGER.info(" :: BasicProductInsuranceProperties[ enabledRulesValidationQuotationAmount :: {} ]",enabledRulesValidationQuotationAmount);
        return enabledRulesValidationQuotationAmount.equalsIgnoreCase("1") ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean enabledFlowEmissionRoyal2_0ByProduct(String productId){
        String key = "flow.royal2.enabled.product.".concat(StringUtils.defaultString(productId));
        Boolean enabledRulesValidationQuotationAmount = Boolean.parseBoolean(this.getProperty(key, Boolean.FALSE.toString()));
        LOGGER.info(" :: BasicProductInsuranceProperties[ enabledRulesValidationQuotationAmount :: {} ]",enabledRulesValidationQuotationAmount);
        return enabledRulesValidationQuotationAmount;
    }

    public Boolean enabledAllProductsEmissionRoyal2_0(){
        Boolean enabledRulesValidationQuotationAmount = Boolean.parseBoolean(this.getProperty("flow.royal2.enabled.all.products", Boolean.FALSE.toString()));
        LOGGER.info(" :: BasicProductInsuranceProperties[ enabledRulesValidationQuotationAmount :: {} ]",enabledRulesValidationQuotationAmount);
        return enabledRulesValidationQuotationAmount;
    }

    public Boolean enabledPaymentICR3(){
        Boolean enabledRulesValidationQuotationAmount = Boolean.parseBoolean(this.getProperty("flow.royal2.enabled.payment.icr3", Boolean.TRUE.toString()));
        LOGGER.info(" :: BasicProductInsuranceProperties[ enabledRulesValidationQuotationAmount :: {} ]",enabledRulesValidationQuotationAmount);
        return enabledRulesValidationQuotationAmount;
    }

    public int obtainRangePaymentAmount(){
        String rangePaymentAmount = this.getProperty("property.range.payment.amount.insurance", "5");
        LOGGER.info(" :: BasicProductInsuranceProperties[ obtainRangePaymentAmount :: {} ]",rangePaymentAmount);
        return Integer.parseInt(rangePaymentAmount);
    }

    public String obtainOfficeTelemarketingCode(){
        String telemarketingCode = this.getProperty("telemarketing.code", StringUtils.EMPTY);
        LOGGER.info(" :: BasicProductInsuranceProperties[ obtainFrequencyTypeByPeriodId :: {} ]",telemarketingCode);
        return telemarketingCode;
    }

    public List<String> obtainAapSSearchInContactDetail(){
        String aapSDigitalSale = this.getProperty("obtain.aaps.config.contact.details", StringUtils.EMPTY);
        LOGGER.info(" :: BasicProductInsuranceProperties[ obtainAapSDigitalSale :: {} ]",aapSDigitalSale);
        return Arrays.asList(aapSDigitalSale.split(";"));
    }

    public String obtainDefaultPromoterCodeSaleDigital(){
        String aapDigitalSale = this.getProperty("agent.and.promoter.code", StringUtils.EMPTY);
        LOGGER.info(" :: BasicProductInsuranceProperties[ obtainAapSDigitalSale :: {} ]",aapDigitalSale);
        return aapDigitalSale;
    }

    public List<String> obtainSaleChannelsNotDigital(){
        String saleChannelNotDigital = this.getProperty("obtain.sale.channels.list", StringUtils.EMPTY);
        LOGGER.info(" :: BasicProductInsuranceProperties[ obtainSaleChannelsNotDigital :: {} ]",saleChannelNotDigital);
        return Arrays.asList(saleChannelNotDigital.split(";"));
    }

    public List<String> obtainProductsNotGenerateMonthlyReceipts(){
        String productsNotGenerateFirstReceipts = this.getProperty("products.modalities.only.first.receipt", StringUtils.EMPTY);
        LOGGER.info(" :: BasicProductInsuranceProperties[ productsNotGenerateFirstReceipts :: {} ]",productsNotGenerateFirstReceipts);
        return Arrays.asList(productsNotGenerateFirstReceipts.split(","));
    }

    
}
