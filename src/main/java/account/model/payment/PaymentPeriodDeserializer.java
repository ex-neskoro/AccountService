package account.model.payment;

import account.exception.WrongDateException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PaymentPeriodDeserializer extends StdDeserializer<YearMonth> {

    public PaymentPeriodDeserializer() {
        super(YearMonth.class);
    }

    @Override
    public YearMonth deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        YearMonth date;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-yyyy");
            date = YearMonth.parse(p.getText(), fmt);
        } catch (DateTimeParseException e) {
            throw new WrongDateException();
        }
        return date;
    }
}
