package account.model.payment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PaymentSalarySerializer extends StdSerializer<Long> {

    protected PaymentSalarySerializer(Class<Long> t) {
        super(t);
    }

    protected PaymentSalarySerializer(JavaType type) {
        super(type);
    }

    protected PaymentSalarySerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected PaymentSalarySerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString("%d dollar(s) %d cent(s)".formatted(value / 100, value % 100));
    }
}
