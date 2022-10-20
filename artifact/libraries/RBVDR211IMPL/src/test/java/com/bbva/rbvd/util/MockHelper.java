package com.bbva.rbvd.util;

import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEMSALW4;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEMSALW5;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEMSALWU;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEWUResponse;

public class MockHelper {
    public static PEWUResponse getSearchInHostByCustomerIdErrorMock(){
        PEWUResponse out = new PEWUResponse();
        out.setHostAdviceCode("ERROR002");
        out.setHostMessage("ERROR DESDE CICS");
        return out;
    }
    public static PEWUResponse getExecuteSearchInHostByCustomerIdMock() {
        PEWUResponse out = new PEWUResponse();
        PEMSALWU pemsalwu = new PEMSALWU();
        PEMSALW4 pemsalw4 = new PEMSALW4();
        PEMSALW5 pemsalw5 = new PEMSALW5();
        pemsalwu.setTdoi("L");
        pemsalwu.setNdoi("99999999");
        pemsalwu.setNombres("Albert");
        pemsalwu.setApellip("Rodriguez");
        pemsalwu.setApellim("Mendoza");
        pemsalwu.setFechan("2019-08-21");
        pemsalwu.setSexo("M");
        pemsalwu.setCodigod("LIMA");
        pemsalwu.setCodigop("COVLAS");
        pemsalwu.setCodigod("MANZANAS");
        pemsalwu.setNroext1("15");
        pemsalwu.setNroint1("15");
        pemsalwu.setManzana("C");
        pemsalwu.setLote("3");
        pemsalwu.setIdendi1("ALM");
        pemsalwu.setIdendi2("AHH");
        pemsalwu.setIdencon(null);
        pemsalwu.setTipocon(null);
        pemsalwu.setContact(null);
        pemsalwu.setIdenco2("C001956783678");
        pemsalwu.setTipoco2("MV");
        pemsalwu.setContac2("956783678");
        pemsalwu.setIdenco3("EMAIL");
        pemsalwu.setTipoco3("MA");
        pemsalwu.setContac3("albert.rod@BBVA.COM");
        pemsalw4.setDesdept("LIMA");
        pemsalw4.setDesprov("COVLAS");
        pemsalw4.setDesdist("MANZANAS");
        pemsalw5.setDescmco("SIN TELEFONO FIJO");
        pemsalw5.setDescmc1("TELEFONO MOVIL");
        pemsalw5.setDescmc2("PERSONAL");
        out.setPemsalwu(pemsalwu);
        out.setPemsalw4(pemsalw4);
        out.setPemsalw5(pemsalw5);
        return out;
    }
    public static PEWUResponse getExecuteSearchInHostByCustomerIdWithRUCMock() {
        PEWUResponse out = new PEWUResponse();
        PEMSALWU pemsalwu = new PEMSALWU();
        PEMSALW4 pemsalw4 = new PEMSALW4();
        PEMSALW5 pemsalw5 = new PEMSALW5();
        pemsalwu.setTdoi("R");
        pemsalwu.setNdoi("20999999991");
        pemsalwu.setNombres("Albert");
        pemsalwu.setApellip("Rodriguez");
        pemsalwu.setApellim("Mendoza");
        pemsalwu.setFechan("2019-08-21");
        pemsalwu.setSexo("M");
        pemsalwu.setCodigod("LIMA");
        pemsalwu.setCodigop("COVLAS");
        pemsalwu.setCodigod("MANZANAS");
        pemsalwu.setNroext1("15");
        pemsalwu.setNroint1("15");
        pemsalwu.setManzana("C");
        pemsalwu.setLote("3");
        pemsalwu.setIdendi1("ALM");
        pemsalwu.setIdendi2("AHH");
        pemsalwu.setIdencon(null);
        pemsalwu.setTipocon(null);
        pemsalwu.setContact(null);
        pemsalwu.setIdenco2("C001956783678");
        pemsalwu.setTipoco2("MV");
        pemsalwu.setContac2("956783678");
        pemsalwu.setIdenco3("EMAIL");
        pemsalwu.setTipoco3("MA");
        pemsalwu.setContac3("albert.rod@BBVA.COM");
        pemsalw4.setDesdept("LIMA");
        pemsalw4.setDesprov("COVLAS");
        pemsalw4.setDesdist("MANZANAS");
        pemsalw5.setDescmco("SIN TELEFONO FIJO");
        pemsalw5.setDescmc1("TELEFONO MOVIL");
        pemsalw5.setDescmc2("PERSONAL");
        out.setPemsalwu(pemsalwu);
        out.setPemsalw4(pemsalw4);
        out.setPemsalw5(pemsalw5);
        return out;
    }
}
