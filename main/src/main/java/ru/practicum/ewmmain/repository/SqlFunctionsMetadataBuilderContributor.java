package ru.practicum.ewmmain.repository;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Component;

@Component
public class SqlFunctionsMetadataBuilderContributor
        implements MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction(
                "distance",
                new SQLFunctionTemplate(
                        StandardBasicTypes.FLOAT,
                        "distance(?1, ?2, ?3, ?4)"
                )
        );
    }
}