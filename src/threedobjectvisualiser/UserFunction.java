package threedobjectvisualiser;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Function;

/**
 * Represents a numeric double -> double function interpreted by a user-given string.
 */
public class UserFunction implements Function<Double, Double> {
    private final String functionString;
    private final static ScriptEngineManager manager = new ScriptEngineManager();
    private final static ScriptEngine engine = manager.getEngineByName("js");

    public UserFunction(String functionString) {
        this.functionString = functionString;
    }

    /**
     * Applies a given input to the functionString and evaluates result.
     * @param input input of function
     * @return output of interpreted function
     */
    public Double apply(Double input) {
        try {
            StringBuilder evalString = new StringBuilder();
            for (char character : functionString.toCharArray()) {
                if (character == 'x')
                    evalString.append(input);
                else
                    evalString.append(character);
            }

           return (Double) engine.eval(evalString.toString());
        } catch (ScriptException e) {
            System.err.println("Error calculating function.");
            e.printStackTrace();
        }

        return 0.0;
    }
}
