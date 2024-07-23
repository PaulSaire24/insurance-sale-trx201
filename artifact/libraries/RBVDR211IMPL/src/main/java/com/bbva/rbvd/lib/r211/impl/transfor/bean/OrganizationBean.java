package com.bbva.rbvd.lib.r211.impl.transfor.bean;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EntidadBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.OrganizacionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.ContactTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.PersonTypeEnum;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Endorsement;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrganizationBean {

    public static void setOrganization(EmisionBO emision, PolicyDTO requestBody, CustomerListASO customerList, ContextEmission processPrePolicy){
        PersonaBO persona = emision.getPayload().getAgregarPersona().getPersona().get(0);
        CustomerBO customer = customerList.getData().get(0);

        List<OrganizacionBO> organizaciones = mapOrganizations(processPrePolicy.getListBusinessesASO().getData().get(0), persona, customer, requestBody);
        emision.getPayload().getAgregarPersona().setOrganizacion(organizaciones);
        emision.getPayload().getAgregarPersona().setPersona(null);
    }

    public static List<OrganizacionBO> mapOrganizations(final BusinessASO business, PersonaBO persona, CustomerBO customer, PolicyDTO requestBody) {
        List<OrganizacionBO> organizaciones = new ArrayList<>();

        ContactDetailDTO correoSelect= requestBody.getHolder().getContactDetails().stream().
                filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals("EMAIL")).findFirst().orElse(new ContactDetailDTO());

        ContactDetailDTO celularSelect= requestBody.getHolder().getContactDetails().stream().
                filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals("PHONE")).findFirst().orElse(new ContactDetailDTO());


        String fijo = customer.getContactDetails().stream().filter(
                        d -> ContactTypeEnum.PHONE_NUMBER.getValue().equals(d.getContactType().getId())).findFirst().
                orElse(new ContactDetailsBO()).getContact();
        String celular = customer.getContactDetails().stream().filter(
                        d -> ContactTypeEnum.MOBILE_NUMBER.getValue().equals(d.getContactType().getId())).findFirst().
                orElse(new ContactDetailsBO()).getContact();
        String correo = customer.getContactDetails().stream().filter(
                        d -> ContactTypeEnum.EMAIL.getValue().equals(d.getContactType().getId())).findFirst().
                orElse(new ContactDetailsBO()).getContact();

        correo = StringUtils.isNotBlank(correoSelect.getContact().getAddress()) ?correoSelect.getContact().getAddress() : correo;
        celular = StringUtils.isNotBlank(celularSelect.getContact().getPhoneNumber()) ? celularSelect.getContact().getPhoneNumber() : celular;

        int[] intArray = new int[]{8, 9, 23};
        for (int i = 0; i < intArray.length; i++) {
            OrganizacionBO organizacion = new OrganizacionBO();
            organizacion.setDireccion(persona.getDireccion());
            organizacion.setRol(intArray[i]);
            organizacion.setTipoDocumento("R");
            organizacion.setNroDocumento(business.getBusinessDocuments().get(0).getDocumentNumber());
            organizacion.setRazonSocial(business.getLegalName());
            organizacion.setNombreComercial(business.getLegalName());
            if(Objects.nonNull(business.getFormation())) {
                organizacion.setPaisOrigen(business.getFormation().getCountry().getName());
                organizacion.setFechaConstitucion(business.getFormation().getDate());
            } else {
                organizacion.setPaisOrigen("PERU");
                organizacion.setFechaConstitucion(null);
            }
            organizacion.setFechaInicioActividad(Objects.isNull(business.getAnnualSales()) ? null : business.getAnnualSales().getStartDate());
            organizacion.setTipoOrganizacion(Objects.isNull(business.getBusinessGroup()) ? StringUtils.EMPTY : business.getBusinessGroup().getId());
            organizacion.setGrupoEconomico(RBVDInternalConstants.DataParticulars.TAG_OTHERS);
            organizacion.setCiiu(Objects.isNull(business.getEconomicActivity()) ? StringUtils.EMPTY  : business.getEconomicActivity().getId());
            organizacion.setTelefonoFijo(fijo);
            organizacion.setCelular(celular);
            organizacion.setCorreoElectronico(correo);
            organizacion.setDistrito(persona.getDistrito());
            organizacion.setProvincia(persona.getProvincia());
            organizacion.setDepartamento(persona.getDepartamento());
            organizacion.setUbigeo(persona.getUbigeo());
            organizacion.setNombreVia(persona.getNombreVia());
            organizacion.setTipoVia(persona.getTipoVia());
            organizacion.setNumeroVia(persona.getNumeroVia());
            organizacion.setTipoPersona(getPersonType(organizacion).getCode());
            organizaciones.add(organizacion);
        }
        return organizaciones;
    }

    public static PersonTypeEnum getPersonType(EntidadBO person) {
        if (Endorsement.RUC_ID.equalsIgnoreCase(person.getTipoDocumento())){
            if (StringUtils.startsWith(person.getNroDocumento(), "20")) return PersonTypeEnum.JURIDIC;
            else return PersonTypeEnum.NATURAL_WITH_BUSINESS;
        }
        return PersonTypeEnum.NATURAL;
    }

}
