package ru.clevertec.news.util.aggregator;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import ru.clevertec.news.data.CreateNewsDto;

public class CreateNewsDtoAggregator implements ArgumentsAggregator {

    @Override
    public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        String title = accessor.getString(0);
        String text = accessor.getString(1);

        return new CreateNewsDto(title, text);
    }
}
