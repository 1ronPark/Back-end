package umc.lightup.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class CustomFunctionContributor implements FunctionContributor {
    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry()
                .register("bitand", new StandardSQLFunction("bitand", StandardBasicTypes.INTEGER));
        functionContributions.getFunctionRegistry()
                .register("json_objectagg", new StandardSQLFunction("json_objectagg", StandardBasicTypes.STRING));
        functionContributions.getFunctionRegistry()
                .register("json_arrayagg", new StandardSQLFunction("json_arrayagg", StandardBasicTypes.STRING));
    }
}