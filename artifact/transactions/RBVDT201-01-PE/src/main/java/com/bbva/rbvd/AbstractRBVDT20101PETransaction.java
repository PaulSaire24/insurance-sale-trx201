package com.bbva.rbvd;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.rbvd.dto.insrncsale.commons.BankDTO;
import com.bbva.rbvd.dto.insrncsale.commons.HolderDTO;
import com.bbva.rbvd.dto.insrncsale.commons.PolicyInspectionDTO;
import com.bbva.rbvd.dto.insrncsale.commons.ValidityPeriodDTO;
import com.bbva.rbvd.dto.insrncsale.policy.BusinessAgentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.FirstInstallmentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.InsuranceCompanyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.InsuredAmountDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyInstallmentPlanDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyPaymentMethodDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyProductPlan;
import com.bbva.rbvd.dto.insrncsale.policy.PromoterDTO;
import com.bbva.rbvd.dto.insrncsale.policy.RelatedContractDTO;
import com.bbva.rbvd.dto.insrncsale.policy.TotalAmountDTO;
import java.util.List;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractRBVDT20101PETransaction extends AbstractTransaction {

	public AbstractRBVDT20101PETransaction(){
	}


	/**
	 * Return value for input parameter quotationId
	 */
	protected String getQuotationid(){
		return (String)this.getParameter("quotationId");
	}

	/**
	 * Return value for input parameter productId
	 */
	protected String getProductid(){
		return (String)this.getParameter("productId");
	}

	/**
	 * Return value for input parameter productPlan
	 */
	protected PolicyProductPlan getProductplan(){
		return (PolicyProductPlan)this.getParameter("productPlan");
	}

	/**
	 * Return value for input parameter paymentMethod
	 */
	protected PolicyPaymentMethodDTO getPaymentmethod(){
		return (PolicyPaymentMethodDTO)this.getParameter("paymentMethod");
	}

	/**
	 * Return value for input parameter validityPeriod
	 */
	protected ValidityPeriodDTO getValidityperiod(){
		return (ValidityPeriodDTO)this.getParameter("validityPeriod");
	}

	/**
	 * Return value for input parameter totalAmount
	 */
	protected TotalAmountDTO getTotalamount(){
		return (TotalAmountDTO)this.getParameter("totalAmount");
	}

	/**
	 * Return value for input parameter insuredAmount
	 */
	protected InsuredAmountDTO getInsuredamount(){
		return (InsuredAmountDTO)this.getParameter("insuredAmount");
	}

	/**
	 * Return value for input parameter isDataTreatment
	 */
	protected Boolean getIsdatatreatment(){
		return (Boolean)this.getParameter("isDataTreatment");
	}

	/**
	 * Return value for input parameter holder
	 */
	protected HolderDTO getHolder(){
		return (HolderDTO)this.getParameter("holder");
	}

	/**
	 * Return value for input parameter relatedContracts
	 */
	protected List<RelatedContractDTO> getRelatedcontracts(){
		return (List<RelatedContractDTO>)this.getParameter("relatedContracts");
	}

	/**
	 * Return value for input parameter installmentPlan
	 */
	protected PolicyInstallmentPlanDTO getInstallmentplan(){
		return (PolicyInstallmentPlanDTO)this.getParameter("installmentPlan");
	}

	/**
	 * Return value for input parameter hasAcceptedContract
	 */
	protected Boolean getHasacceptedcontract(){
		return (Boolean)this.getParameter("hasAcceptedContract");
	}

	/**
	 * Return value for input parameter inspection
	 */
	protected PolicyInspectionDTO getInspection(){
		return (PolicyInspectionDTO)this.getParameter("inspection");
	}

	/**
	 * Return value for input parameter firstInstallment
	 */
	protected FirstInstallmentDTO getFirstinstallment(){
		return (FirstInstallmentDTO)this.getParameter("firstInstallment");
	}

	/**
	 * Return value for input parameter participants
	 */
	protected List<ParticipantDTO> getParticipants(){
		return (List<ParticipantDTO>)this.getParameter("participants");
	}

	/**
	 * Return value for input parameter businessAgent
	 */
	protected BusinessAgentDTO getBusinessagent(){
		return (BusinessAgentDTO)this.getParameter("businessAgent");
	}

	/**
	 * Return value for input parameter promoter
	 */
	protected PromoterDTO getPromoter(){
		return (PromoterDTO)this.getParameter("promoter");
	}

	/**
	 * Return value for input parameter bank
	 */
	protected BankDTO getBank(){
		return (BankDTO)this.getParameter("bank");
	}

	/**
	 * Return value for input parameter identityVerificationCode
	 */
	protected String getIdentityverificationcode(){
		return (String)this.getParameter("identityVerificationCode");
	}

	/**
	 * Return value for input parameter insuranceCompany
	 */
	protected InsuranceCompanyDTO getInsurancecompany(){
		return (InsuranceCompanyDTO)this.getParameter("insuranceCompany");
	}

	/**
	 * Set value for String output parameter id
	 */
	protected void setId(final String field){
		this.addParameter("id", field);
	}
}
