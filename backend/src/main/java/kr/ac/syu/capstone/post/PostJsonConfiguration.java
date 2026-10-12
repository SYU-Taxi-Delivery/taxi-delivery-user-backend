package kr.ac.syu.capstone.post;

import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.*;

@Configuration
public class PostJsonConfiguration {
    @Bean Jackson2ObjectMapperBuilderCustomizer strictJsonTypes() {
        return builder -> builder.postConfigurer(mapper -> {
            // ALLOW_COERCION_OF_SCALARS alone still accepts boolean/number -> String.
            for (CoercionInputShape shape : new CoercionInputShape[] {
                    CoercionInputShape.Boolean, CoercionInputShape.Integer, CoercionInputShape.Float }) {
                mapper.coercionConfigFor(LogicalType.Textual).setCoercion(shape, CoercionAction.Fail);
                mapper.coercionConfigFor(LogicalType.DateTime).setCoercion(shape, CoercionAction.Fail);
            }
        });
    }
}
