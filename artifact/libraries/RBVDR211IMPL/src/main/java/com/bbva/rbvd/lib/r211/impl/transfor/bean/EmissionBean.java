package com.bbva.rbvd.lib.r211.impl.transfor.bean;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.CommonBO;
import com.bbva.pisd.dto.insurance.bo.GeographicGroupTypeBO;
import com.bbva.pisd.dto.insurance.bo.GeographicGroupsBO;
import com.bbva.pisd.dto.insurance.bo.LocationBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.bo.emision.*;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.commons.PolicyInspectionDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.PersonTypeEnum;
import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.lib.r211.impl.RBVDR211Impl;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Endorsement.RUC_ID;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.LabelCompany.SIN_ESPECIFICAR;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.LabelRimac;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.NotSpecified.NO_EXIST;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Period;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class EmissionBean {

    private static final String PROPERTY_VALIDATE_NATURAL_PARTICIPANT = "invoke.participant.validation.emission.noLife.natural.";
    private static final String PROPERTY_VALIDATE_LEGAL_PARTICIPANT = "invoke.participant.validation.emission.noLife.legal.";
    private static final Logger LOGGER = LoggerFactory.getLogger(EmissionBean.class);


    public static EmisionBO addNotLifeParticipants(EmisionBO rimacRequest, PolicyDTO requestBody, ApplicationConfigurationService applicationConfigurationService,CustomerListASO customerListASO,
                                            ProcessPrePolicyDTO processPrePolicyDTO) {

        String productChannelConditionalNaturalPers = applicationConfigurationService.
                getDefaultProperty(PROPERTY_VALIDATE_NATURAL_PARTICIPANT.
                        concat(requestBody.getProductId()).concat(".").
                        concat(requestBody.getSaleChannelId()),"true");
        String productChannelConditionalLegalPers = applicationConfigurationService.
                getDefaultProperty(PROPERTY_VALIDATE_LEGAL_PARTICIPANT.
                        concat(requestBody.getProductId()).concat(".").
                        concat(requestBody.getSaleChannelId()),"true");

        boolean validateNaturalParticipant = Boolean.parseBoolean(productChannelConditionalNaturalPers);
        boolean validateLegalParticipant = Boolean.parseBoolean(productChannelConditionalLegalPers);

        if(!validateNaturalParticipant && !validateLegalParticipant){
            return rimacRequest;
        }


        String tipoDoc = customerListASO.getData().get(0).getIdentityDocuments().get(0).getDocumentType().getId();
        String nroDoc = customerListASO.getData().get(0).getIdentityDocuments().get(0).getDocumentNumber();
        boolean isLegalPerson = RUC_ID.equalsIgnoreCase(tipoDoc) && StringUtils.startsWith(nroDoc, "20");

        EmisionBO generalEmisionRequest = null;

        if((validateNaturalParticipant && !isLegalPerson) || (isLegalPerson && validateLegalParticipant)) {
            generalEmisionRequest = EmissionBean.toRequestGeneralBodyRimac(rimacRequest, requestBody,processPrePolicyDTO,customerListASO,applicationConfigurationService,processPrePolicyDTO.getOperationGlossaryDesc());
            LOGGER.info("***** RBVDR211 generalEmisionRequest => {} ****", generalEmisionRequest);
        }

        if (isLegalPerson && validateLegalParticipant) {
            OrganizationBean.setOrganization(generalEmisionRequest,requestBody,customerListASO,processPrePolicyDTO);
        }

        return Objects.isNull(generalEmisionRequest) ? rimacRequest : generalEmisionRequest;
    }


    public static EmisionBO toRequestGeneralBodyRimac(EmisionBO rimacRequest, PolicyDTO requestBody, ProcessPrePolicyDTO processPrePolicyDTO,CustomerListASO customerList, ApplicationConfigurationService applicationConfigurationService,String operationGlossaryDesc){
        EmisionBO generalEmisionRimacRequest = new EmisionBO();
        PayloadEmisionBO emisionBO = new PayloadEmisionBO();
        emisionBO.setEmision(rimacRequest.getPayload());
        String productsCalculateValidityMonths = applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt","");
        emisionBO.getEmision().setProducto(getInsuranceBusinessNameFromDB(processPrePolicyDTO.getResponseQueryGetProductById()));
        generalEmisionRimacRequest.setPayload(emisionBO);

        FinanciamientoBO financiamiento = new FinanciamientoBO();
        financiamiento.setFrecuencia(applicationConfigurationService.getProperty(requestBody.getInstallmentPlan().getPeriod().getId()));
        String strDate = requestBody.getValidityPeriod().getStartDate().toInstant()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        financiamiento.setFechaInicio(strDate);
        financiamiento.setNumeroCuotas(requestBody.getInstallmentPlan().getTotalNumberInstallments());

        if (Arrays.asList(productsCalculateValidityMonths.split(",")).contains(operationGlossaryDesc)) {
            if(Period.MONTHLY_LARGE.equals(requestBody.getInstallmentPlan().getPeriod().getId())){
                financiamiento.setFrecuencia(Period.FREE_PERIOD);
            }
            financiamiento.setNumeroCuotas((long) PISDConstants.Number.UNO);
        }

        List<FinanciamientoBO> financiamientoBOs = new ArrayList<>();
        financiamientoBOs.add(financiamiento);
        CrearCronogramaBO crearCronogramaBO = new CrearCronogramaBO();
        crearCronogramaBO.setFinanciamiento(financiamientoBOs);

        generalEmisionRimacRequest.getPayload().setCrearCronograma(crearCronogramaBO);

        CustomerBO customer = customerList.getData().get(0);
        List<PersonaBO> personasList = new ArrayList<>();
        PersonaBO persona = Optional.ofNullable(customer).map(customerBO -> constructPerson(requestBody,customerBO,processPrePolicyDTO,applicationConfigurationService)).orElse(new PersonaBO()) ;

        StringBuilder stringAddress  = new StringBuilder();

        String filledAddress = fillAddress(customerList, persona, stringAddress,requestBody.getSaleChannelId(),applicationConfigurationService);
        validateIfAddressIsNull(filledAddress);

        constructListPersons(persona, personasList);

        AgregarPersonaBO agregarPersonaBO = new AgregarPersonaBO();
        agregarPersonaBO.setPersona(personasList);

        generalEmisionRimacRequest.getPayload().setAgregarPersona(agregarPersonaBO);

        if(Arrays.asList(productsCalculateValidityMonths.split(",")).contains(operationGlossaryDesc)){
            DatoParticularBO quintoDatoParticular = new DatoParticularBO();
            quintoDatoParticular.setEtiqueta(LabelRimac.PARTICULAR_DATA_MESES_DE_VIGENCIA);
            quintoDatoParticular.setCodigo(StringUtils.EMPTY);
            quintoDatoParticular.setValor(String.valueOf(getMonthsOfValidity(requestBody.getInstallmentPlan().getMaturityDate())));
            generalEmisionRimacRequest.getPayload().getEmision().getDatosParticulares().add(quintoDatoParticular);
        }

        return generalEmisionRimacRequest;
    }

    private static int getMonthsOfValidity(Date maturity){

        LocalDate todayDate = new LocalDate();
        LocalDate maturityDate = FunctionsUtils.convertDateToLocalDate(maturity);
        int difYear = maturityDate.getYear() - todayDate.getYear();
        int difDate = maturityDate.getDayOfMonth() > todayDate.getDayOfMonth() ? 1 : 0 ;
        int difMonth = difYear*12 + maturityDate.getMonthOfYear() - todayDate.getMonthOfYear() + difDate;

        return Math.max(difMonth, 0);
    }

    private static void validateIfAddressIsNull(String filledAddress) {
        if (isNull(filledAddress)) {
            throw new BusinessException("RBVD10094935", false,"Revisar Datos de Direccion");
        }
    }

    private static void constructListPersons(PersonaBO persona, List<PersonaBO> personasList) {
        int[] intArray = new int[]{ 8,9,23 };
        for(int i=0; i<intArray.length; i++){
            PersonaBO personas =  getFillFieldsPerson(persona);
            personas.setRol(intArray[i]);
            personasList.add(personas);
        }
    }

    private static PersonaBO getFillFieldsPerson(PersonaBO persona) {
        PersonaBO persons = new PersonaBO();
        persons.setTipoDocumento(persona.getTipoDocumento());
        persons.setNroDocumento(persona.getNroDocumento());
        persons.setApePaterno(persona.getApePaterno());
        persons.setApeMaterno(persona.getApeMaterno());
        persons.setNombres(persona.getNombres());
        persons.setFechaNacimiento(persona.getFechaNacimiento());
        persons.setSexo(persona.getSexo());
        persons.setCorreoElectronico(persona.getCorreoElectronico());
        persons.setDireccion(persona.getDireccion());
        persons.setDistrito(persona.getDistrito());
        persons.setProvincia(persona.getProvincia());
        persons.setUbigeo(persona.getUbigeo());
        persons.setDepartamento(persona.getDepartamento());
        persons.setTipoVia(persona.getTipoVia());
        persons.setNombreVia(persona.getNombreVia());
        persons.setNumeroVia(persona.getNumeroVia());
        persons.setCelular(persona.getCelular());
        return persons;
    }


    public static String fillAddress(CustomerListASO customerList, PersonaBO persona, StringBuilder stringAddress, String saleChannelId, ApplicationConfigurationService applicationConfigurationService) {

        String picCodeValue = applicationConfigurationService.getProperty("pic.code");

        String controlChannel = " ";

        CustomerBO customer = customerList.getData().get(0);
        LocationBO customerLocation = CollectionUtils.isEmpty(customer.getAddresses()) ? null :  customer.getAddresses().get(0).getLocation();
        List<GeographicGroupsBO> geographicGroups = null;

        if(Objects.nonNull(customerLocation)){
            geographicGroups = customerLocation.getGeographicGroups().stream()
                    .filter(element -> !filterExceptionAddress(element.getGeographicGroupType().getId()))
                    .collect(Collectors.toList());
        }

        fillAddressUbigeo(geographicGroups, persona);

        List<GeographicGroupsBO> geographicGroupsAddress = null;

        if(!CollectionUtils.isEmpty(geographicGroups)){
            geographicGroupsAddress = geographicGroups.stream()
                    .filter(element -> !filterUbicationCode(element.getGeographicGroupType().getId()))
                    .collect(Collectors.toList());
        }

        String addressViaList =  CollectionUtils.isEmpty(geographicGroupsAddress) ? null :  fillAddressViaList(geographicGroupsAddress, persona);
        String addressGroupList = CollectionUtils.isEmpty(geographicGroupsAddress) ? null : fillAddressGroupList(geographicGroupsAddress, addressViaList, persona);

        if(isNull(addressGroupList) && isNull(addressViaList) &&
                picCodeValue.equals(saleChannelId)) {
            return null;
        } else if (isNull(addressGroupList) && isNull(addressViaList)) {
            persona.setTipoVia(LabelRimac.SIN_ESPECIFICAR);
            persona.setNombreVia(LabelRimac.SIN_ESPECIFICAR);
            persona.setNumeroVia(LabelRimac.SIN_ESPECIFICAR);
            persona.setDireccion(LabelRimac.SIN_ESPECIFICAR);
            return controlChannel;
        }

        String addressNumberVia = fillAddressNumberVia(geographicGroupsAddress, persona);

        String fullNameOther = fillAddressOther(geographicGroupsAddress, stringAddress);

        if (NO_EXIST.equals(addressNumberVia) || NO_EXIST.equals(fullNameOther)){
            fillAddressAditional(geographicGroupsAddress, stringAddress);
        }

        return getFullDirectionFrom(addressViaList, addressGroupList, addressNumberVia, stringAddress, persona);

    }

    public static void fillAddressAditional(List<GeographicGroupsBO> geographicGroupsAddress, StringBuilder stringAddress) {
        String nameManzana = "";
        String nameLote = "";

        Map<String, String> mapAditional = geographicGroupsAddress.stream()
                .filter(element -> filterAddressAditional(element.getGeographicGroupType().getId()))
                .collect(Collectors.groupingBy(
                        element -> element.getGeographicGroupType().getId(),
                        Collectors.mapping(GeographicGroupsBO::getName, Collectors.joining(", "))
                ));

        nameManzana = mapAditional.getOrDefault("BLOCK", "");
        nameLote = mapAditional.getOrDefault("LOT", "");

        if (!nameManzana.isEmpty() && !stringAddress.toString().contains(nameManzana)) {
            appendToAddress(stringAddress, nameManzana);
        }
        if (!nameLote.isEmpty() && !stringAddress.toString().contains(nameLote)) {
            appendToAddress(stringAddress, nameLote);
        }
        if (!nameManzana.isEmpty() && !nameLote.isEmpty()) {
            if (!stringAddress.toString().contains(nameManzana) || !stringAddress.toString().contains(nameLote)) {
                appendToAddress(stringAddress, nameManzana.concat(" ").concat(nameLote));
            }
        }
    }

    private static  void appendToAddress(StringBuilder stringAddress, String toAppend) {
        if (stringAddress.length() > 0 && !stringAddress.toString().endsWith(" ")) {
            stringAddress.append(" ");
        }
        stringAddress.append(toAppend);
    }

    private static boolean  filterAddressAditional (final String geographicGroupTyeId){
        Stream<String> aditionalCode = Stream.of("BLOCK","LOT");
        return aditionalCode.anyMatch(element -> element.equalsIgnoreCase(geographicGroupTyeId));
    }

    private static String fillAddressNumberVia(List<GeographicGroupsBO> geographicGroupsAddress, PersonaBO persona) {

        String numberVia = NO_EXIST;

        if(!CollectionUtils.isEmpty(geographicGroupsAddress)){
            numberVia = geographicGroupsAddress.stream()
                    .filter(geographicGroupsBO -> geographicGroupsBO.getGeographicGroupType().getId().equalsIgnoreCase("EXTERIOR_NUMBER")).findAny()
                    .map(CommonBO::getName).orElse(NO_EXIST);
        }

        if(!NO_EXIST.equals(numberVia)) {
            persona.setNumeroVia(numberVia);
        } else {
            persona.setNumeroVia(SIN_ESPECIFICAR);
        }

        return numberVia;

    }

    public static String fillAddressOther(List<GeographicGroupsBO> geographicGroupsAddress, StringBuilder stringAddress) {


        String typeOther = "";
        String nameOther = "";
        String separationSymbol = "-";
        String addressOther = NO_EXIST;

        if(!CollectionUtils.isEmpty(geographicGroupsAddress)){
            addressOther = geographicGroupsAddress.stream()
                    .filter(element -> filterAddressOther(element.getGeographicGroupType().getId()))
                    .findFirst()
                    .map(element -> getTypeOther(element.getGeographicGroupType().getId()) + separationSymbol + element.getName())
                    .orElse(NO_EXIST);
        }

        if (!NO_EXIST.equals(addressOther) && addressOther.split(separationSymbol).length > 1) {
            String[] arrayOther = addressOther.split(separationSymbol);
            typeOther = arrayOther[0];
            nameOther = arrayOther[1];
            stringAddress.append(typeOther.concat(" ").concat(nameOther));
        }

        return addressOther;
    }

    private static String getTypeOther(final String geographicGroupTypeId) {
        Map<String, String> mapTypeTypeOther = tipeListOther();
        return mapTypeTypeOther.entrySet().stream()
                .filter(element -> element.getKey().equals(geographicGroupTypeId))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private static boolean filterAddressOther(final String geographicGroupTyeId) {
        Map<String, String> addressOther = tipeListOther();
        return addressOther.entrySet().stream().anyMatch(element -> element.getKey().equals(geographicGroupTyeId));
    }

    private static Map<String, String> tipeListOther() {

        Map<String, String> tipeListOther = new HashMap<>();

        tipeListOther.put("QUINTA", "QUINTA");
        tipeListOther.put("INTERIOR_NUMBER","DPTO.");
        tipeListOther.put("FLOOR", "PISO");
        tipeListOther.put("COLONY", "COL.");
        tipeListOther.put("DELEGATION","OTRO");
        tipeListOther.put("MUNICIPALITY","MUNI.");
        tipeListOther.put("DOOR","INT.");

        return tipeListOther;

    }


    private static String fillAddressViaList(List<GeographicGroupsBO> geographicGroupsAddress, PersonaBO persona) {

        String nombreDir1 = null;
        String viaType = "";
        String viaName = "";
        String separationSymbol = "-";

        String dataViaType = geographicGroupsAddress.stream()
                .filter(element -> filterViaType(element.getGeographicGroupType().getId()))
                .findFirst()
                .map(element -> getViaType(element.getGeographicGroupType().getId()) + separationSymbol + element.getName())
                .orElse(null);

        if(nonNull(dataViaType) && dataViaType.split(separationSymbol).length > 1) {
            String[] arrayVia = dataViaType.split(separationSymbol);
            viaType = arrayVia[0];
            viaName = arrayVia[1];
            persona.setTipoVia(viaType);
            persona.setNombreVia(viaName);
            nombreDir1 = viaType.concat(" ").concat(viaName);
        }

        return nombreDir1;

    }

    private static String getViaType(final String geographicGroupTypeId) {
        Map<String, String> mapTypeListDir1 = tipeListDir1();
        return mapTypeListDir1.entrySet().stream()
                .filter(element -> element.getKey().equals(geographicGroupTypeId))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }


    private static  boolean filterViaType(final String geographicGroupTyeId) {
        Map<String, String> mapTypeListDir1 = tipeListDir1();
        return mapTypeListDir1.entrySet().stream().anyMatch(element -> element.getKey().equals(geographicGroupTyeId));
    }

    private static Map<String, String> tipeListDir1() {

        Map<String, String> tipeListDir1Map = new HashMap<>();

        tipeListDir1Map.put("ALAMEDA", "ALM");
        tipeListDir1Map.put("AVENUE", "AV.");
        tipeListDir1Map.put("STREET", "CAL");
        tipeListDir1Map.put("MALL", "CC.");
        tipeListDir1Map.put("ROAD", "CRT");
        tipeListDir1Map.put("SHOPPING_ARCADE", "GAL");
        tipeListDir1Map.put("JIRON", "JR.");
        tipeListDir1Map.put("JETTY", "MAL");
        tipeListDir1Map.put("OVAL", "OVA");
        tipeListDir1Map.put("PEDESTRIAN_WALK", "PAS");
        tipeListDir1Map.put("SQUARE", "PLZ");
        tipeListDir1Map.put("PARK", "PQE");
        tipeListDir1Map.put("PROLONGATION", "PRL");
        tipeListDir1Map.put("PASSAGE", "PSJ");
        tipeListDir1Map.put("BRIDGE", "PTE");
        tipeListDir1Map.put("DESCENT", "BAJ");
        tipeListDir1Map.put("PORTAL", "POR");

        return tipeListDir1Map;

    }

    private static String fillAddressGroupList(List<GeographicGroupsBO> geographicGroupsAddress, String addressViaList, PersonaBO persona) {

        String nombreDir2 = null;
        String groupType = "";
        String groupName = "";
        String separationSymbol = "-";

        String dataGroupType = geographicGroupsAddress.stream()
                .filter(element -> filterGroupType(element.getGeographicGroupType().getId()))
                .findFirst()
                .map(element -> getGroupType(element.getGeographicGroupType().getId()) + separationSymbol + element.getName())
                .orElse(null);

        if(nonNull(dataGroupType) && dataGroupType.split(separationSymbol).length > 1) {
            String[] arrayGroupType = dataGroupType.split(separationSymbol);
            groupType = arrayGroupType[0];
            groupName = arrayGroupType[1];
            nombreDir2 = groupType.concat(" ").concat(groupName);
        }

        if(nonNull(dataGroupType) && isNull(addressViaList)) {
            persona.setTipoVia(groupType);
            persona.setNombreVia(groupName);
        }

        return nombreDir2;

    }

    private static String getGroupType(final String geographicGroupTyeId) {
        Map<String, String> mapTypeListDir2 = tipeListDir2();
        return mapTypeListDir2.entrySet().stream()
                .filter(element -> element.getKey().equals(geographicGroupTyeId))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private static boolean filterGroupType(final String geographicGroupTyeId) {
        Map<String, String> mapTypeListDir2 = tipeListDir2();
        return mapTypeListDir2.entrySet().stream().anyMatch(element -> element.getKey().equals(geographicGroupTyeId));
    }

    private static Map<String, String> tipeListDir2() {

        Map<String, String> tipeListDir2Map = new HashMap<>();

        tipeListDir2Map.put("GROUP", "AGR");
        tipeListDir2Map.put("AAHH", "AHH");
        tipeListDir2Map.put("HOUSING_COMPLEX", "CHB");
        tipeListDir2Map.put("INDIGENOUS_COMMUNITY", "COM");
        tipeListDir2Map.put("PEASANT_COMMUNITY", "CAM");
        tipeListDir2Map.put("HOUSING_COOPERATIVE", "COV");
        tipeListDir2Map.put("STAGE", "ETP");
        tipeListDir2Map.put("SHANTYTOWN", "PJJ");
        tipeListDir2Map.put("NEIGHBORHOOD", "SEC");
        tipeListDir2Map.put("URBANIZATION", "URB");
        tipeListDir2Map.put("NEIGHBORHOOD_UNIT", "UV.");
        tipeListDir2Map.put("ZONE", "ZNA");
        tipeListDir2Map.put("ASSOCIATION", "ASC");
        tipeListDir2Map.put("FUNDO", "FUN");
        tipeListDir2Map.put("MINING_CAMP", "MIN");
        tipeListDir2Map.put("RESIDENTIAL", "RES");

        return tipeListDir2Map;

    }


    private static boolean filterExceptionAddress(final String geographicGroupTypeId) {
        Stream<String> ubicationAddress = Stream.of("UNCATEGORIZED", "NOT_PROVIDED");
        return ubicationAddress.anyMatch(element -> element.equals(geographicGroupTypeId));
    }

    private static boolean filterUbicationCode(final String geographicGroupTypeId) {
        Stream<String> ubicationCode = Stream.of("DEPARTMENT", "PROVINCE", "DISTRICT");
        return ubicationCode.anyMatch(element -> element.equalsIgnoreCase(geographicGroupTypeId));
    }


    private static void fillAddressUbigeo(final List<GeographicGroupsBO> geographicGroups, final PersonaBO persona) {
        String department = "";
        String province = "";
        String district = "";
        String ubigeo = "";
        String separationSymbol = "-";

        Map<String, String> mapUbication = new HashMap<>();

        if(!CollectionUtils.isEmpty(geographicGroups)){
            mapUbication = geographicGroups.stream()
                    .filter(element -> filterUbicationCode(Optional.ofNullable(element.getGeographicGroupType()).map(GeographicGroupTypeBO::getId).orElse(StringUtils.EMPTY)))
                    .collect(Collectors.toMap(
                            element -> Optional.ofNullable(element.getGeographicGroupType()).map(GeographicGroupTypeBO::getId).orElse(StringUtils.EMPTY),
                            element -> element.getCode() + separationSymbol + element.getName()));
        }
        String[] arrayDepartment = StringUtils.defaultString(mapUbication.get("DEPARTMENT")).split(separationSymbol);
        String[] arrayProvince = StringUtils.defaultString(mapUbication.get("PROVINCE")).split(separationSymbol);
        String[] arrayDistrict = StringUtils.defaultString(mapUbication.get("DISTRICT")).split(separationSymbol);

        ubigeo = arrayDepartment[0] + arrayProvince[0] + arrayDistrict[0];
        department = arrayDepartment[1];
        province = arrayProvince[1];
        district = arrayDistrict[1];

        persona.setDepartamento(department);
        persona.setProvincia(province);
        persona.setDistrito(district);
        persona.setUbigeo(ubigeo);
    }

    private static String getFullDirectionFrom(String addressViaList, String addressGroupList, String addressNumberVia, StringBuilder stringAddress, PersonaBO persona) {

        String directionForm = null;
        //Logica del primer Grupo : Ubicacion uno
        if(nonNull(addressViaList) && nonNull(addressGroupList) && !NO_EXIST.equals(addressNumberVia)) {
            directionForm = addressViaList.concat(" ").concat(addressNumberVia).concat(", ").concat(addressGroupList)
                    .concat(" ").concat(stringAddress.toString());
        }

        if(nonNull(addressViaList) && nonNull(addressGroupList) && NO_EXIST.equals(addressNumberVia)) {
            directionForm = addressViaList.concat(" ").concat(", ").concat(addressGroupList)
                    .concat(" ").concat(stringAddress.toString());
        }

        if(nonNull(addressViaList) && isNull(addressGroupList) && !NO_EXIST.equals(addressNumberVia)) {
            directionForm = addressViaList.concat(" ").concat(addressNumberVia).concat(" ")
                    .concat(stringAddress.toString());
        }

        if(nonNull(addressViaList) && isNull(addressGroupList) && NO_EXIST.equals(addressNumberVia)) {
            directionForm = addressViaList.concat(" ").concat(stringAddress.toString());
        }
        //Logica del segundo Grupo : Ubicacion dos
        if(isNull(addressViaList) && nonNull(addressGroupList) && !NO_EXIST.equals(addressNumberVia)) {
            directionForm = addressGroupList.concat( " ").concat(addressNumberVia).concat(" ")
                    .concat(stringAddress.toString());
        }

        if(isNull(addressViaList) && nonNull(addressGroupList) && NO_EXIST.equals(addressNumberVia)) {
            directionForm = addressGroupList.concat( " ").concat(stringAddress.toString());
        }

        if(nonNull(directionForm)) {
            persona.setDireccion(directionForm);
        }

        return directionForm;

    }


    private static PersonaBO constructPerson(PolicyDTO requestBody, CustomerBO customer, ProcessPrePolicyDTO processPrePolicyDTO, ApplicationConfigurationService applicationConfigurationService){
        PersonaBO persona = new PersonaBO();
        ContactDetailDTO emailContact = getEmailContact(requestBody);
        ContactDetailDTO mobileContact = getMobileContact(requestBody);

        persona.setTipoDocumento(getDocumentType(requestBody, customer, applicationConfigurationService));
        persona.setNroDocumento(getDocumentNumber(requestBody, customer, applicationConfigurationService));
        persona.setApePaterno(customer.getLastName());
        persona.setApeMaterno(getSecondLastName(customer));
        persona.setNombres(customer.getFirstName());
        persona.setFechaNacimiento(customer.getBirthData().getBirthDate());
        persona.setSexo(getGender(customer));
        persona.setCorreoElectronico(getEmail(emailContact, processPrePolicyDTO));
        persona.setCelular(getMobileNumber(mobileContact, processPrePolicyDTO));
        persona.setTipoPersona(getPersonType(persona).getCode());

        return persona;
    }

    private static ContactDetailDTO getEmailContact(PolicyDTO requestBody) {
        return requestBody.getHolder().getContactDetails().stream()
                .filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(PISDConstants.ContactDetail.EMAIL_TYPE))
                .findFirst()
                .orElse(new ContactDetailDTO());
    }

    private static ContactDetailDTO getMobileContact(PolicyDTO requestBody) {
        return requestBody.getHolder().getContactDetails().stream()
                .filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(PISDConstants.ContactDetail.MOBILE_TYPE))
                .findFirst()
                .orElse(new ContactDetailDTO());
    }

    private static String getDocumentType(PolicyDTO requestBody, CustomerBO customer, ApplicationConfigurationService applicationConfigurationService) {
        return applicationConfigurationService.getProperty(Objects.isNull(requestBody.getHolder()) ? customer.getIdentityDocuments().get(0).getDocumentType().getId() : requestBody.getHolder().getIdentityDocument().getDocumentType().getId());
    }

    private static String getDocumentNumber(PolicyDTO requestBody, CustomerBO customer , ApplicationConfigurationService applicationConfigurationService) {
        if (RUC_ID.equalsIgnoreCase(getDocumentType(requestBody, customer,applicationConfigurationService))) {
            return Objects.nonNull(requestBody.getHolder()) && Objects.nonNull(requestBody.getHolder().getIdentityDocument()) && Objects.nonNull(requestBody.getHolder().getIdentityDocument().getNumber()) ? requestBody.getHolder().getIdentityDocument().getNumber() : StringUtils.EMPTY;
        } else {
            return Objects.nonNull(customer) && !CollectionUtils.isEmpty(customer.getIdentityDocuments()) ? StringUtils.defaultString(customer.getIdentityDocuments().get(0).getDocumentNumber()) : StringUtils.EMPTY;
        }
    }

    private static String getSecondLastName(CustomerBO customer) {
        return Objects.nonNull(customer.getSecondLastName()) && customer.getSecondLastName().length() > PISDConstants.Number.UNO ? customer.getSecondLastName() : "";
    }

    private static String getGender(CustomerBO customer) {
        return Objects.nonNull(customer.getGender()) ? "MALE".equals(customer.getGender().getId()) ? "M" : "F" : "";
    }

    private static String getEmail(ContactDetailDTO emailContact, ProcessPrePolicyDTO processPrePolicyDTO) {
        return Objects.isNull(emailContact.getContact()) ? processPrePolicyDTO.getQuotationEmailDesc() : emailContact.getContact().getAddress();
    }

    private static String getMobileNumber(ContactDetailDTO mobileContact, ProcessPrePolicyDTO processPrePolicyDTO) {
        return Objects.isNull(mobileContact.getContact()) ? processPrePolicyDTO.getQuotationCustomerPhoneDesc() : mobileContact.getContact().getPhoneNumber();
    }

    public static PersonTypeEnum getPersonType(EntidadBO person) {
        if (RUC_ID.equalsIgnoreCase(person.getTipoDocumento())){
            if (StringUtils.startsWith(person.getNroDocumento(), "20")) return PersonTypeEnum.JURIDIC;
            else return PersonTypeEnum.NATURAL_WITH_BUSINESS;
        }
        return PersonTypeEnum.NATURAL;
    }

    public static String getInsuranceBusinessNameFromDB(Map<String, Object> responseQueryGetProductById) {
        return (String) (responseQueryGetProductById.get(ConstantsUtil.FIELD_PRODUCT_SHORT_DESC) != null
                ? responseQueryGetProductById.get(ConstantsUtil.FIELD_PRODUCT_SHORT_DESC)
                : responseQueryGetProductById.get(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue()));
    }

    public static EmisionBO toRequestBodyRimac(PolicyInspectionDTO inspection, String secondParticularDataValue, String channelCode,
                                           String dataId, String saleOffice) {
        EmisionBO rimacRequest = new EmisionBO();

        PayloadEmisionBO payload = new PayloadEmisionBO();

        List<DatoParticularBO> datosParticulares = mapInDatoParticular(secondParticularDataValue, channelCode, dataId, saleOffice);

        payload.setDatosParticulares(datosParticulares);
        payload.setEnvioElectronico(PISDConstants.LETTER_NO);
        payload.setIndCobro(PISDConstants.LETTER_NO);
        payload.setIndValidaciones(PISDConstants.LETTER_NO);

        if(inspection.getIsRequired()) {
            ContactoInspeccionBO contactoInspeccion = new ContactoInspeccionBO();
            contactoInspeccion.setNombre(inspection.getFullName());

            ContactDetailDTO contactEmail = inspection.getContactDetails().stream().
                    filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(PISDConstants.ContactDetail.EMAIL_TYPE)).findFirst().orElse(null);

            ContactDetailDTO contactPhone = inspection.getContactDetails().stream().
                    filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(PISDConstants.ContactDetail.MOBILE_TYPE)).findFirst().orElse(null);

            contactoInspeccion.setCorreo( Objects.nonNull(contactEmail) ? contactEmail.getContact().getAddress() : null);
            contactoInspeccion.setTelefono( Objects.nonNull(contactPhone) ? contactPhone.getContact().getPhoneNumber() : null);

            payload.setContactoInspeccion(contactoInspeccion);
            payload.setIndInspeccion((long) PISDConstants.Number.UNO);
        } else {
            payload.setIndInspeccion((long) PISDConstants.Number.CERO);
        }

        rimacRequest.setPayload(payload);
        return rimacRequest;
    }

    private static List<DatoParticularBO> mapInDatoParticular(String secondParticularDataValue, String channelCode, String dataId, String saleOffice) {
        List<DatoParticularBO> datosParticulares = new ArrayList<>();

        DatoParticularBO primerDatoParticular = new DatoParticularBO();
        primerDatoParticular.setEtiqueta(RBVDInternalConstants.DataParticulars.PARTICULAR_DATA_THIRD_CHANNEL);
        primerDatoParticular.setCodigo(StringUtils.EMPTY);
        primerDatoParticular.setValor(channelCode);
        datosParticulares.add(primerDatoParticular);

        DatoParticularBO segundoDatoParticular = new DatoParticularBO();
        segundoDatoParticular.setEtiqueta(RBVDInternalConstants.DataParticulars.PARTICULAR_DATA_ACCOUNT_DATA);
        segundoDatoParticular.setCodigo(StringUtils.EMPTY);
        segundoDatoParticular.setValor(secondParticularDataValue);
        datosParticulares.add(segundoDatoParticular);

        DatoParticularBO tercerDatoParticular = new DatoParticularBO();
        tercerDatoParticular.setEtiqueta(RBVDInternalConstants.DataParticulars.PARTICULAR_DATA_CERT_BANCO);
        tercerDatoParticular.setCodigo(StringUtils.EMPTY);
        tercerDatoParticular.setValor(dataId);
        datosParticulares.add(tercerDatoParticular);

        DatoParticularBO cuartoDatoParticular = new DatoParticularBO();
        cuartoDatoParticular.setEtiqueta(RBVDInternalConstants.DataParticulars.PARTICULAR_DATA_SALE_OFFICE);
        cuartoDatoParticular.setCodigo(StringUtils.EMPTY);
        cuartoDatoParticular.setValor(saleOffice);
        datosParticulares.add(cuartoDatoParticular);
        return datosParticulares;
    }


}
