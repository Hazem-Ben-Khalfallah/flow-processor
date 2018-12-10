package com.blacknebula.flowprocessor.csv;

import com.blacknebula.flowprocessor.exception.CustomErrorCode;
import com.blacknebula.flowprocessor.exception.CustomException;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.TextParsingException;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CsvUnivocityParser {

    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private final InputStream inputStream;
    private Logger LOGGER = LoggerFactory.getLogger(CsvUnivocityParser.class);
    private CsvParameters csvParameters;

    public CsvUnivocityParser(InputStream inputStream, CsvParameters csvParameters) {
        this.inputStream = inputStream;
        this.csvParameters = csvParameters;
    }

    static Set<Integer> getIndexIntervals(ParseLimit parseLimit) {
        return IntStream.range(parseLimit.getOffset(), parseLimit.getOffset() + parseLimit.getSize()) //
                .boxed() //
                .collect(Collectors.toSet());
    }

    public void parseCsv(ParseLimit parseLimit, Consumer<CsvElement> csvConsumer, Supplier<Boolean> isInterrupted) throws CustomException {
        if (inputStream == null) {
            throw new CustomException(CustomErrorCode.BAD_ARGS, "input stream cannot be null");
        }

        CsvParser parser = null;
        Reader reader = null;
        try {
            reader = new InputStreamReader(inputStream, UTF8_CHARSET);
            final CsvParserSettings parsingParameters = constructParsingParameters(csvParameters);
            parser = new CsvParser(parsingParameters);
            parser.beginParsing(reader);

            // retrieve header
            int headerCount = csvParameters.getHeaderLineCount();
            String[] header = null;
            if (headerCount > 0) {
                if (headerCount > 1) {
                    int shift = headerCount;
                    while (shift > 1) {
                        parser.parseNext();
                        shift--;
                    }
                }
                header = parser.parseNext();
            }

            final boolean parseAll = Objects.isNull(parseLimit);
            final Set<Integer> indexSet = parseAll ? new HashSet<>() : getIndexIntervals(parseLimit);

            // parse file
            int index = 0;
            while (true) {
                boolean unparsableRow = false;
                String[] row;
                try {
                    row = parser.parseNext();
                } catch (TextParsingException e) {
                    row = new String[0];
                    unparsableRow = true;
                }

                if (row == null)
                    break;

                if (parseAll || indexSet.contains(index)) {
                    csvConsumer.accept(new CsvElement(header, row, index, unparsableRow));
                }
                indexSet.remove(index);
                index++;
                // no more lines to parse
                if (!parseAll && indexSet.isEmpty()) {
                    break;
                }

                if (isInterrupted.get()) {
                    break;
                }
            }
        } catch (TextParsingException e) {
            LOGGER.error("Error while processing a row ", e);
            throw new CustomException(CustomErrorCode.BAD_ARGS, "Malformed csv file. Verify delimiters, quotes and escape sequences");
        } finally {
            if (parser != null) {
                parser.stopParsing();
            }
        }
        // Resources are automatically closed by Univocity
    }

    private CsvParserSettings constructParsingParameters(CsvParameters csvParameter) {
        final CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setHeaderExtractionEnabled(false);

        parserSettings.setRowProcessor(new RowProcessor() {
            @Override
            public void processStarted(ParsingContext context) {
            }

            @Override
            public void rowProcessed(String[] row, ParsingContext context) {
            }

            @Override
            public void processEnded(ParsingContext context) {
            }
        });
        parserSettings.setIgnoreLeadingWhitespaces(false);
        parserSettings.setIgnoreTrailingWhitespaces(false);
        parserSettings.setEmptyValue("");
        CsvFormat format = parserSettings.getFormat();
        final String recordDelimiter = StringEscapeUtils.unescapeJava(csvParameter.getLineDelimiter());
        format.setLineSeparator("\n");
        format.setDelimiter(csvParameter.getFieldDelimiter());
        format.setQuote(csvParameter.getQuote());
        format.setQuoteEscape(csvParameter.getEscape());
        format.setCharToEscapeQuoteEscaping(csvParameter.getEscape());
        // disable comment due to skip univocity issue
        format.setComment('\u0000');
        return parserSettings;
    }

}