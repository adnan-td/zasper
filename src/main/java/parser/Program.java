package parser;
import java.util.ArrayList;
import com.google.gson.Gson;

public class Program extends Statement {
    ArrayList<Statement> body = new ArrayList<>();

    public Program() {
        super(NodeType.Program);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

