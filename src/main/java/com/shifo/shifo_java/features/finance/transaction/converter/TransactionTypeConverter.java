package com.shifo.shifo_java.features.finance.transaction.converter;

import com.shifo.shifo_java.features.finance.transaction.model.TransactionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionTypeConverter implements Converter<String, TransactionType> {

    @Override
    public TransactionType convert(String source) {
        return TransactionType.valueOf(source.toUpperCase());
    }
}
