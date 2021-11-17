package threedobjectvisualiser;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class UserFunction {
    private final String functionString;
    private final static ScriptEngineManager manager = new ScriptEngineManager();
    private final static ScriptEngine engine = manager.getEngineByName("js");

    public UserFunction(String functionString) {
        this.functionString = functionString;
    }

    public int apply(double input) {
        try {
            StringBuilder evalString = new StringBuilder();
            for (char character : functionString.toCharArray()) {
                if (character == 'x')
                    evalString.append(input);
                else
                    evalString.append(character);
            }

            return (int) Math.round((Double) engine.eval(evalString.toString()));
        } catch (ScriptException e) {
            System.err.println("Error calculating function.");
            e.printStackTrace();
        }

        return 0;
    }
}
