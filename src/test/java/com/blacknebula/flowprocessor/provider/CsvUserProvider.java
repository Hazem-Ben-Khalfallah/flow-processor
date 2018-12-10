package com.blacknebula.flowprocessor.provider;

import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.csv.CsvParameters;
import com.blacknebula.flowprocessor.csv.CsvUnivocityParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author hazem
 */
public class CsvUserProvider extends CsvProvider<User> {

    CsvUserProvider(InputStream inputStream, CsvParameters csvParameters) {
        super(inputStream, csvParameters);
    }

    @Override
    protected User transformCsvLineToDto(Context ctx, String[] header, String[] row) {
        return User.newBuilder()
                .setId(row[0])
                .setName(row[1])
                .setAge(row[2])
                .build();
    }
}
