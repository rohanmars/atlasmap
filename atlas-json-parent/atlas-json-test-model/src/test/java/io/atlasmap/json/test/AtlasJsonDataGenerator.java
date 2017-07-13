package io.atlasmap.json.test;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class AtlasJsonDataGenerator {

    private ObjectMapper mapper = null;
    
    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    @After
    public void tearDown() throws Exception {
        mapper = null;
    }

    @Test
    public void testGenerateOrderList() throws Exception {
        BaseOrderList orderList = AtlasJsonUtil.generateOrderListClass(SourceOrderList.class, SourceOrder.class, SourceAddress.class, SourceContact.class);
        SourceOrderList sourceOrderList = (SourceOrderList)orderList;      
        mapper.writeValue(new File("target/list-rooted-sourceorderlist.json"), sourceOrderList);
    }

}