package server;
import java.util.*;

public class Function {

    class FunctionInfo {

        public String cmd;
        public String cmdFull;
        public int cmdLength_min;
        public int cmdLength_max;
        public int[] permission;

        public FunctionInfo(String cmd, String cmdFull, int cmdLength_min, int cmdLength_max, int[] permission) {
            this.cmd = cmd;
            this.cmdFull = cmdFull;
            this.cmdLength_min = cmdLength_min;
            this.cmdLength_max = cmdLength_max;
            this.permission = Arrays.copyOf(permission, 4);
        }
    }

    private ArrayList<FunctionInfo> functionList;
    private Map<String, Integer> indexMap;
    private String[] roles = {"guest", "admin", "studio", "stream"}; // permission groups

    public Function() {
        functionList = new ArrayList<FunctionInfo>();
        indexMap = new HashMap<String, Integer>();
        String cmd;
        String cmdFull;
        int cmdLength_min;
        int cmdLength_max;
        int[] permission;
        cmd = "stop";
        cmdFull = "stop";
        cmdLength_min = 1;
        cmdLength_max = 1;
        permission = new int[] {1,1,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "help";
        cmdFull = "help";
        cmdLength_min = 1;
        cmdLength_max = 1;
        permission = new int[] {1,1,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "login";
        cmdFull = "login,<user>,<password>";
        cmdLength_min = 3;
        cmdLength_max = 3;
        permission = new int[] {1,0,0,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "register";
        cmdFull = "register,<user>,<password>,<role>,[expiration]";
        cmdLength_min = 4;
        cmdLength_max = 5;
        permission = new int[] {0,1,0,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "logout";
        cmdFull = "logout";
        cmdLength_min = 1;
        cmdLength_max = 1;
        permission = new int[] {0,1,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "change_password";
        cmdFull = "change_password,<old_password>,<new_password>";
        cmdLength_min = 3;
        cmdLength_max = 3;
        permission = new int[] {0,1,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "create_demo";
        cmdFull = "create_demo,<short name>,<long name>,<number of accounts>";
        cmdLength_min = 4;
        cmdLength_max = 4;
        permission = new int[] {0,1,0,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "create_studio";
        cmdFull = "create_studio,<short name>,<long name>";
        cmdLength_min = 3;
        cmdLength_max = 3;
        permission = new int[] {0,1,0,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "create_event";
        cmdFull = "create_event,<type>,<name>,<year produced>,<duration>,<studio>,<license fee>";
        cmdLength_min = 7;
        cmdLength_max = 7;
        permission = new int[] {0,1,1,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "create_stream";
        cmdFull = "create_stream,<short name>,<long name>,<subscription price>";
        cmdLength_min = 4;
        cmdLength_max = 4;
        permission = new int[] {0,1,0,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "offer_movie";
        cmdFull = "offer_movie,<streaming service>,<movie name>,<year produced>";
        cmdLength_min = 4;
        cmdLength_max = 4;
        permission = new int[] {0,1,0,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "offer_ppv";
        cmdFull = "offer_ppv,<streaming service>,<pay-per-view name>,<year produced>,<viewing price>";
        cmdLength_min = 5;
        cmdLength_max = 5;
        permission = new int[] {0,1,0,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "watch_event";
        cmdFull = "watch_event,<demographic group>,<percentage>,<streaming service>,<event name>,<year produced>";
        cmdLength_min = 6;
        cmdLength_max = 6;
        permission = new int[] {0,1,0,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "next_month";
        cmdFull = "next_month";
        cmdLength_min = 1;
        cmdLength_max = 1;
        permission = new int[] {0,1,0,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "display_demo";
        cmdFull = "display_demo,<short name>";
        cmdLength_min = 2;
        cmdLength_max = 2;
        permission = new int[] {0,1,0,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "display_stream";
        cmdFull = "display_stream,[short name]";
        cmdLength_min = 1;
        cmdLength_max = 2;
        permission = new int[] {0,1,0,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "display_studio";
        cmdFull = "display_studio,[short name]";
        cmdLength_min = 1;
        cmdLength_max = 2;
        permission = new int[] {0,1,1,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "display_events";
        cmdFull = "display_events";
        cmdLength_min = 1;
        cmdLength_max = 1;
        permission = new int[] {0,1,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "display_offers";
        cmdFull = "display_offers";
        cmdLength_min = 1;
        cmdLength_max = 1;
        permission = new int[] {0,1,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "display_time";
        cmdFull = "display_time";
        cmdLength_min = 1;
        cmdLength_max = 1;
        permission = new int[] {1,1,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "display_transactions";
        cmdFull = "display_transactions";
        cmdLength_min = 1;
        cmdLength_max = 1;
        permission = new int[] {0,0,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "display_users";
        cmdFull = "display_users";
        cmdLength_min = 1;
        cmdLength_max = 1;
        permission = new int[] {0,1,0,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "update_demo";
        cmdFull = "update_demo,<short name>,<long name>,<number of accounts>";
        cmdLength_min = 4;
        cmdLength_max = 4;
        permission = new int[] {0,1,0,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "update_event";
        cmdFull = "update_event,<name>,<year produced>,<duration>,<license fee>";
        cmdLength_min = 5;
        cmdLength_max = 5;
        permission = new int[] {0,1,1,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "update_stream";
        cmdFull = "update_stream,<short name>,<long name>,<subscription price>";
        cmdLength_min = 4;
        cmdLength_max = 4;
        permission = new int[] {0,1,0,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "retract_movie";
        cmdFull = "retract_movie,<streaming service>,<movie name>,<movie year>";
        cmdLength_min = 4;
        cmdLength_max = 4;
        permission = new int[] {0,1,1,0};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "archive_transaction";
        cmdFull = "archive_transaction,<year>,<month>";
        cmdLength_min = 3;
        cmdLength_max = 3;
        permission = new int[] {0,0,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
        cmd = "unarchive_transaction";
        cmdFull = "unarchive_transaction,<year>,<month>";
        cmdLength_min = 3;
        cmdLength_max = 3;
        permission = new int[] {0,0,1,1};
        create_function(cmd, cmdFull, cmdLength_min, cmdLength_max, permission);
    }

    public int isvalid(String[] tokens, String role) {
        if (!indexMap.containsKey(tokens[0]))
            return 1;
        int index = indexMap.get(tokens[0]);
        if (tokens.length < functionList.get(index).cmdLength_min || tokens.length > functionList.get(index).cmdLength_max)
            return 2;
        int idx_role = Arrays.asList(roles).indexOf(role);
        if (idx_role == -1 || functionList.get(index).permission[idx_role] == 0)
            return 3;
        return 0;
    }

    public void create_function(String cmd, String cmfFull, int cmdLength_min, int cmdLength_max, int[] permission) {
        if (!indexMap.containsKey(cmd)) {
            indexMap.put(cmd, functionList.size());
            functionList.add(new FunctionInfo(cmd, cmfFull, cmdLength_min, cmdLength_max, permission));
        }
    }

    public String get_help(String role) {
        int index = Arrays.asList(roles).indexOf(role);
        if (index == -1)
            return "";
        String results = "";
        for (int i=0; i<functionList.size(); i++) {
            if (functionList.get(i).permission[index] == 1) {
                results += functionList.get(i).cmdFull + "\r\n";
            }
        }
        return results;
    }

}
