package com.blacknebula.flowprocessor.provider;

import com.blacknebula.flowprocessor.core.Performer;
import com.blacknebula.flowprocessor.csv.CsvBuilder;
import com.blacknebula.flowprocessor.csv.CsvParameters;
import com.blacknebula.flowprocessor.csv.CsvUnivocityParser;
import com.blacknebula.flowprocessor.processor.Processor;
import com.blacknebula.flowprocessor.processor.ProcessorBuilder;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class CsvProviderTest {

    @Test
    public void testCsvProviderForProduct() {
        // given
        final List<User> users = ImmutableList.<User>builder()
                .add(User.newBuilder()
                        .setId("1")
                        .setName("Martin")
                        .setAge("30")
                        .build())
                .add(User.newBuilder()
                        .setId("2")
                        .setName("Kevin")
                        .setAge("20")
                        .build())
                .build();
        final CsvParameters csvParameters = CsvParameters.createCsvParameterWithDefaultValues();
        final String csvContent = convertDtoListToCsvRows(users, csvParameters);
        final InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(CsvUnivocityParser.UTF8_CHARSET));
        final CsvProvider csvProvider = new CsvUserProvider(inputStream, csvParameters);
        final List<User> collectedList = new ArrayList<>();
        final Performer<User> anyPerformer = (ctx, elementWrapper) -> collectedList.add(elementWrapper.getElement());
        final Processor<User> onBoardingFlow = ProcessorBuilder.processorForType(User.class)
                .withProvider(csvProvider)
                .startFlowDefinition()
                .registerPerformer(anyPerformer)
                .endFlowDefinition()
                .build();

        // when
        onBoardingFlow.start();

        // Then
        Assertions.assertThat(users).hasSize(collectedList.size());
        Assertions.assertThat(users).containsAll(collectedList);
    }

    private String convertDtoListToCsvRows(List list, CsvParameters csvParameters) {
        final StringBuilder result = new StringBuilder();
        if (list != null && list.size() > 0) {
            if (csvParameters.getHeaderLineCount() > 0) {
                result.append(CsvBuilder.getCsvRow(getHeadersFromDto(list.get(0)), csvParameters.getFieldDelimiter().toString(), csvParameters.getQuote().toString()))
                        .append(csvParameters.getLineDelimiter());
            }
            list.forEach(object ->
                    result.append(CsvBuilder.getCsvRow(getValuesFromDto(object), csvParameters.getFieldDelimiter().toString(), csvParameters.getQuote().toString()))
                            .append(csvParameters.getLineDelimiter()));
        }
        return result.toString();
    }

    private List<String> getHeadersFromDto(Object dto) {
        List<String> headers = new ArrayList<>();
        try {
            final Field[] fields = getClassFields(dto);
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getType() == List.class) {
                    List<Object> values = (List<Object>) fields[i].get(dto);
                    if (values != null && values.size() > 0) {
                        for (int j = 0; j < values.size(); j++) {
                            headers.add(fields[i].getName());
                        }
                    }
                } else {
                    headers.add(fields[i].getName());
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return headers;
    }


    private List<String> getValuesFromDto(Object dto) {
        final List<String> allValues = new ArrayList<>();
        final Field[] fields = getClassFields(dto);
        try {
            List<Object> fieldValues;
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getType() == List.class) {
                    fieldValues = (List<Object>) fields[i].get(dto);
                    if (fieldValues != null && fieldValues.size() > 0) {
                        List<String> stringValues = (List<String>) fields[i].get(dto);
                        for (int j = 0; j < fieldValues.size(); j++) {
                            allValues.add(stringValues.get(j) == null ? "" : stringValues.get(j));
                        }

                    }
                } else {
                    allValues.add(fields[i].get(dto) == null ? "" : fields[i].get(dto).toString());
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return allValues;
    }

    private Field[] getClassFields(Object obj) {
        List<Field> result = new ArrayList<>();
        Field[] allFields = obj.getClass().getFields();
        for (int i = 0; i < allFields.length; i++) {
            Field currentField = allFields[i];
            // get only public field, and avoid jacoco fields
            if (/* currentField.isAccessible() && */currentField.getName().indexOf("$") == -1) {
                result.add(currentField);
            }
        }
        return result.toArray(new Field[result.size()]);
    }


}
