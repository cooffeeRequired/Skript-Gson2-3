/**
 * Copyright CooffeeRequired, and SkriptLang team and contributors
 */
package cz.coffee.skriptgson.util;

import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;

import java.lang.reflect.Field;

public class VariableUtil {
    static {
        Field _VARIABLE_NAME = null;
        try {
            _VARIABLE_NAME = Variable.class.getDeclaredField("name");
            _VARIABLE_NAME.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        VARIABLE_NAME = _VARIABLE_NAME;
    }

    private static final Field VARIABLE_NAME;

    public static VariableString getVarName(Variable<?> var) {
        try {
            return (VariableString) VARIABLE_NAME.get(var);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
