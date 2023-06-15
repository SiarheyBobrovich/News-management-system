package ru.clevertec.news.util.aggregator;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import ru.clevertec.news.data.CreateCommentDto;

public class CreateCommentDtoAggregator implements ArgumentsAggregator {

    @Override
    public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        String text = accessor.getString(0);
        Long newsId = accessor.getLong(1);

        return new CreateCommentDto(text, newsId);
    }
}
