package nextstep.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public final class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> List<T> toDtoList(String input, Class<T> type) throws Exception {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper
                .readValue(input, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
    }
}
