package ru.yandex.practicum.kafka.telemetry.serialization;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import java.util.Locale;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final Schema schema;
    private final DecoderFactory decoderFactory;

    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.schema = schema;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) return null;

        try {
            int start = 0;
            if (data.length > 5 && data[0] == 0x0) start = 5;

            int length = data.length - start;
            if (length <= 0)
                throw new SerializationException("Слишком короткий payload после удаления префикса. length=" + length);

            DatumReader<T> reader = new SpecificDatumReader<>(schema);
            return reader.read(null, decoderFactory.binaryDecoder(data, start, length, null));
        } catch (Exception e) {
            throw new SerializationException(
                    "Ошибка десериализации данных. firstBytes(hex)=" + toHexSample(data, 64), e);
        }
    }

    private static String toHexSample(byte[] data, int max) {
        if (data == null) return "<null>";
        StringBuilder sb = new StringBuilder();
        int len = Math.min(data.length, max);
        for (int i = 0; i < len; i++) sb.append(String.format(Locale.ROOT, "%02x", data[i]));
        if (data.length > max) sb.append("...");
        return sb.toString();
    }
}