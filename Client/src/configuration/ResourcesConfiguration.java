package configuration;

public class ResourcesConfiguration {
    public static final String BASE_URL = "http://localhost:8080/S-emulator";

    public static final String USER_RESOURCE = "/api/user";
    public static final String GET_ALL_USERS_RESOURCE = "/api/users";
    public static final String PROGRAM_RESOURCE = "/api/program";
    public static final String GET_ALL_PROGRAMS_RESOURCE = "/api/shared/programs";
    public static final String GET_ALL_FUNCTIONS_RESOURCE = "/api/shared/functions";
    public static final String SET_ACTIVE_PROGRAM_RESOURCE = "/api/program/active";
    public static final String ADD_CREDITS_RESOURCE = "/api/user/credit";
    public static final String BREAKPOINT_RESOURCE = "/api/program/debug/breakpoint";
    public static final String EXPAND_PROGRAM_RESOURCE = "/api/program/expand";
    public static final String RUN_PROGRAM_RESOURCE = "/api/program/execute";
    public static final String START_DEBUG_RESOURCE = "/api/program/debug";
    public static final String RESUME_DEBUG_RESOURCE = "/api/program/debug/resume";
    public static final String STEP_OVER_RESOURCE = "/api/program/debug/stepover";
    public static final String STOP_DEBUG_RESOURCE = "/api/program/debug/stop";
}