package program;

import XMLandJaxB.SFunction;
import XMLandJaxB.SFunctions;
import XMLandJaxB.SInstructions;

import java.io.FileNotFoundException;

public class Function extends Program {

    private final String userString;

    public Function(SFunction sFunction) throws FileNotFoundException {
        super(sFunction.getSInstructions(), sFunction.getName());
        this.userString = sFunction.getUserString();
    }

    public String getUserString() {
        return userString;
    }
}
