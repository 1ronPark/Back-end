package umc.lightup.config;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueryDSLTemplate {

    public Expression<String> jsonObjectAgg(Object... args) {
        return getStringTemplate("json_objectagg", args);
    }

    public Expression<String> jsonObject(Object... args) {
        return getStringTemplate("json_object", args);
    }

    public Expression<String> jsonArrayAgg(Object... args) {
        return getStringTemplate("json_arrayagg", args);
    }

    public StringTemplate getStringTemplate(String functionName, Object... args) {
        StringBuilder builder = new StringBuilder("function('")
                .append(functionName)
                .append('\'');
        for (int i = 0; i != args.length; i++)
            builder.append(", {").append(i).append('}');
        return Expressions.stringTemplate(
                builder.append(')').toString(),
                args
        );
    }
}