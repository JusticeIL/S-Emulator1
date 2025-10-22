package dto;

import java.util.List;
import java.util.Set;

public class ExecutionPayload {
    private String architecture;
    private Set<VariableDTO> arguments; // או List<VariableDTO> אם זה פועל בשרת
    // (נניח ש-List<VariableDTO> עובד עבור GSON)

    // Getters and setters...
    // נניח לצורך הפשטות ש-GSON יכול לנתח ישירות ל-List<VariableDTO>
    // או ניצור List<VariableDTO> במקום List<Map<String, Object>> אם השרת מאפשר

    public String getArchitecture() {
        return architecture;
    }

    public Set<VariableDTO> getArguments() {
        // המרה מ-Map ל-VariableDTO אם נשלח כך
        // אם ה-Client שולח אוסף של VariableDTO, נניח שהג'ייסון תקין
        return arguments; // נניח ש-GSON מנתח נכון
    }

    public ExecutionPayload(Set<VariableDTO> arguments,String architecture) {
        this.architecture = architecture;
        this.arguments = arguments;
    }
}
