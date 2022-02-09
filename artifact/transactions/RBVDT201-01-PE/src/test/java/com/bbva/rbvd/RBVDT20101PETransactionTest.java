package com.bbva.rbvd;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.request.TransactionRequest;
import com.bbva.elara.domain.transaction.request.body.CommonRequestBody;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.elara.test.osgi.DummyBundleContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.Resource;

import com.bbva.rbvd.dto.insrncsale.commons.*;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.*;
import com.bbva.rbvd.lib.r211.RBVDR211;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/elara-test.xml",
		"classpath:/META-INF/spring/RBVDT20101PETest.xml" })
public class RBVDT20101PETransactionTest {

	@Spy
	@Autowired
	private RBVDT20101PETransaction transaction;

	@Resource(name = "dummyBundleContext")
	private DummyBundleContext bundleContext;

	@Resource(name = "rbvdR211")
	private RBVDR211 rbvdr211;

	@Mock
	private CommonRequestHeader header;

	@Mock
	private TransactionRequest transactionRequest;

	@Mock
	private PolicyProductPlan productPlan;
	@Mock
	private PolicyPaymentMethodDTO paymentMethod;
	@Mock
	private ValidityPeriodDTO validityPeriod;
	@Mock
	private TotalAmountDTO totalAmount;
	@Mock
	private InsuredAmountDTO insuredAmount;
	@Mock
	private HolderDTO holder;
	@Mock
	private PolicyInstallmentPlanDTO installmentPlan;
	@Mock
	private PolicyInspectionDTO inspection;
	@Mock
	private FirstInstallmentDTO firstInstallment;
	@Mock
	private BusinessAgentDTO businessAgent;
	@Mock
	private PromoterDTO promoter;
	@Mock
	private InsuranceCompanyDTO insuranceCompany;
	@Mock
	private BankDTO bank;

	private MockData mockData;

	@Before
	public void initializeClass() throws Exception {
		MockitoAnnotations.initMocks(this);

		this.transaction.start(bundleContext);
		this.transaction.setContext(new Context());

		CommonRequestBody commonRequestBody = new CommonRequestBody();
		commonRequestBody.setTransactionParameters(new ArrayList<>());

		this.transactionRequest.setBody(commonRequestBody);
		this.transactionRequest.setHeader(header);
		this.transaction.getContext().setTransactionRequest(transactionRequest);

		mockData = MockData.getInstance();

		doReturn("quotationId").when(this.transaction).getQuotationid();
		doReturn("productId").when(this.transaction).getProductid();
		doReturn(productPlan).when(this.transaction).getProductplan();
		doReturn(paymentMethod).when(this.transaction).getPaymentmethod();
		doReturn(validityPeriod).when(this.transaction).getValidityperiod();
		doReturn(totalAmount).when(this.transaction).getTotalamount();
		doReturn(insuredAmount).when(this.transaction).getInsuredamount();
		doReturn(false).when(this.transaction).getIsdatatreatment();
		doReturn(holder).when(this.transaction).getHolder();
		doReturn(new ArrayList<>()).when(this.transaction).getRelatedcontracts();
		doReturn(installmentPlan).when(this.transaction).getInstallmentplan();
		doReturn(false).when(this.transaction).getHasacceptedcontract();
		doReturn(inspection).when(this.transaction).getInspection();
		doReturn(firstInstallment).when(this.transaction).getFirstinstallment();
		doReturn(new ArrayList<>()).when(this.transaction).getParticipants();
		doReturn(businessAgent).when(this.transaction).getBusinessagent();
		doReturn(promoter).when(this.transaction).getPromoter();
		doReturn(bank).when(this.transaction).getBank();
		doReturn("identityVerificationCode").when(this.transaction).getIdentityverificationcode();
		doReturn(insuranceCompany).when(this.transaction).getInsurancecompany();
	}

	@Test
	public void execute() throws IOException {
		PolicyDTO simulateResponse = mockData.getCreateInsuranceRequestBody();
		simulateResponse.setOperationDate(new Date());

		when(rbvdr211.executeBusinessLogicEmissionPrePolicy(anyObject())).thenReturn(simulateResponse);
		this.transaction.execute();

		assertTrue(this.transaction.getAdviceList().isEmpty());
	}

	@Test
	public void testNotNull(){
		assertNotNull(this.transaction);
		this.transaction.execute();

		assertNotNull(this.transaction.getSeverity());
	}

	@Test
	public void testNull() {
		when(rbvdr211.executeBusinessLogicEmissionPrePolicy(anyObject())).thenReturn(null);
		this.transaction.execute();
		assertEquals(Severity.ENR.getValue(), this.transaction.getSeverity().getValue());
	}

}
